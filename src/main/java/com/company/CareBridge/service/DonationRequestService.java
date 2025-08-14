package com.company.CareBridge.service;


import com.company.CareBridge.dtos.DonationRequestRequestDto;
import com.company.CareBridge.dtos.DonationRequestResponseDto;
import com.company.CareBridge.entity.DonationRequest;
import com.company.CareBridge.entity.Ngo;
import com.company.CareBridge.exceptions.ResourceNotFoundException;
import com.company.CareBridge.repository.DonationRequestRepository;
import com.company.CareBridge.repository.NgoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DonationRequestService {

    private final DonationRequestRepository donationRequestRepository;
    private final NgoRepository ngoRepository;
    private final ModelMapper modelMapper;

    public DonationRequestResponseDto createDonationRequest(DonationRequestRequestDto requestDto) throws BadRequestException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String currentUserEmail = auth.getName();

        log.info("Creating donation request for user: {}",currentUserEmail);

        // check if the current User is NGO or not
        Ngo ngo = ngoRepository.findByEmail(currentUserEmail)
                .orElseThrow(()->new ResourceNotFoundException("NGO not found for email:"+currentUserEmail));

        if (!auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("NGO"))) {
            log.error("Unauthorized attempt to create donation request by: {}", currentUserEmail);
            throw new BadRequestException("Only NGOs can create donation requests.");
        }

        DonationRequest donationRequest = modelMapper.map(requestDto,DonationRequest.class);
        donationRequest.setNgo(ngo);

        DonationRequest saved = donationRequestRepository.save(donationRequest);
        log.info("Donation request created successfully with ID: {}",saved.getId());

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
