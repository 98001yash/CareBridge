package com.company.CareBridge.entity;


import com.company.CareBridge.enums.DonationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "donations")
@Builder
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "donor_id",nullable=false)
    private User donor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ngo_id")
    private User ngo;


    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DonationStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate(){
        createdAt = LocalDateTime.now();
        updatedAt= createdAt;
        status= DonationStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum DonationStatus {
        PENDING, APPROVED, COMPLETED, REJECTED
    }
}
