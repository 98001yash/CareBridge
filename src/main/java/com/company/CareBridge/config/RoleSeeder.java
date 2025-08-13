package com.company.CareBridge.config;


import com.company.CareBridge.entity.Role;
import com.company.CareBridge.repository.PermissionRepository;
import com.company.CareBridge.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class RoleSeeder {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @PostConstruct
    public void seedRoles() {
        if (roleRepository.findByName("ADMIN").isEmpty()) {
            Role admin = new Role();
            admin.setName("ADMIN");
            admin.setPermissions(Set.copyOf(permissionRepository.findAll())); // admin gets all permissions
            roleRepository.save(admin);
        }

        if (roleRepository.findByName("NGO").isEmpty()) {
            Role ngo = new Role();
            ngo.setName("NGO");
            ngo.setPermissions(Set.of(
                    permissionRepository.findByName("READ_CASE").orElseThrow(),
                    permissionRepository.findByName("CREATE_REFERRAL").orElseThrow()
            ));
            roleRepository.save(ngo);
        }

        if (roleRepository.findByName("DONOR").isEmpty()) {
            Role donor = new Role();
            donor.setName("DONOR");
            donor.setPermissions(Set.of(
                    permissionRepository.findByName("READ_CASE").orElseThrow()
                    // Add other permissions for DONOR here if needed
            ));
            roleRepository.save(donor);
        }
    }
}
