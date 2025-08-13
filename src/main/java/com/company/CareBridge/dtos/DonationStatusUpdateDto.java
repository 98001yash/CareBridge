package com.company.CareBridge.dtos;


import com.company.CareBridge.enums.DonationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DonationStatusUpdateDto {

    private DonationStatus status;
}
