package com.registar.hotel.userService.controller;

import com.registar.hotel.userService.entity.Guest;
import com.registar.hotel.userService.model.GuestDTO;
import com.registar.hotel.userService.service.GuestService;
import com.registar.hotel.userService.service.S3Service;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLConnection;
import java.util.Optional;

@RestController
@RequestMapping("/file")
public class FileDownloadController {
    private final GuestService guestService;
    private final S3Service s3Service;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    public FileDownloadController(GuestService guestService, S3Service s3Service) {
        this.guestService = guestService;
        this.s3Service = s3Service;
    }

    @GetMapping("/download/govtId/{guestId}")
    public ResponseEntity<byte[]> downloadGovtId(@PathVariable int guestId) {
        Optional<GuestDTO> guestDTO = guestService.getGuestById(guestId);
        Guest guest = modelMapper.map(guestDTO, Guest.class);
        if (guest.getPictureFilePath() != null) {
            try {
                // Generate a pre-signed URL for the file with a short expiration time
                byte[] fileFromS3 = s3Service.getFileFromS3(guest.getGovtIDFilePath());
                // Determine the content type based on the file extension
                String contentType = URLConnection.guessContentTypeFromName(guest.getGovtIDFilePath());

                return ResponseEntity
                        .ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(fileFromS3);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/download/picture/{guestId}")
    public ResponseEntity<byte[]> downloadPicture(@PathVariable int guestId) {
        Optional<GuestDTO> guestDTO = guestService.getGuestById(guestId);
        Guest guest = modelMapper.map(guestDTO, Guest.class);
        if (guest.getPictureFilePath() != null) {
            try {
                // Generate a pre-signed URL for the file with a short expiration time
                byte[] fileFromS3 = s3Service.getFileFromS3(guest.getPictureFilePath());
                // Determine the content type based on the file extension
                String contentType = URLConnection.guessContentTypeFromName(guest.getPictureFilePath());

                return ResponseEntity
                        .ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(fileFromS3);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

