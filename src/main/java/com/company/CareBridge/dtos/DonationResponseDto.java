package com.company.CareBridge.dtos;


import com.company.CareBridge.enums.DonationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DonationResponseDto {

    private Long id;
    private String donorName;
    private String ngoName;
    private String itemName;
    private String category;
    private Integer quantity;
    private String description;
    private DonationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
