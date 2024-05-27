package com.registar.hotel.userService.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalServicesDTO {
    private Long id;
    private String name;
    private double cost;
}
