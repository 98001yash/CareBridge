package com.company.CareBridge.service;



import com.company.CareBridge.dtos.SignUpDto;
import com.company.CareBridge.dtos.UserDto;
import com.company.CareBridge.entity.Role;
import com.company.CareBridge.entity.User;
import com.company.CareBridge.exceptions.ResourceNotFoundException;
import com.company.CareBridge.exceptions.RuntimeConflictException;
import com.company.CareBridge.repository.RoleRepository;
import com.company.CareBridge.repository.UserRepository;
import com.company.CareBridge.security.JWTService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final RoleRepository roleRepository; // add to constructor

    public String[] login(String email, String password){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email,password)
        );

        User user = (User) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new String[]{accessToken, refreshToken};
    }



    @Transactional
    public UserDto signup(SignUpDto signUpDto) {
        // Check if user with email already exists
        if (userRepository.findByEmail(signUpDto.getEmail()).isPresent()) {
            throw new RuntimeConflictException("User already exists with email: " + signUpDto.getEmail());
        }

        // Map SignUpDto to User entity
        User mappedUser = modelMapper.map(signUpDto, User.class);

        // Fetch DONOR role entity
        Role donorRole = roleRepository.findByName("DONOR")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        // Assign the DONOR role
        mappedUser.setRoles(Set.of(donorRole));

        // Encode the password before saving
        mappedUser.setPassword(passwordEncoder.encode(mappedUser.getPassword()));

        // Save user entity
        User savedUser = userRepository.save(mappedUser);

        // Map saved User entity to UserDto
        UserDto userDto = modelMapper.map(savedUser, UserDto.class);

        // Override roles in DTO with role names (Set<String>)
        userDto.setRoles(savedUser.getRoles()
                .stream()
                .map(Role::getName)  // get role name as String
                .collect(Collectors.toSet()));

        return userDto;
    }




    public String refreshToken(String refreshToken){
        Long userId = jwtService.getUserIdFromToken(refreshToken);

        User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found with id: "+userId));
        return jwtService.generateAccessToken(user);
    }



    //  create a new User LIKE ADMIN, NGO
    // ADMIN AND NGOs are only created by other ADMIN not normal user
    @Transactional
    public UserDto createUserWithRole(SignUpDto signUpDto){
        if(userRepository.findByEmail(signUpDto.getEmail()).isPresent()){
            throw new RuntimeConflictException("User already exists with email: "+signUpDto.getEmail());
        }

        User mappedUser = modelMapper.map(signUpDto, User.class);

        // Get role from Request (ADMIN or NGO)
        if(signUpDto.getRole()==null){
            throw new RuntimeException("Role is required for admin creation");
        }

        Role role = roleRepository.findByName(signUpDto.getRole().toUpperCase())
                .orElseThrow(()->new RuntimeException("Role not found"));

        mappedUser.setRoles(Set.of(role));
        mappedUser.setPassword(passwordEncoder.encode(mappedUser.getPassword()));

        User savedUser = userRepository.save(mappedUser);

        UserDto userDto = modelMapper.map(savedUser, UserDto.class);
        userDto.setRoles(savedUser.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet()));

        return userDto;
    }
}
