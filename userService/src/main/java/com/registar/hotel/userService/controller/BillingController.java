package com.registar.hotel.userService.controller;

import com.registar.hotel.userService.model.response.BillDTO;
import com.registar.hotel.userService.service.BillingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bills")
public class BillingController {
    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<byte[]> generateBill(@PathVariable Long bookingId) {
        return billingService.generateBillPdf(bookingId);
    }
}