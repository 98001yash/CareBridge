package com.company.CareBridge.service;


import com.company.CareBridge.dtos.DonationRequestDto;
import com.company.CareBridge.dtos.DonationResponseDto;
import com.company.CareBridge.dtos.DonationStatusUpdateDto;
import com.company.CareBridge.entity.Donation;
import com.company.CareBridge.entity.User;
import com.company.CareBridge.enums.DonationStatus;
import com.company.CareBridge.exceptions.ResourceNotFoundException;
import com.company.CareBridge.repository.DonationRepository;
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



    @Transactional
    public DonationResponseDto createDonation(Long donorId, DonationRequestDto donationRequestDto) {
        log.info("Creating donation by donorId: {}", donorId);

        User donor = userRepository.findById(donorId)
                .orElseThrow(() -> new ResourceNotFoundException("Donor not found with id: " + donorId));

        User ngo = null;
        if (donationRequestDto.getNgoId() != null) {
            ngo = userRepository.findById(donationRequestDto.getNgoId())
                    .orElseThrow(() -> new ResourceNotFoundException("NGO not found with id: " + donationRequestDto.getNgoId()));
        }

        // Map but skip ID and relationships
        Donation donation = modelMapper.map(donationRequestDto, Donation.class);
        donation.setId(null); // ensure Hibernate treats this as new
        donation.setDonor(donor);
        donation.setNgo(ngo);
        donation.setStatus(DonationStatus.PENDING); // default for new donation
        donation.setCreatedAt(LocalDateTime.now());

        Donation saved = donationRepository.save(donation);
        log.info("Donation created with id: {}", saved.getId());
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
                .ngoName(donation.getNgo() != null ? donation.getNgo().getUsername() : null)
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
