package com.card91.wallet.repository;

import com.card91.wallet.model.Data;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataRepository extends JpaRepository<Data, Integer> {
    Data findByPhoneNumberAndLastFourDigitsOfCardAndFileData_FileNumber(Long phoneNumber, Integer lastFourDigitsOfCard,
                                                                        Integer fileNumber);
}