package com.registar.hotel.userService.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreateHotelRequest {
    private String name;
    private String address;
    private List<String> phoneNumbers = new ArrayList<>();
    private String gstNumber;
    private List<Long> employeeIds;
}
