package com.card91.wallet.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestDTO {
    private Long phoneNumber;
    private Integer lastFourDigitsOfCard;
}
