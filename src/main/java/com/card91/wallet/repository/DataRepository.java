package com.card91.wallet.repository;

import com.card91.wallet.model.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DataRepository extends JpaRepository<Data, Integer> {
    Data findByPhoneNumberAndLastFourDigitsOfCardAndFileData_FileNumber(String phoneNumber, String lastFourDigitsOfCard,
                                                                                    Integer fileNumber);
}