package com.card91.wallet.service;

import com.card91.wallet.dto.DataWalletDTO;
import com.card91.wallet.model.FileData;

import java.util.List;

public interface FileDataService {
    void createFileData(FileData fileData);

    List<DataWalletDTO> getAllDataByFileNumber(Integer fileNumber);

    FileData getFileDataByFileNumber(Integer fileNumber);

}
