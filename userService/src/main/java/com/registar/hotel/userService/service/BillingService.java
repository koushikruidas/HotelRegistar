package com.registar.hotel.userService.service;

import com.registar.hotel.userService.model.response.BillDTO;
import org.springframework.http.ResponseEntity;

public interface BillingService {
    ResponseEntity<byte[]> generateBillPdf(Long bookingId);
}