package com.company.CareBridge.service;


import com.company.CareBridge.dtos.NgoRequestDto;
import com.company.CareBridge.dtos.NgoResponseDto;
import com.company.CareBridge.entity.Ngo;
import com.company.CareBridge.entity.User;
import com.company.CareBridge.repository.NgoRepository;
import com.company.CareBridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NgoService {

    private final NgoRepository ngoRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public NgoResponseDto createNgo(NgoRequestDto ngoRequestDto,Long creatorId){
        log.info("Request to create NGO: {} by User Id: {}",ngoRequestDto.getName(),creatorId);

        //valodate creator
        User creator = userRepository.findById(creatorId)
                .orElseThrow(()->{
                    log.error("Creator with id {} not found",creatorId);
                    return new RuntimeException("Creator not found");
                });

        // Only ADMIN can create Ngo
        boolean isAdmin = creator.getRoles().stream()
                .anyMatch(role->role.getName().equalsIgnoreCase("ADMIN"));

        if(!isAdmin){
            log.warn("User Id: {} attempted to create NGO without ADMIN role",creatorId);
            throw new RuntimeException("Only ADMIN users can create NGOs");
        }

        Ngo ngoEntity = modelMapper.map(ngoRequestDto, Ngo.class);
        ngoEntity.setCreatedAt(LocalDateTime.now());
        ngoEntity.setUser(creator); // assign user to satisfy not-null constraint
        // save NGPs
        Ngo savedNgo = ngoRepository.save(ngoEntity);
        log.info("NGO created successfully with ID: {}",savedNgo.getId());

        return modelMapper.map(savedNgo, NgoResponseDto.class);
    }

    public NgoResponseDto getNgoById(Long ngoId){
        Ngo ngo = ngoRepository.findById(ngoId)
                .orElseThrow(()->new RuntimeException("NGO not found"));
        return modelMapper.map(ngo, NgoResponseDto.class);
    }


    public List<NgoResponseDto> getAllNgos(){
        List<Ngo> ngos = ngoRepository.findAll();
        return ngos.stream()
                .map(ngo->modelMapper.map(ngo, NgoResponseDto.class))
                .toList();
    }


    public NgoResponseDto updateNgo(Long ngoId, NgoRequestDto ngoRequestDto){
        Ngo existingNgo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new RuntimeException("NGO not found"));

        existingNgo.setName(ngoRequestDto.getName());
        existingNgo.setEmail(ngoRequestDto.getEmail());
        existingNgo.setPhone(ngoRequestDto.getPhone());
        existingNgo.setAddress(ngoRequestDto.getAddress());
        existingNgo.setCity(ngoRequestDto.getCity());
        existingNgo.setState(ngoRequestDto.getState());
        existingNgo.setZipCode(ngoRequestDto.getZipCode());
        existingNgo.setUpdatedAt(LocalDateTime.now());

        Ngo updatedNgo = ngoRepository.save(existingNgo);
        return modelMapper.map(updatedNgo, NgoResponseDto.class);
    }


    public void deleteNgo(Long ngoId){
    Ngo ngo = ngoRepository.findById(ngoId)
                .orElseThrow(()->new RuntimeException("Ngo not found"));

    ngoRepository.delete(ngo);
    }

}
