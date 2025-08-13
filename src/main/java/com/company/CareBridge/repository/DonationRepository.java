package com.company.CareBridge.repository;

import com.company.CareBridge.entity.Donation;
import com.company.CareBridge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface  DonationRepository extends JpaRepository<Donation,Long> {

    List<Donation> findByDonor(User donor);
    List<Donation> findByNgo(User ngo);
    List<Donation> findByStatus(Donation.DonationStatus status);

    List<Donation> findByDonorAndStatus(User donor, Donation.DonationStatus status);

    List<Donation> findByNgoAndStatus(User ngo, Donation.DonationStatus status);
}
