package com.card91.wallet.service;

import com.card91.wallet.dto.DataWalletDTO;
import com.card91.wallet.dto.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DataService {

    DataWalletDTO getDataByDataId(Integer dataId);

    FileUploadResponse processFileUpload(MultipartFile file) throws IOException;

    List<DataWalletDTO> fetchDataFromFile(MultipartFile file, Integer fileNumber) throws IOException;

}