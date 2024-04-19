package com.registar.hotel.userService.model.request;

import lombok.Data;

import java.util.List;

@Data
public class EmploymentRequest {
    private List<Long> hotelIds;
    private String employeeUsername;
}
