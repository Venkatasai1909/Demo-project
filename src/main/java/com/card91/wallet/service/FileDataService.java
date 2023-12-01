package com.card91.wallet.service;

import com.card91.wallet.dto.DataWalletDto;
import com.card91.wallet.model.Data;
import com.card91.wallet.model.FileData;

import java.util.List;

public interface FileDataService {
    void createFileData(FileData fileData);

    List<DataWalletDto> getAllDataByFileNumber(Integer fileNumber);

    FileData getDataByFileNumber(Integer fileNumber);

}
