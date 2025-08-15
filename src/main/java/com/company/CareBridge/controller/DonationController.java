package com.company.CareBridge.controller;


import com.company.CareBridge.dtos.DonationRequestDto;
import com.company.CareBridge.dtos.DonationResponseDto;
import com.company.CareBridge.dtos.DonationStatusUpdateDto;
import com.company.CareBridge.service.DonationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
@Slf4j
public class DonationController {

    private final DonationService donationService;


    @PostMapping("/donor/{donorId}")
    public ResponseEntity<DonationResponseDto> createDonation(
            @PathVariable Long donorId,
            @RequestBody DonationRequestDto donationRequestDto,
            @RequestParam(required = false) Long donationRequestId // optional
    ) {
        log.info("Received request to create donation for donorId: {} with donationRequestId: {}", donorId, donationRequestId);
        DonationResponseDto createdDonation = donationService.createDonation(donorId, donationRequestDto, donationRequestId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDonation);
    }

    @GetMapping("/donor/{donorId}")
    public ResponseEntity<List<DonationResponseDto>> getDonorDonations(@PathVariable Long donorId) {
        log.info("Received request to fetch donations for donorId {}", donorId);
        return ResponseEntity.ok(donationService.getDonorDonations(donorId));
    }

    @GetMapping("/ngo/{ngoId}")
    public ResponseEntity<List<DonationResponseDto>> getNgoDonations(@PathVariable Long ngoId) {
        log.info("Received request to fetch donations for ngoId {}", ngoId);
        return ResponseEntity.ok(donationService.getNgoDonations(ngoId));
    }


    @PatchMapping("/{donationId}/status")
    public ResponseEntity<DonationResponseDto> updateDonationStatus(
            @PathVariable Long donationId,
            @RequestBody DonationStatusUpdateDto statusUpdateDto) {
        log.info("Received request to update status for donationId {}", donationId);
        DonationResponseDto updatedDonation = donationService.updateDonationStatus(donationId, statusUpdateDto);
        return ResponseEntity.ok(updatedDonation);
    }
}
