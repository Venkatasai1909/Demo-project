package com.card91.wallet.dto;

import lombok.*;

import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataWalletDTO {
    private Long phoneNumber;
    private Integer lastFourDigitsOfCard;
    private Map<String, WalletDTO> walletInformation;
}
