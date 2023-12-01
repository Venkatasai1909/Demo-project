package com.card91.wallet.service.Implementations;

import com.card91.wallet.model.Wallet;
import com.card91.wallet.repository.WalletRepository;

import com.card91.wallet.service.WalletService;
import org.springframework.stereotype.Service;

@Service
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;

    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public void saveWallet(Wallet wallet) {
        walletRepository.save(wallet);
    }
}
