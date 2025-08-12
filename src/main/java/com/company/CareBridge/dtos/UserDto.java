package com.company.CareBridge.dtos;


import com.company.CareBridge.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserDto {


    private Long id;
    private String username;
    private String email;
    private Set<String> roles;

}
