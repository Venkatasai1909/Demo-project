package com.card91.wallet.repository;

import com.card91.wallet.model.Data;
import com.card91.wallet.model.FileData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileDataRepository extends JpaRepository<FileData, Integer> {

}
