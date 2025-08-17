package com.company.CareBridge.controller;


import com.company.CareBridge.dtos.NgoRequestDto;
import com.company.CareBridge.dtos.NgoResponseDto;
import com.company.CareBridge.service.NgoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


    @GetMapping("/{ngoId}")
    public ResponseEntity<NgoResponseDto> getNgoById(@PathVariable Long ngoId) {
        return ResponseEntity.ok(ngoService.getNgoById(ngoId));
    }

    @GetMapping
    public ResponseEntity<List<NgoResponseDto>> getAllNgos() {
        return ResponseEntity.ok(ngoService.getAllNgos());
    }

    @PutMapping("/{ngoId}")
    public ResponseEntity<NgoResponseDto> updateNgo(
            @PathVariable Long ngoId,
            @RequestBody NgoRequestDto ngoRequestDto) {
        return ResponseEntity.ok(ngoService.updateNgo(ngoId, ngoRequestDto));
    }


    // to delete the Ngo
    @DeleteMapping("/{ngoId}")
    public ResponseEntity<String> deleteNgo(@PathVariable Long ngoId) {
        ngoService.deleteNgo(ngoId);
        return ResponseEntity.ok("NGO deleted successfully.");
    }


}
