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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "data_id_seq")
    @SequenceGenerator(name = "data_id_seq", sequenceName = "data_id_seq", allocationSize = 1, initialValue = 1)
    @Column(name = "data_id", nullable = false)
    private Integer dataId;

    @Column(name = "phone_number", nullable = false)
    private Long phoneNumber;

    @Column(name = "last_four_digits_of_card", nullable = false)
    private Integer lastFourDigitsOfCard;

    @OneToMany(targetEntity = Wallet.class, cascade = CascadeType.ALL, mappedBy = "data")
    private List<Wallet> wallets;

    @ManyToOne(targetEntity = FileData.class)
    @JoinColumn(name = "fileNumber", referencedColumnName = "file_number")
    private FileData fileData;
}