package com.registar.hotel.userService.model;

import lombok.Data;

@Data
public class CreateHotelRequest {
    private String name;
    private String address;
    private String GSTNo;
}
