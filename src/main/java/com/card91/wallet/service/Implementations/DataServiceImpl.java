package com.card91.wallet.service.Implementations;

import com.card91.wallet.dto.DataWalletDto;
import com.card91.wallet.dto.RequestDto;
import com.card91.wallet.model.Data;
import com.card91.wallet.model.FileData;
import com.card91.wallet.model.Wallet;
import com.card91.wallet.repository.DataRepository;

import com.card91.wallet.repository.FileDataRepository;
import com.card91.wallet.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DataServiceImpl implements DataService {
    private final DataRepository dataRepository;

    @Autowired
    public DataServiceImpl(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Transactional
    public void uploadFile(Data data) {
        dataRepository.save(data);
    }

    @Transactional
    public DataWalletDto getDataFromFileNumber(RequestDto requestDto, Integer fileNumber) {
        Data data = dataRepository.findByPhoneNumberAndLastFourDigitsOfCardAndFileData_FileNumber(requestDto.getPhoneNumber(),
                                                                    requestDto.getLastFourDigitsOfCard(),fileNumber);

        if(data == null){
            return null;
        }

        Map<String, String> walletInformation = new HashMap<>();
        List<Wallet> wallets = data.getWallets();

        for(Wallet wallet: wallets){
            walletInformation.put(wallet.getWalletId().toString(), wallet.getWalletName());
        }

        DataWalletDto dataWalletDto = DataWalletDto.builder().
                                      phoneNumber(data.getPhoneNumber()).
                                      lastFourDigitsOfCard(data.getLastFourDigitsOfCard()).
                                      walletInformation(walletInformation).
                                      build();

        return dataWalletDto;
    }

    @Transactional
    public DataWalletDto getDataByDataId(Integer dataId){
        Optional<Data> data = dataRepository.findById(dataId);

        DataWalletDto dataWalletDto = null;

        if(data.isPresent()){
            Map<String, String> walletInformation = new HashMap<>();
            List<Wallet> wallets = data.get().getWallets();

            for(Wallet wallet : wallets){
                walletInformation.put(wallet.getWalletId().toString(), wallet.getWalletName());
            }

            dataWalletDto = DataWalletDto.builder().
                            phoneNumber(data.get().getPhoneNumber()).
                            lastFourDigitsOfCard(data.get().getLastFourDigitsOfCard()).
                            walletInformation(walletInformation).
                            build();

            return dataWalletDto;
        }

        return dataWalletDto;
    }
}