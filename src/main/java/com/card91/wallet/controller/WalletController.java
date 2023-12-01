package com.card91.wallet.controller;

import com.card91.wallet.dto.DataWalletDto;
import com.card91.wallet.dto.FileUploadResponse;
import com.card91.wallet.dto.RequestDto;
import com.card91.wallet.model.Data;
import com.card91.wallet.model.FileData;
import com.card91.wallet.model.Wallet;
import com.card91.wallet.service.DataService;
import com.card91.wallet.service.FileDataService;
import com.card91.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/user")
public class WalletController {
    private final DataService dataService;
    private final WalletService walletService;
    private final FileDataService fileDataService;

    @Autowired
    WalletController(DataService dataService, WalletService walletService, FileDataService fileDataService) {
        this.dataService = dataService;
        this.walletService = walletService;
        this.fileDataService = fileDataService;
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<FileUploadResponse> saveCsvData(@RequestBody MultipartFile file) throws IOException {
        FileUploadResponse fileUploadResponse = FileUploadResponse.builder().build();

        if (file == null || file.isEmpty()) {

            return new ResponseEntity<>(fileUploadResponse, HttpStatus.NO_CONTENT);
        } else if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
            fileUploadResponse.setMessage("only .csv files accepted");

            return new ResponseEntity<>(fileUploadResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        List<Data> dataList = new ArrayList<>();

        FileData fileData = FileData.builder().
                fileName(file.getOriginalFilename()).
                build();

        fileDataService.createFileData(fileData);

        try (InputStream inputStream = file.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            String column_names = reader.readLine();
            String[] columns = column_names.split(",");

            if (columns.length >= 2) {
                if (!(columns[0].equalsIgnoreCase("phone_number") &&
                        columns[1].equalsIgnoreCase("last_four_digits_of_card_number"))) {
                    fileUploadResponse.setMessage("Incorrect format file");

                    return new ResponseEntity<>(fileUploadResponse, HttpStatus.BAD_REQUEST);
                }
            } else {
                fileUploadResponse.setMessage("Incorrect format file");

                return new ResponseEntity<>(fileUploadResponse, HttpStatus.BAD_REQUEST);
            }

            while ((line = reader.readLine()) != null) {
                String[] record = line.split(",");

                int recordLength = record.length;
                int index = 2;

                Data data = Data.builder().
                        phoneNumber(record[0].trim()).
                        lastFourDigitsOfCard(record[1].trim()).
                        build();

                if (recordLength > 2) {
                    List<Wallet> walletList = new ArrayList<>();

                    dataService.uploadFile(data);

                    while (index < recordLength) {
                        Wallet wallet = Wallet.builder().
                                walletName(record[index].trim()).
                                data(data).build();

                        walletService.saveWallet(wallet);
                        walletList.add(wallet);

                        index++;
                    }

                    data.setWallets(walletList);
                    dataList.add(data);
                    data.setFileData(fileData);

                    dataService.uploadFile(data);
                } else {
                    fileUploadResponse.setMessage("File is not in format");

                    return new ResponseEntity<>(fileUploadResponse, HttpStatus.BAD_REQUEST);
                }
            }
        } catch (IOException ioException) {
            fileUploadResponse.setMessage(ioException.getMessage());

            return new ResponseEntity<>(fileUploadResponse, HttpStatus.CONFLICT);
        }

        fileData.setDataList(dataList);
        fileDataService.createFileData(fileData);

        fileUploadResponse.setFileName(fileData.getFileName());
        fileUploadResponse.setFileNumber(fileData.getFileNumber());
        fileUploadResponse.setMessage("File uploaded successfully");

        return new ResponseEntity<>(fileUploadResponse, HttpStatus.CREATED);
    }

    @GetMapping(value = "/data/file/{fileNumber}", consumes = "multipart/form-data")
    public ResponseEntity<DataWalletDto> getDataInFileNumber(@PathVariable Integer fileNumber,
                                                             @RequestBody MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        DataWalletDto dataWalletDto = null;

        FileData fileData = fileDataService.getDataByFileNumber(fileNumber);

        if (fileData == null) {
            return new ResponseEntity<>(dataWalletDto, HttpStatus.NO_CONTENT);
        }

        try (InputStream inputStream = file.getInputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            bufferedReader.readLine();

            String line = bufferedReader.readLine();
            String[] record = line.split(",");

            RequestDto requestDto = RequestDto.builder().
                    phoneNumber(record[0].trim()).
                    lastFourDigitsOfCard(record[1].trim()).
                    build();

            dataWalletDto = dataService.getDataFromFileNumber(requestDto, fileNumber);

            if (dataWalletDto == null) {
                return new ResponseEntity<>(dataWalletDto, HttpStatus.NOT_FOUND);
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        return new ResponseEntity<>(dataWalletDto, HttpStatus.FOUND);
    }

    @GetMapping("/data/{dataId}")
    public ResponseEntity<DataWalletDto> getDataByDataId(@PathVariable Integer dataId) {
        DataWalletDto dataWalletDto = dataService.getDataByDataId(dataId);

        if (dataWalletDto == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(dataWalletDto, HttpStatus.FOUND);
    }

    @GetMapping("/data/file/{fileNumber}")
    public ResponseEntity<List<DataWalletDto>> getAllDataInFileNumber(@PathVariable Integer fileNumber) {
        FileData fileData = fileDataService.getDataByFileNumber(fileNumber);
        List<DataWalletDto> dataWalletDtos = null;

        if (fileData == null) {
            return new ResponseEntity<>(dataWalletDtos, HttpStatus.NOT_FOUND);
        }

        dataWalletDtos = fileDataService.getAllDataByFileNumber(fileNumber);

        return new ResponseEntity<>(dataWalletDtos, HttpStatus.FOUND);
    }
}