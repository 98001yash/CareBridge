package com.company.CareBridge.service;


import com.company.CareBridge.repository.DonationRepository;
import com.company.CareBridge.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional

}
