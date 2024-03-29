package com.registar.hotel.userService.controller;

import com.registar.hotel.userService.entity.Guest;
import com.registar.hotel.userService.exception.GuestNotFoundException;
import com.registar.hotel.userService.model.BookingDTO;
import com.registar.hotel.userService.model.GuestDTO;
import com.registar.hotel.userService.service.FileUploadService;
import com.registar.hotel.userService.service.GuestService;
import com.registar.hotel.userService.service.S3Service;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private FileUploadService fileUploadService;
    private GuestService guestService;
    private S3Service s3Service;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    public FileUploadController(FileUploadService fileUploadService, GuestService guestService, S3Service s3Service) {
        this.fileUploadService = fileUploadService;
        this.guestService = guestService;
        this.s3Service = s3Service;
    }

    @PostMapping("/guest/files")
    public ResponseEntity<GuestDTO> createGuestWithGovID(@RequestParam("govtId") MultipartFile govtId, @RequestParam("picture") MultipartFile picture, @RequestPart GuestDTO guestDTO) {
        // Upload the files in parallel
        CompletableFuture<String> govtIdUploadFuture = uploadFileAsync(govtId,guestDTO.getName(),guestDTO.getMobileNo());

        CompletableFuture<String> picUploadFuture = uploadFileAsync(picture,guestDTO.getName(),guestDTO.getMobileNo());
        // Wait for both uploads to complete
        CompletableFuture.allOf(govtIdUploadFuture, picUploadFuture).join();


        guestDTO.setPictureFilePath(picUploadFuture.join());
        guestDTO.setGovtIDFilePath(govtIdUploadFuture.join());

        return ResponseEntity.ok(guestDTO);
    }

    // Helper method to upload file asynchronously
    private CompletableFuture<String> uploadFileAsync(MultipartFile file, String name, String mobileNo) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return s3Service.uploadFileForGuest(file, name, mobileNo);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @PostMapping("/guest/picture")
    public ResponseEntity<String> createGuestWithPicture(@RequestParam("file") MultipartFile file, @RequestBody GuestDTO guestDTO) {
        try {
            // Upload the file for the guest
            String uploadedFilePath = s3Service.uploadFileForGuest(file, guestDTO.getName(), guestDTO.getMobileNo());

            // Create the guest entity with file paths
            guestDTO.setPictureFilePath(uploadedFilePath);

            // Save the guest
            guestService.saveGuest(guestDTO);

            return ResponseEntity.ok("Guest created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create guest with govt. id file: " + e.getMessage());
        }
    }


    // Endpoint for uploading government IDs
    @PostMapping("/govtId")
    public ResponseEntity<String> uploadGovID(@RequestParam("file") MultipartFile file, @RequestParam("guestId") int guestId) {
        try {
            s3Service.uploadFileForGuest(file, guestId);
            return ResponseEntity.ok("File uploaded successfully.");
        } catch (IOException | GuestNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload govt. id file: " + e.getMessage());
        }
    }

    // Endpoint for uploading guest pictures
    @PostMapping("/picture")
    public ResponseEntity<String> uploadPicture(@RequestParam("file") MultipartFile file, @RequestParam("guestId") int guestId) {
        try {

            // Get the guest name
            Optional<GuestDTO> guestOptional = guestService.getGuestById(guestId);
            // Generate a unique key for the file in the S3 bucket
            String keyName;
            if (guestOptional.isPresent()) {
                GuestDTO guest = guestOptional.get();
                keyName = String.format("uploads/%s/%s/%s", guest.getMobileNo(), guest.getName(), file.getOriginalFilename());
                // Proceed with keyName
            } else {
                // Handle the case where guest is not found
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Guest not exists for id: " + guestId);
            }
            // Upload the file to S3 bucket
            s3Service.uploadFile(file, keyName);

            // Update the guest entity with the updated file path
            guestOptional.map(i -> {
                i.setGovtIDFilePath(keyName);
                return i;
            });
            guestService.update(guestOptional.get());
            return ResponseEntity.ok("File uploaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload picture file.");
        }
    }

    @GetMapping("/download/govtId/{guestId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable int guestId) {
        Optional<GuestDTO> guestDTO = guestService.getGuestById(guestId);
        Guest guest = modelMapper.map(guestDTO, Guest.class);
        if (guest.getPictureFilePath() != null) {
            try {
                // Generate a pre-signed URL for the file with a short expiration time
                URL presignedUrl = s3Service.getPresignedUrl(guest.getGovtIDFilePath());

                // Use the pre-signed URL to download the file
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<byte[]> response = restTemplate.exchange(presignedUrl.toURI(), HttpMethod.GET, null, byte[].class);
                // Determine content type based on file extension
                String contentType = URLConnection.guessContentTypeFromName(guest.getGovtIDFilePath());

                return ResponseEntity
                        .ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(response.getBody());
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

