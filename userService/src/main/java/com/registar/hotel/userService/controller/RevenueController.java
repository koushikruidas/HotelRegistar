package com.registar.hotel.userService.controller;

import com.registar.hotel.userService.model.response.DateRangeRevenueDTO;
import com.registar.hotel.userService.model.response.MonthlyRevenueDTO;
import com.registar.hotel.userService.service.RevenueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/revenue")
public class RevenueController {

    private final RevenueService revenueService;

    public RevenueController(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    @GetMapping("/monthly/{year}/{month}/{hotelId}")
    public ResponseEntity<MonthlyRevenueDTO> generateMonthlyRevenueReport(@PathVariable int year, @PathVariable int month,
                                                                          @PathVariable long hotelId) {
        MonthlyRevenueDTO monthlyRevenue = revenueService.generateMonthlyRevenueReport(year, month, hotelId);
        return ResponseEntity.ok(monthlyRevenue);
    }

    @GetMapping("/date/range")
    public ResponseEntity<DateRangeRevenueDTO> generateRevenueReport(@RequestParam(name = "startDate") LocalDate startDate,
                                                                     @RequestParam(name = "endDate") LocalDate endDate,
                                                                     @RequestParam(name = "hotelId") long hotelId) {
        DateRangeRevenueDTO monthlyRevenue = revenueService.generateRevenueReport(startDate, endDate, hotelId);
        return ResponseEntity.ok(monthlyRevenue);
    }
}