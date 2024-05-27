package com.registar.hotel.userService.service;

import com.registar.hotel.userService.entity.Booking;
import com.registar.hotel.userService.exception.ResourceNotFoundException;
import com.registar.hotel.userService.model.response.AdditionalServicesDTO;
import com.registar.hotel.userService.repository.AdditionalServicesRepository;
import com.registar.hotel.userService.repository.BookingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.registar.hotel.userService.entity.AdditionalServices;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AddServiceImpl implements AddServices {

    private final AdditionalServicesRepository additionalServicesRepository;
    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public AddServiceImpl(AdditionalServicesRepository additionalServicesRepository,
                          BookingRepository bookingRepository,
                          ModelMapper modelMapper) {
        this.additionalServicesRepository = additionalServicesRepository;
        this.bookingRepository = bookingRepository;
        this.modelMapper = modelMapper;
    }
    @Override
    @Transactional
    public AdditionalServicesDTO createService(Long bookingId, AdditionalServicesDTO additionalServicesDTO) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()){
            throw new ResourceNotFoundException("Booking not found for id: "+bookingId);
        }
        AdditionalServices service = new AdditionalServices();
        service.setName(additionalServicesDTO.getName());
        service.setCost(additionalServicesDTO.getCost());
        service.setBookings(booking.get());
        service = additionalServicesRepository.save(service);
        return new AdditionalServicesDTO(service.getId(), service.getName(), service.getCost());
    }

    @Override
    public List<AdditionalServicesDTO> getServiceByBookingId(Long id) {
        return additionalServicesRepository.findByBookings_Id(id).stream()
                .map(service -> new AdditionalServicesDTO(service.getId(), service.getName(), service.getCost()))
                .collect(Collectors.toList());
    }

    @Override
    public AdditionalServicesDTO getServiceById(Long id) {
        AdditionalServices service = additionalServicesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with ID: " + id));
        return new AdditionalServicesDTO(service.getId(), service.getName(), service.getCost());
    }

    @Override
    public AdditionalServicesDTO updateService(Long id, AdditionalServicesDTO additionalServicesDTO) {
        AdditionalServices service = additionalServicesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with ID: " + id));
        if (additionalServicesDTO.getName() != null && !additionalServicesDTO.getName().isEmpty() && !additionalServicesDTO.getName().isBlank()) {
            service.setName(additionalServicesDTO.getName());
        }
        if (additionalServicesDTO.getCost() > 0) {
            service.setCost(additionalServicesDTO.getCost());
        }
        service = additionalServicesRepository.save(service);
        return new AdditionalServicesDTO(service.getId(), service.getName(), service.getCost());
    }

    @Override
    public void deleteService(Long id) {
        additionalServicesRepository.deleteById(id);
    }
}