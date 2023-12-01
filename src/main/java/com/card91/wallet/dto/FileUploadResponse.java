package com.card91.wallet.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileUploadResponse {
    private Integer fileNumber;
    private String fileName;
    private String message;
}
