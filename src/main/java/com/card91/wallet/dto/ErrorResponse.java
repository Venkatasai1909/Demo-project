package com.card91.wallet.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {
    private String message;
    private String errorCode;
    private LocalDateTime timestamp;
}
