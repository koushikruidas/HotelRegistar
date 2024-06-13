package com.registar.hotel.userService.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.registar.hotel.userService.entity.*;
import com.registar.hotel.userService.model.RoomDTO;
import com.registar.hotel.userService.model.response.BillDTO;
import com.registar.hotel.userService.repository.AdditionalServicesRepository;
import com.registar.hotel.userService.repository.BookingRepository;
import com.registar.hotel.userService.repository.GuestRepository;
import com.registar.hotel.userService.repository.HotelRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BillingServiceImpl implements BillingService {

    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;
    private final AdditionalServicesRepository additionalServiceRepository;
    private final HotelRepository hotelRepository;

    @Autowired
    public BillingServiceImpl(BookingRepository bookingRepository,
                              AdditionalServicesRepository additionalServiceRepository,
                              ModelMapper modelMapper, HotelRepository hotelRepository) {
        this.bookingRepository = bookingRepository;
        this.additionalServiceRepository = additionalServiceRepository;
        this.modelMapper = modelMapper;
        this.hotelRepository = hotelRepository;
    }

    @Override
    public ResponseEntity<byte[]> generateBillPdf(Long bookingId) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()) {
            throw new RuntimeException("Booking not found with ID: " + bookingId);
        }

        Booking booking = bookingOptional.get();
        List<Guest> guests = booking.getGuests();
        List<Room> rooms = booking.getBookedRooms();
        Hotel hotel = rooms.get(0).getHotel();
