package com.registar.hotel.userService.service;

import com.registar.hotel.userService.model.response.DateRangeRevenueDTO;
import com.registar.hotel.userService.model.response.MonthlyRevenueDTO;

import java.time.LocalDate;

public interface RevenueService {
    MonthlyRevenueDTO generateMonthlyRevenueReport(int year, int month, long hotelId);
    DateRangeRevenueDTO generateRevenueReport(LocalDate startDate, LocalDate endDate, long hotelId);
}
