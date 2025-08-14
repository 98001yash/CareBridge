package com.company.CareBridge.controller;


import com.company.CareBridge.dtos.NgoRequestDto;
import com.company.CareBridge.dtos.NgoResponseDto;
import com.company.CareBridge.service.NgoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/ngos")
@RequiredArgsConstructor
public class NgoController {

    private final NgoService ngoService;


    @PostMapping
    public ResponseEntity<NgoResponseDto> createNgo(
            @RequestBody NgoRequestDto ngoRequestDto,
            @RequestParam Long creatorId
    ) {
        log.info("Received request to create NGO by user ID: {}", creatorId);
        NgoResponseDto response = ngoService.createNgo(ngoRequestDto, creatorId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