//        Hotel hotel = hotelRepository.findById(rooms.get(0).getHotel().getId()).get();

        List<AdditionalServices> services = additionalServiceRepository.findByBookings_Id(bookingId);

        double additionalServicesCost = services.stream().mapToDouble(AdditionalServices::getCost).sum();
        double totalCost = booking.getTotalPrice() + additionalServicesCost;

        List<String> guestNames = guests.stream().map(Guest::getName).collect(Collectors.toList());
        List<String> guestMobileNos = guests.stream().map(Guest::getMobileNo).collect(Collectors.toList());
        List<String> hotelPhoneNos = hotel.getPhoneNumbers().stream().map(PhoneNumber::getNumber).toList();

        List<RoomDTO> roomDTOs = rooms.stream().map(room -> modelMapper.map(room, RoomDTO.class))
                .collect(Collectors.toList());

        BillDTO billDTO = BillDTO.builder()
                .hotelName(hotel.getName())
                .gstin(hotel.getGstNumber())
                .address(hotel.getAddress())
                .phoneNumbers(hotelPhoneNos)
                .guestName(guestNames)
                .guestMobileNo(guestMobileNos)
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .rooms(roomDTOs)
                .numberOfNights(ChronoUnit.DAYS.between(booking.getCheckInDate(),booking.getCheckOutDate()))
                .totalCost(totalCost)
                .build();

        // Generate PDF
        byte[] pdfContent = generatePdf(billDTO, services);
        billDTO.setPdfContent(pdfContent);

        // Set response headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("inline").filename("bill.pdf").build());

        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }
    private byte[] generatePdf(BillDTO billDTO, List<AdditionalServices> services) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            // Hotel name in the top center
            Paragraph hotelNameParagraph = new Paragraph(billDTO.getHotelName(), new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD));
            hotelNameParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(hotelNameParagraph);

            // Create a table for the header with two cells
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingAfter(10f);
            headerTable.setSpacingBefore(10f);

            // Address and phone numbers on the left side
            PdfPCell leftCell = new PdfPCell();
            leftCell.setBorder(Rectangle.NO_BORDER);
            Paragraph leftParagraph = new Paragraph();
            leftParagraph.add(new Phrase("Address:\n", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD)));
            // Wrap the address for better presentation
            leftParagraph.add(new Phrase(billDTO.getAddress().getStreet()+", "+billDTO.getAddress().getCity()+"\n", new Font(Font.FontFamily.HELVETICA, 11)));
            leftParagraph.add(new Phrase(billDTO.getAddress().getState()+" - "+billDTO.getAddress().getZipCode()+"\n", new Font(Font.FontFamily.HELVETICA, 11)));
            leftParagraph.add(new Phrase(billDTO.getAddress().getCountry()+"\n", new Font(Font.FontFamily.HELVETICA, 11)));

            leftParagraph.add(new Phrase("Phone Numbers:\n", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD)));
            for (String phoneNumber : billDTO.getPhoneNumbers()) {
                leftParagraph.add(new Phrase(phoneNumber + "\n", new Font(Font.FontFamily.HELVETICA, 11)));
            }
            leftParagraph.add(Chunk.NEWLINE);
            leftCell.addElement(leftParagraph);
            headerTable.addCell(leftCell);

            // GSTIN on the right side
            PdfPCell rightCell = new PdfPCell(new Phrase("GSTIN :"+billDTO.getGstin(), new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD)));
            rightCell.setBorder(Rectangle.NO_BORDER);
            rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(rightCell);

            document.add(headerTable);



            document.add(Chunk.NEWLINE); // Add a newline for spacing

            // Guest details in table with bold headers
            PdfPTable guestTable = new PdfPTable(4);
            guestTable.setWidthPercentage(100);
            guestTable.setSpacingBefore(10f);
            guestTable.setSpacingAfter(10f);
            guestTable.addCell(new PdfPCell(new Phrase("Guest Name", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD))));
            guestTable.addCell(new PdfPCell(new Phrase("Mobile No", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD))));
            guestTable.addCell(new PdfPCell(new Phrase("Check-in Date", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD))));
            guestTable.addCell(new PdfPCell(new Phrase("Check-out Date", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD))));

            for (int i = 0; i < billDTO.getGuestName().size(); i++) {
                guestTable.addCell(new PdfPCell(new Phrase(billDTO.getGuestName().get(i), new Font(Font.FontFamily.HELVETICA, 11))));
                guestTable.addCell(new PdfPCell(new Phrase(billDTO.getGuestMobileNo().get(i), new Font(Font.FontFamily.HELVETICA, 11))));
                guestTable.addCell(new PdfPCell(new Phrase(String.valueOf(billDTO.getCheckInDate()), new Font(Font.FontFamily.HELVETICA, 11))));
                guestTable.addCell(new PdfPCell(new Phrase(String.valueOf(billDTO.getCheckOutDate()), new Font(Font.FontFamily.HELVETICA, 11))));
            }
            document.add(guestTable);

            // Add room details
            PdfPTable roomTable = new PdfPTable(3);
            roomTable.setWidthPercentage(100);
            roomTable.setSpacingBefore(10f);
            roomTable.setSpacingAfter(10f);
            roomTable.addCell(new PdfPCell(new Phrase("Room Number", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD))));
            roomTable.addCell(new PdfPCell(new Phrase("Room Type", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD))));
            PdfPCell priceHeaderCell = new PdfPCell(new Phrase("Price", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD)));
            priceHeaderCell.setHorizontalAlignment(Element.ALIGN_RIGHT); // Align header to the right
            roomTable.addCell(priceHeaderCell);

            for (RoomDTO room : billDTO.getRooms()) {
                roomTable.addCell(new PdfPCell(new Phrase(String.valueOf(room.getRoomNumber()), new Font(Font.FontFamily.HELVETICA, 11))));
                roomTable.addCell(new PdfPCell(new Phrase(room.getType().name(), new Font(Font.FontFamily.HELVETICA, 11))));
                PdfPCell priceCell = new PdfPCell(new Phrase(room.getPricePerNight()+" X "+billDTO.getNumberOfNights()+"(Nights) = "+billDTO.getNumberOfNights()*room.getPricePerNight(), new Font(Font.FontFamily.HELVETICA, 11)));
                priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                roomTable.addCell(priceCell);
            }
            document.add(roomTable);

            // Add additional services details
            Paragraph additionalServicesHeader = new Paragraph("Additional Services:", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD));
            document.add(additionalServicesHeader);

            PdfPTable servicesTable = new PdfPTable(2);
            servicesTable.setWidthPercentage(100);
            servicesTable.setSpacingBefore(10f);
            servicesTable.setSpacingAfter(10f);

            for (AdditionalServices service : services) {
                servicesTable.addCell(new PdfPCell(new Phrase(service.getName(), new Font(Font.FontFamily.HELVETICA, 11))));
                PdfPCell costCell = new PdfPCell(new Phrase(String.valueOf(service.getCost()), new Font(Font.FontFamily.HELVETICA, 11)));
                costCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                servicesTable.addCell(costCell);
            }
            document.add(servicesTable);

            // Add total cost, bold
            Paragraph totalCost = new Paragraph("Total Cost: " + billDTO.getTotalCost(), new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD));
            totalCost.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalCost);

            document.close();
            return baos.toByteArray();
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate PDF");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
