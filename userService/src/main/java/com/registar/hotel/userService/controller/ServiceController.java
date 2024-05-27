package com.registar.hotel.userService.controller;

import com.registar.hotel.userService.model.response.AdditionalServicesDTO;
import com.registar.hotel.userService.service.AddServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
public class ServiceController {

    private final AddServices addServices;

    @Autowired
    public ServiceController(AddServices addServices) {
        this.addServices = addServices;
    }

    @PostMapping("/{bookingId}")
    public ResponseEntity<AdditionalServicesDTO> createService(@PathVariable(name = "bookingId") Long bookingId, @RequestBody AdditionalServicesDTO additionalServicesDTO) {
        AdditionalServicesDTO createdService = addServices.createService(bookingId, additionalServicesDTO);
        return new ResponseEntity<>(createdService, HttpStatus.CREATED);
    }

    @GetMapping("/bookings/{bookingId}")
    public ResponseEntity<List<AdditionalServicesDTO>> getAllServices(@PathVariable(name = "bookingId") Long bookingId) {
        List<AdditionalServicesDTO> services = addServices.getServiceByBookingId(bookingId);
        return new ResponseEntity<>(services, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdditionalServicesDTO> getServiceById(@PathVariable Long id) {
        AdditionalServicesDTO additionalServicesDTO = addServices.getServiceById(id);
        return new ResponseEntity<>(additionalServicesDTO, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdditionalServicesDTO> updateService(@PathVariable Long id, @RequestBody AdditionalServicesDTO additionalServicesDTO) {
        AdditionalServicesDTO updatedService = addServices.updateService(id, additionalServicesDTO);
        return new ResponseEntity<>(updatedService, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        addServices.deleteService(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
