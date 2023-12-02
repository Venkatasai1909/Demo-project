package com.card91.wallet.service.Implementations;

import com.card91.wallet.dto.DataWalletDTO;
import com.card91.wallet.dto.WalletDTO;
import com.card91.wallet.exception.DataNotFoundException;
import com.card91.wallet.model.Data;
import com.card91.wallet.model.FileData;
import com.card91.wallet.model.Wallet;
import com.card91.wallet.repository.FileDataRepository;
import com.card91.wallet.service.FileDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class FileDataServiceImpl implements FileDataService {
    private final FileDataRepository fileDataRepository;

    public FileDataServiceImpl(FileDataRepository fileDataRepository) {
        this.fileDataRepository = fileDataRepository;
    }

    @Transactional
    public void createFileData(FileData fileData) {
        fileDataRepository.save(fileData);
    }

    @Transactional
    public FileData getFileDataByFileNumber(Integer fileNumber) {
        Optional<FileData> optionalFileData = fileDataRepository.findById(fileNumber);

        if (optionalFileData.isEmpty()) {
            throw new DataNotFoundException("FileData not found for fileNumber: " + fileNumber + ". Please provide a valid fileNumber.");
        }

        return optionalFileData.get();
    }

    @Transactional
    public List<DataWalletDTO> getAllDataByFileNumber(Integer fileNumber) {
        FileData fileData = getFileDataByFileNumber(fileNumber);
        List<Data> dataList = fileData.getDataList();

        List<DataWalletDTO> dataWalletDTOs = new ArrayList<>();

        for (Data data : dataList) {
            DataWalletDTO dataWalletDto = convertDataToDataWalletDTO(data);

            dataWalletDTOs.add(dataWalletDto);
        }

        return dataWalletDTOs;
    }

    private DataWalletDTO convertDataToDataWalletDTO(Data data) {
        Map<String, WalletDTO> walletInformation = new HashMap<>();
        List<Wallet> wallets = data.getWallets();

        for (Wallet wallet : wallets) {
            WalletDTO walletDto = WalletDTO.builder().
                    walletName(wallet.getWalletName()).
                    walletBalance(wallet.getWalletBalance()).
                    build();

            walletInformation.put(wallet.getWalletId().toString(), walletDto);
        }

        return DataWalletDTO.builder().
                phoneNumber(data.getPhoneNumber()).
                lastFourDigitsOfCard(data.getLastFourDigitsOfCard()).
                walletInformation(walletInformation).
                build();
    }
}