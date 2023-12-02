package com.card91.wallet.controller;

import com.card91.wallet.dto.DataWalletDTO;
import com.card91.wallet.dto.FileUploadResponse;
import com.card91.wallet.service.DataService;
import com.card91.wallet.service.FileDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class WalletController {
    private final DataService dataService;
    private final FileDataService fileDataService;

    @Autowired
    WalletController(DataService dataService, FileDataService fileDataService) {
        this.dataService = dataService;
        this.fileDataService = fileDataService;
    }

    @PostMapping(value = "/data/upload", consumes = "multipart/form-data")
    public ResponseEntity<FileUploadResponse> saveCsvData(@RequestBody MultipartFile file) throws IOException {
        FileUploadResponse fileUploadResponse = dataService.processFileUpload(file);

        return new ResponseEntity<>(fileUploadResponse, HttpStatus.CREATED);
    }

    @GetMapping(value = "/data/file/{fileNumber}", consumes = "multipart/form-data")
    public ResponseEntity<List<DataWalletDTO>> getDataByFileNumber(@PathVariable Integer fileNumber,
                                                             @RequestBody MultipartFile file) throws IOException {
        List<DataWalletDTO> dataWalletDTOS = dataService.fetchDataFromFile(file, fileNumber);

        return new ResponseEntity<>(dataWalletDTOS, HttpStatus.FOUND);
    }

    @GetMapping("/data/{dataId}")
    public ResponseEntity<DataWalletDTO> getDataByDataId(@PathVariable Integer dataId) {
        DataWalletDTO dataWalletDto = dataService.getDataByDataId(dataId);

        return new ResponseEntity<>(dataWalletDto, HttpStatus.FOUND);
    }

    @GetMapping("/data/all/{fileNumber}")
    public ResponseEntity<List<DataWalletDTO>> getAllDataByFileNumber(@PathVariable Integer fileNumber) {
        List<DataWalletDTO> dataWallets = fileDataService.getAllDataByFileNumber(fileNumber);

        return new ResponseEntity<>(dataWallets, HttpStatus.FOUND);
    }
}