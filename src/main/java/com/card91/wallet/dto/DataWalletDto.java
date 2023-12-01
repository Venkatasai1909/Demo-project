package com.card91.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataWalletDto {
    private String phoneNumber;
    private String lastFourDigitsOfCard;
    private Map<String, String> walletInformation;
}
