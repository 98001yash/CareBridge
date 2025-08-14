package com.company.CareBridge.controller;


import com.company.CareBridge.dtos.DonationRequestRequestDto;
import com.company.CareBridge.dtos.DonationRequestResponseDto;
import com.company.CareBridge.service.DonationRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/donation-requests")
@RequiredArgsConstructor
@Slf4j
public class DonationRequestController {

    private final DonationRequestService donationRequestService;

    @PostMapping
    public ResponseEntity<DonationRequestResponseDto> createDonationRequest(@RequestBody DonationRequestRequestDto requestDto) throws BadRequestException {
        log.info("Received request to create donation request");
        DonationRequestResponseDto created = donationRequestService.createDonationRequest(requestDto);
        return ResponseEntity.ok(created);
    }
}
