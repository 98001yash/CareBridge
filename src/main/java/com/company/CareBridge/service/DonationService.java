package com.company.CareBridge.service;


import com.company.CareBridge.dtos.DonationRequestDto;
import com.company.CareBridge.dtos.DonationResponseDto;
import com.company.CareBridge.dtos.DonationStatusUpdateDto;
import com.company.CareBridge.entity.Donation;
import com.company.CareBridge.entity.DonationRequest;
import com.company.CareBridge.entity.Ngo;
import com.company.CareBridge.entity.User;
import com.company.CareBridge.enums.DonationStatus;
import com.company.CareBridge.exceptions.ResourceNotFoundException;
import com.company.CareBridge.repository.DonationRepository;
import com.company.CareBridge.repository.DonationRequestRepository;
import com.company.CareBridge.repository.NgoRepository;
import com.company.CareBridge.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final DonationRequestRepository donationRequestRepository;
    private final NgoRepository ngoRepository;



    @Transactional
    public DonationResponseDto createDonation(Long donorId, DonationRequestDto donationRequestDto, Long donationRequestId) {
        log.info("Creating donation by donorId: {}", donorId);

        // Fetch donor
        User donor = userRepository.findById(donorId)
                .orElseThrow(() -> new ResourceNotFoundException("Donor not found with id: " + donorId));

        Ngo targetNgo = null;
        DonationRequest linkedRequest = null;

        // If donationRequestId is provided, fetch the request and set NGO from it
        if (donationRequestId != null) {
            linkedRequest = donationRequestRepository.findById(donationRequestId)
                    .orElseThrow(() -> new ResourceNotFoundException("Donation request not found with id: " + donationRequestId));
            targetNgo = linkedRequest.getNgo();
            log.info("Donation will be linked to request ID: {} and NGO: {}", donationRequestId, targetNgo.getName());
        } else if (donationRequestDto.getNgoId() != null) {
            // If donor chooses a specific NGO for general donation
            targetNgo = ngoRepository.findById(donationRequestDto.getNgoId())
                    .orElseThrow(() -> new ResourceNotFoundException("NGO not found with id: " + donationRequestDto.getNgoId()));
        }

        // Map DTO to entity
        Donation donation = modelMapper.map(donationRequestDto, Donation.class);
        donation.setId(null); // ensure new entity
        donation.setDonor(donor);
        donation.setNgo(targetNgo);
        donation.setDonationRequest(linkedRequest); // link to request if any
        donation.setStatus(DonationStatus.PENDING);
        donation.setCreatedAt(LocalDateTime.now());

        Donation saved = donationRepository.save(donation);
        log.info("Donation created successfully with ID: {}", saved.getId());

        return mapToResponseDto(saved);
    }



    @Transactional(readOnly = true)
    public List<DonationResponseDto> getDonorDonations(Long donorId){
        log.info("Fetching donations for donorId: {}",donorId);

        User donor = userRepository.findById(donorId)
                .orElseThrow(()->new ResourceNotFoundException("Donor not found with id: "+donorId));

        return donationRepository.findByDonor(donor)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DonationResponseDto> getNgoDonations(Long ngoId){
        log.info("Fetching donation for ngoId: {}",ngoId);

        User ngo = userRepository.findById(ngoId)
                .orElseThrow(()->new ResourceNotFoundException("NGO not found with id: "+ngoId));

        return donationRepository.findByNgo(ngo)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public DonationResponseDto updateDonationStatus(Long donationId, DonationStatusUpdateDto statusUpdateDto){
        log.info("Updating donationId {} status to  {}",donationId, statusUpdateDto.getStatus());

        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(()->new ResourceNotFoundException("Donation not found with id: "+donationId));

        donation.setStatus(statusUpdateDto.getStatus());
        Donation updated = donationRepository.save(donation);

        log.info("DonationId {} status updated successfully",donationId);
        return mapToResponseDto(updated);
    }


    private DonationResponseDto mapToResponseDto(Donation donation) {
        return DonationResponseDto.builder()
                .id(donation.getId())
                .donorName(donation.getDonor().getUsername())
                .ngoName(donation.getNgo() != null ? donation.getNgo().getName() : null)
                .itemName(donation.getItemName())
                .category(donation.getCategory())
                .quantity(donation.getQuantity())
                .description(donation.getDescription())
                .status(donation.getStatus())
                .createdAt(donation.getCreatedAt())
                .updatedAt(donation.getUpdatedAt())
                .build();
    }


}
