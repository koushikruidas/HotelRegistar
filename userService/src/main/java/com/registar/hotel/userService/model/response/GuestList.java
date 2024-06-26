package com.registar.hotel.userService.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestList {
    private long id;
    private String name;
    private String mobileNo;
}
