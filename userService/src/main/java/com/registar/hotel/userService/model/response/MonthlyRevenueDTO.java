package com.registar.hotel.userService.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyRevenueDTO {
    private int year;
    private int month;
    private double totalBookingRevenue;
    private double totalServiceRevenue;
    private double totalRevenue;
}
