package com.registar.hotel.userService.service;

import com.registar.hotel.userService.entity.Guest;
import com.registar.hotel.userService.repository.GuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

@Service
public class FileUploadService {

    @Autowired
    private GuestRepository guestRepository;

    @Transactional
    public ResponseEntity<String> uploadGovID(MultipartFile file, int guestId) {
        try {
            // Save the file to the server
            String filePath = "/path/to/store/govids/" + file.getOriginalFilename();
            File dest = new File(filePath);
            file.transferTo(dest);

            // Update guest entity with file path
            Guest guest = guestRepository.findById(guestId).orElse(null);
            if (guest != null) {
                guest.setGovIDFilePath(filePath);
                guestRepository.save(guest);
                return new ResponseEntity<>("File uploaded successfully.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Guest not found.", HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to upload file.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<String> uploadPicture(MultipartFile file, int guestId) {
        try {
            // Save the file to the server
            String filePath = "/govtId/pictures/" + file.getOriginalFilename();
            File dest = new File(filePath);
            // Create the directory if it doesn't exist
            boolean mkdirs = dest.getParentFile().mkdirs();
            if (mkdirs) {
                file.transferTo(dest);
            }

            // Update guest entity with file path
            Guest guest = guestRepository.findById(guestId).orElse(null);
            if (guest != null) {
                guest.setPictureFilePath(filePath);
                guestRepository.save(guest);
                return new ResponseEntity<>("File uploaded successfully.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Guest not found.", HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to upload file.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

