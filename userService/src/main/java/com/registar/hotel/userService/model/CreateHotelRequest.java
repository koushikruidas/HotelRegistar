package com.registar.hotel.userService.model;

import com.registar.hotel.userService.entity.Address;
import com.registar.hotel.userService.model.request.AddressDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreateHotelRequest {
    private String name;
    private AddressDTO address;
    private List<String> phoneNumbers = new ArrayList<>();
    @NotBlank(message = "cannot not blank")
    @NotNull(message = "mandatory field")
    private String gstNumber;
    private List<Long> employeeIds;
}
