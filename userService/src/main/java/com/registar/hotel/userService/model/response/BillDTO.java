package com.registar.hotel.userService.model.response;

import com.registar.hotel.userService.model.RoomDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillDTO {
    private String hotelName;
    private String address;
    private String gstin;
    private List<String> phoneNumbers;
    private List<String> guestName;
    private List<String> guestMobileNo;
    private List<RoomDTO> rooms;
    private double totalCost; // total booking cost + additional services cost
    private byte[] pdfContent; // Additional field for PDF content
}