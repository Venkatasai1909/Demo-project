package com.card91.wallet.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "file")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class FileData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_number_seq")
    @SequenceGenerator(name = "file_number_seq", sequenceName = "file_number_seq", allocationSize = 1, initialValue = 1)
    @Column(name = "file_number", nullable = false)
    private Integer fileNumber;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @OneToMany(targetEntity = Data.class, cascade = CascadeType.ALL, mappedBy = "fileData")
    private List<Data> dataList;
}