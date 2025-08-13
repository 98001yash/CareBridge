package com.company.CareBridge.controller;


import com.company.CareBridge.dtos.DonationRequestDto;
import com.company.CareBridge.dtos.DonationResponseDto;
import com.company.CareBridge.service.DonationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
@Slf4j
public class DonationController {

    private final DonationService donationService;


    @PostMapping("/donor/{donorId}")
    public ResponseEntity<DonationResponseDto> createDonation(
            @PathVariable Long donorId,
            @RequestBody DonationRequestDto donationRequestDto
            ){
        log.info("Received request to create donation for donorId {}",donorId);
        DonationResponseDto createdDonation = donationService.createDonation(donorId, donationRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDonation);
    }
}
