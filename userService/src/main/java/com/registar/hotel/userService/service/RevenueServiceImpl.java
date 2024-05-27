package com.registar.hotel.userService.service;

import com.registar.hotel.userService.entity.AdditionalServices;
import com.registar.hotel.userService.entity.Booking;
import com.registar.hotel.userService.model.response.DateRangeRevenueDTO;
import com.registar.hotel.userService.model.response.MonthlyRevenueDTO;
import com.registar.hotel.userService.repository.AdditionalServicesRepository;
import com.registar.hotel.userService.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RevenueServiceImpl implements RevenueService {

    private final BookingRepository bookingRepository;
    private final AdditionalServicesRepository serviceRepository;

    public RevenueServiceImpl(BookingRepository bookingRepository, AdditionalServicesRepository serviceRepository) {
        this.bookingRepository = bookingRepository;
        this.serviceRepository = serviceRepository;
    }

    @Override
    public MonthlyRevenueDTO generateMonthlyRevenueReport(int year, int month, long hotelId) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

        List<Booking> bookings = bookingRepository.findBookingsByHotelIdAndDates(startOfMonth, endOfMonth, hotelId);
        List<AdditionalServices> services = serviceRepository.findServicesByBookingDates(startOfMonth, endOfMonth);

        double totalBookingRevenue = bookings.stream().mapToDouble(Booking::getTotalPrice).sum();
        double totalServiceRevenue = services.stream().mapToDouble(AdditionalServices::getCost).sum();
        double totalRevenue = totalBookingRevenue + totalServiceRevenue;

        return new MonthlyRevenueDTO(year, month, totalBookingRevenue, totalServiceRevenue, totalRevenue);
    }

    @Override
    public DateRangeRevenueDTO generateRevenueReport(LocalDate startDate, LocalDate endDate, long hotelId) {
        List<Booking> bookings = bookingRepository.findBookingsByHotelIdAndDates(startDate, endDate, hotelId);
        List<AdditionalServices> services = serviceRepository.findServicesByBookingDates(startDate, endDate);

        double totalBookingRevenue = bookings.stream().mapToDouble(Booking::getTotalPrice).sum();
        double totalServiceRevenue = services.stream().mapToDouble(AdditionalServices::getCost).sum();
        double totalRevenue = totalBookingRevenue + totalServiceRevenue;

        return new DateRangeRevenueDTO(startDate,endDate,totalBookingRevenue,totalServiceRevenue,totalRevenue);

    }
}