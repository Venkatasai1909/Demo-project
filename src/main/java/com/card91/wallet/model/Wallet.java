package com.card91.wallet.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "wallet")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "wallet_id", nullable = false)
    private UUID walletId;

    @Column(name = "wallet_name", nullable = false)
    private String walletName;

    @Column(name = "wallet_balance")
    private BigDecimal walletBalance;

    @ManyToOne(targetEntity = Data.class)
    @JoinColumn(referencedColumnName = "data_id")
    private Data data;
}