package com.company.CareBridge.repository;

import com.company.CareBridge.entity.DonationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonationRequestRepository extends JpaRepository<DonationRequest,Long> {
    List<DonationRequest> findByNgoId(Long ngoId);
}
