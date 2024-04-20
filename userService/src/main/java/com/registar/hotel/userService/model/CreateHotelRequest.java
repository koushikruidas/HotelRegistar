package com.registar.hotel.userService.model;

import lombok.Data;

import java.util.List;

@Data
public class CreateHotelRequest {
    private String name;
    private String address;
    private String GSTNo;
    private List<Long> employeeIds;
}
