package com.card91.wallet.dto;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletDTO {
    private String walletName;
    private BigDecimal walletBalance;
}
