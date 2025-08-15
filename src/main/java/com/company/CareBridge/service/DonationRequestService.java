package com.company.CareBridge.service;


import com.company.CareBridge.dtos.DonationRequestRequestDto;
import com.company.CareBridge.dtos.DonationRequestResponseDto;
import com.company.CareBridge.entity.DonationRequest;
import com.company.CareBridge.entity.Ngo;
import com.company.CareBridge.entity.User;
import com.company.CareBridge.exceptions.ResourceNotFoundException;
import com.company.CareBridge.repository.DonationRequestRepository;
import com.company.CareBridge.repository.NgoRepository;
import com.company.CareBridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DonationRequestService {

    private final DonationRequestRepository donationRequestRepository;
    private final NgoRepository ngoRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public DonationRequestResponseDto createDonationRequest(DonationRequestRequestDto requestDto, Long ngoId) throws BadRequestException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = auth.getName();

        log.info("Creating donation request by user: {}", currentUserEmail);

        // Fetch the currently logged-in user
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> {
                    log.error("User not found for email: {}", currentUserEmail);
                    return new ResourceNotFoundException("User not found");
                });

        Ngo targetNgo;

        // Check if the user is ADMIN
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"));

        if (isAdmin) {
            // ADMIN can create donation request for any NGO
            targetNgo = ngoRepository.findById(ngoId)
                    .orElseThrow(() -> {
                        log.error("NGO not found for ID: {}", ngoId);
                        return new ResourceNotFoundException("NGO not found");
                    });
            log.info("ADMIN {} is creating a donation request for NGO ID: {}", currentUserEmail, ngoId);
        } else {
            // Only NGO can create their own donation request
            targetNgo = ngoRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> {
                        log.error("NGO not found for email: {}", currentUserEmail);
                        return new BadRequestException("Only NGOs can create donation requests.");
                    });

            // Ensure the user has NGO role
            boolean isNgo = currentUser.getRoles().stream()
                    .anyMatch(role -> role.getName().equalsIgnoreCase("NGO"));

            if (!isNgo) {
                log.error("Unauthorized attempt to create donation request by: {}", currentUserEmail);
                throw new BadRequestException("Only NGOs can create donation requests.");
            }

            log.info("NGO {} is creating their own donation request", currentUserEmail);
        }

        // Map the DTO to entity (without touching ngo field)
        DonationRequest donationRequest = modelMapper.map(requestDto, DonationRequest.class);

        // Attach the managed NGO entity
        donationRequest.setNgo(targetNgo);

        // Save donation request
        DonationRequest saved = donationRequestRepository.save(donationRequest);

        log.info("Donation request created successfully with ID: {} for NGO ID: {}", saved.getId(), targetNgo.getId());

        return modelMapper.map(saved, DonationRequestResponseDto.class);
    }





    public List<DonationRequestResponseDto> getAllDonationRequests(){
        log.info("Fetching all donation requests");

        return donationRequestRepository.findAll()
                .stream()
                .map(dr->{
                    DonationRequestResponseDto dto = modelMapper.map(dr, DonationRequestResponseDto.class);
                    dto.setNgoId(dr.getNgo().getId());
                    dto.setNgoName(dr.getNgo().getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
