package com.company.CareBridge.config;


import com.company.CareBridge.entity.Permission;
import com.company.CareBridge.repository.PermissionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PermissionSeeder {

    private final PermissionRepository permissionRepository;


    @PostConstruct
    public void defaultPermissions(){
        List<String> defaultPermissions = List.of(
                "READ_CASE",
                "CREATE_CASE",
                "UPDATE_CASE",
                "DELETE_CASE",
                "READ_FACILITY",
                "MANAGE_FACILITY",
                "READ_REFERRAL",
                "CREATE_REFERRAL",
                "MANAGE_REFERRAL"
        );

        for(String permName: defaultPermissions){
            if(permissionRepository.findByName(permName).isEmpty()){
                permissionRepository.save(Permission.builder()
                        .name(permName)
                        .build());
            }
        }
    }
}
