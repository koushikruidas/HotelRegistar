package com.registar.hotel.userService.service;

import com.registar.hotel.userService.model.response.AdditionalServicesDTO;

import java.util.List;

public interface AddServices {
    AdditionalServicesDTO createService(Long bookingId, AdditionalServicesDTO additionalServicesDTO);
    List<AdditionalServicesDTO> getServiceByBookingId(Long id);
    AdditionalServicesDTO getServiceById(Long id);
    AdditionalServicesDTO updateService(Long id, AdditionalServicesDTO additionalServicesDTO);
    void deleteService(Long id);
}