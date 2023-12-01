package com.card91.wallet.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "data")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Data {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "data_id", nullable = false)
    private Integer dataId;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "last_four-digits_of_card", nullable = false)
    private String lastFourDigitsOfCard;

    @OneToMany(targetEntity = Wallet.class, cascade = CascadeType.ALL, mappedBy = "data")
    private List<Wallet> wallets;

    @ManyToOne(targetEntity = FileData.class)
    @JoinColumn(name = "fileNumber", referencedColumnName = "file_number")
    private FileData fileData;
}