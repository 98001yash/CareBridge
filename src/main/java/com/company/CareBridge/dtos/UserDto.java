package com.company.CareBridge.dtos;


import com.company.CareBridge.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {


    private Long id;
    private String username;
    private String email;
    private Set<String> roles;

}
