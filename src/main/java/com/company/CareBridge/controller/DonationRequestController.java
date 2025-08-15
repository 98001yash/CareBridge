package com.company.CareBridge.controller;


import com.company.CareBridge.dtos.DonationRequestRequestDto;
import com.company.CareBridge.dtos.DonationRequestResponseDto;
import com.company.CareBridge.service.DonationRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donation-requests")
@RequiredArgsConstructor
@Slf4j
public class DonationRequestController {

    private final DonationRequestService donationRequestService;

    @PostMapping
    public ResponseEntity<DonationRequestResponseDto> createDonationRequest(
            @RequestBody DonationRequestRequestDto requestDto,
            @RequestParam(required = false) Long ngoId  // Optional for ADMIN
    ) throws BadRequestException {
        log.info("Received request to create donation request");

        DonationRequestResponseDto created = donationRequestService.createDonationRequest(requestDto, ngoId);

        log.info("Donation request created successfully with ID: {}", created.getId());
        return ResponseEntity.ok(created);
    }


    @GetMapping
    public ResponseEntity<List<DonationRequestResponseDto>> getAllDonationRequests(){
        log.info("Received request to fetch all donation requests");
        return ResponseEntity.ok(donationRequestService.getAllDonationRequests());
    }
}
