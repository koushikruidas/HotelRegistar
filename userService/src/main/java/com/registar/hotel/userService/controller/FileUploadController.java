package com.registar.hotel.userService.controller;

import com.registar.hotel.userService.entity.Guest;
import com.registar.hotel.userService.model.GuestDTO;
import com.registar.hotel.userService.service.FileUploadService;
import com.registar.hotel.userService.service.GuestService;
import com.registar.hotel.userService.service.S3Service;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private FileUploadService fileUploadService;
    private GuestService guestService;
    private S3Service s3Service;

    @Autowired
    public FileUploadController(FileUploadService fileUploadService, GuestService guestService, S3Service s3Service) {
        this.fileUploadService = fileUploadService;
        this.guestService = guestService;
        this.s3Service = s3Service;
    }

    // Endpoint for uploading government IDs
    @PostMapping("/govtId")
    public ResponseEntity<String> uploadGovID(@RequestParam("file") MultipartFile file, @RequestParam("guestId") int guestId) {
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
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Guest not exists for id: "+guestId);
            }
            // Upload the file to S3 bucket
            s3Service.uploadFile(file, keyName);

            // Update the guest entity with the updated file path
            guestOptional.map(i -> {
                i.setGovIDFilePath(keyName);
                return i;
            });
            return ResponseEntity.ok("File uploaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload govt. id file.");
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
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Guest not exists for id: "+guestId);
            }
            // Upload the file to S3 bucket
            s3Service.uploadFile(file, keyName);

            // Update the guest entity with the updated file path
            guestOptional.map(i -> {
                i.setGovIDFilePath(keyName);
                return i;
            });
            return ResponseEntity.ok("File uploaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload picture file.");
        }
    }

    @GetMapping("/download/govtId/{guestId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable int guestId) {
        Optional<GuestDTO> guest = guestService.getGuestById(guestId);
        if (guest.isPresent() && guest.get().getPictureFilePath() != null) {
            try {
                // Generate a pre-signed URL for the file with a short expiration time
                URL presignedUrl = s3Service.getPresignedUrl(guest.get().getPictureFilePath());

                // Use the pre-signed URL to download the file
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<byte[]> response = restTemplate.exchange(presignedUrl.toURI(), HttpMethod.GET, null, byte[].class);

                return ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
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

