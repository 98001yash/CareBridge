package com.company.CareBridge.service;



import com.company.CareBridge.dtos.SignUpDto;
import com.company.CareBridge.dtos.UserDto;
import com.company.CareBridge.entity.Ngo;
import com.company.CareBridge.entity.Role;
import com.company.CareBridge.entity.User;
import com.company.CareBridge.exceptions.ResourceNotFoundException;
import com.company.CareBridge.exceptions.RuntimeConflictException;
import com.company.CareBridge.repository.NgoRepository;
import com.company.CareBridge.repository.RoleRepository;
import com.company.CareBridge.repository.UserRepository;
import com.company.CareBridge.security.JWTService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.hibernate.ResourceClosedException;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final RoleRepository roleRepository; // add to constructor
    private final NgoRepository ngoRepository;

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
        public UserDto createUserWithRole(SignUpDto signUpDto) throws BadRequestException {
            log.info("Attempting to create user with email: {}", signUpDto.getEmail());

            // Check if email already exists
            userRepository.findByEmail(signUpDto.getEmail()).ifPresent(user -> {
                log.warn("User already exists with email: {}", signUpDto.getEmail());
                throw new RuntimeConflictException("User already exists with email: " + signUpDto.getEmail());
            });

            // Validate role
            if (signUpDto.getRole() == null || signUpDto.getRole().isBlank()) {
                log.error("Role is missing in signup request for email: {}", signUpDto.getEmail());
                throw new BadRequestException("Role is required for user creation");
            }

            // Map DTO to Entity
            User mappedUser = modelMapper.map(signUpDto, User.class);

            // Fetch role from DB
            Role role = roleRepository.findByName(signUpDto.getRole().toUpperCase())
                    .orElseThrow(() -> {
                        log.error("Role '{}' not found in database", signUpDto.getRole());
                        return new ResourceClosedException("Role not found: " + signUpDto.getRole());
                    });

            // Set user role
            mappedUser.setRoles(Set.of(role));

            // Encode password
            mappedUser.setPassword(passwordEncoder.encode(mappedUser.getPassword()));

            // Save user
            User savedUser = userRepository.save(mappedUser);
            log.info("User created successfully with id: {} and role: {}", savedUser.getId(), role.getName());

            // Map back to DTO
            UserDto userDto = modelMapper.map(savedUser, UserDto.class);
            userDto.setRoles(savedUser.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet()));

            return userDto;
        }
}
