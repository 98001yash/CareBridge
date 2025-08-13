package com.company.CareBridge.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DonationRequestDto {

    private Long ngoId;
    private String itemName;
    private String category;
    private Integer quantity;
    private String description;
}
