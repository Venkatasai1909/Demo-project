package com.card91.wallet.service;

import com.card91.wallet.dto.DataWalletDto;
import com.card91.wallet.dto.RequestDto;
import com.card91.wallet.model.Data;

public interface DataService {
    void uploadFile(Data data);

    DataWalletDto getDataFromFileNumber(RequestDto requestDto, Integer fileNumber);

    DataWalletDto getDataByDataId(Integer dataId);

}