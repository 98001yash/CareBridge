package com.company.CareBridge.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DonationRequestResponseDto {

    private String title;
    private String description;
    private String category;
    private Integer quantity;
}
