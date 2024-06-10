package com.registar.hotel.userService.service;

import com.registar.hotel.userService.exception.GuestNotFoundException;
import com.registar.hotel.userService.model.GuestDTO;;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.utils.IoUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class S3Service {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;
    @Value("${cloud.aws.region.static}")
    private String region;
    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Autowired
    private GuestService guestService;

    public String uploadFile(MultipartFile file, String keyName) throws IOException {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);

        try (S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build()) {

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

            return keyName;
        }
    }

    // Define a new method in your service layer to handle file upload
    public String uploadFileForGuest(MultipartFile file, int guestId) throws IOException {
        // Get the guest name
        Optional<GuestDTO> guestOptional = guestService.getGuestById(guestId);
        if (guestOptional.isPresent()) {
            GuestDTO guest = guestOptional.get();
            // Generate a unique key for the file in the S3 bucket
            String keyName = String.format("uploads/%s/%s/%s/%s", LocalDate.now(), guest.getMobileNo(), guest.getName(), file.getOriginalFilename());
            // Upload the file to S3 bucket
            return uploadFile(file, keyName);
        } else {
            throw new GuestNotFoundException("Guest not found for id: " + guestId);
        }
    }

    public byte[] getFileFromS3(String keyName) {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);

        try (S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build()) {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            try (ResponseInputStream<GetObjectResponse> objectResponseInputStream = s3Client.getObject(getObjectRequest)) {
                return objectResponseInputStream.readAllBytes();
            } catch (IOException e) {
                throw new RuntimeException("Failed to read file from S3", e);
            }
        }
    }


    public String uploadFileForGuest(MultipartFile file, String name, String mobileNo, LocalDate checkInDate) throws IOException {
        // Generate a unique key for the file in the S3 bucket
        String keyName = String.format("uploads/%s/%s/%s/%s", checkInDate, mobileNo, name, file.getOriginalFilename());
        // Upload the file to S3 bucket
        return uploadFile(file, keyName);
    }


}
