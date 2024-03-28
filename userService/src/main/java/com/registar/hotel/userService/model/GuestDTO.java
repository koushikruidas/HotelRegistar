package com.registar.hotel.userService.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GuestDTO {
    private int id;

    @NotEmpty(message = "name cannot be empty.")
    private String name;

    @NotEmpty(message = "mobile number cannot be empty.")
    @Size(min = 10, max = 12, message = "mobile number must be 10 digits. with country code it can be 12 digits.")
    private String mobileNo;

    private String govtIDFilePath;
    private String pictureFilePath;

}
