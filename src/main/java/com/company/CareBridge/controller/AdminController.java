package com.company.CareBridge.controller;


import com.company.CareBridge.dtos.SignUpDto;
import com.company.CareBridge.dtos.UserDto;
import com.company.CareBridge.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {


    private final AuthService authService;


    @PostMapping("/create-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> createUser(@RequestBody SignUpDto signUpDto){
        return ResponseEntity.ok(authService.createUserWithRole(signUpDto));
    }
}
