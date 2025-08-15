package com.company.CareBridge.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DonationRequestResponseDto {

    private Long id;
    private String title;
    private String description;
    private String category;
    private Integer quantity;
    private Long ngoId;
    private String ngoName;
    private LocalDateTime createdAt;


    private Long donationRequestId;
    private String donationRequestTitle;

}
