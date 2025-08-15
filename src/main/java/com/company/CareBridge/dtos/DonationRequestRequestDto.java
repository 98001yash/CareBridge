package com.company.CareBridge.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DonationRequestRequestDto {

    private String title;
    private String description;
    private String category;
    private Integer quantity;

    // Optional field: only needed when ADMIN is creating a donation request
    private Long ngoId;
}
