package com.card91.wallet.service.Implementations;

import com.card91.wallet.dto.DataWalletDto;
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

    public FileDataServiceImpl(FileDataRepository fileDataRepository){
        this.fileDataRepository = fileDataRepository;
    }
    @Transactional
    public void createFileData(FileData fileData) {
        fileDataRepository.save(fileData);
    }

    @Transactional
    public FileData getDataByFileNumber(Integer fileNumber){
        Optional<FileData> optionalFileData = fileDataRepository.findById(fileNumber);

        if(optionalFileData.isPresent()){
            return optionalFileData.get();
        }

        return null;
    }

    @Transactional
    public List<DataWalletDto> getAllDataByFileNumber(Integer fileNumber){
        Optional<FileData> optionalData = fileDataRepository.findById(fileNumber);
        List<DataWalletDto> dataWalletDtos = new ArrayList<>();

        if(optionalData.isPresent()){
            List<Data> dataList = optionalData.get().getDataList();

            for(Data data : dataList){
                List<Wallet> wallets = data.getWallets();

                Map<String, String> walletInformation = new HashMap<>();

                for(Wallet wallet: wallets){
                    walletInformation.put(wallet.getWalletId().toString(), wallet.getWalletName());
                }

                DataWalletDto dataWalletDto = DataWalletDto.builder().
                                              phoneNumber(data.getPhoneNumber()).
                                              lastFourDigitsOfCard(data.getLastFourDigitsOfCard()).
                                              walletInformation(walletInformation).
                                              build();

                dataWalletDtos.add(dataWalletDto);
            }

            return dataWalletDtos;
        }

        return null;
    }
}