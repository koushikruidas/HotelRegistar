package com.registar.hotel.userService.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateRangeRevenueDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalBookingRevenue;
    private double totalServiceRevenue;
    private double totalRevenue;
}