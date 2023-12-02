package com.card91.wallet.service.Implementations;

import com.card91.wallet.dto.DataWalletDTO;
import com.card91.wallet.dto.FileUploadResponse;
import com.card91.wallet.dto.RequestDTO;
import com.card91.wallet.dto.WalletDTO;
import com.card91.wallet.exception.DataAlreadyExistsException;
import com.card91.wallet.exception.DataNotFoundException;
import com.card91.wallet.exception.IncorrectFileFormatException;
import com.card91.wallet.exception.UnsupportedFileExtension;
import com.card91.wallet.model.Data;
import com.card91.wallet.model.FileData;
import com.card91.wallet.model.Wallet;
import com.card91.wallet.repository.DataRepository;
import com.card91.wallet.service.DataService;
import com.card91.wallet.service.FileDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

@Service
public class DataServiceImpl implements DataService {
    private final DataRepository dataRepository;
    private final FileDataService fileDataService;
    private final Integer PHONE_NUMBER = 0;
    private final Integer LAST_FOUR_CARD_DIGITS = 1;

    @Autowired
    public DataServiceImpl(DataRepository dataRepository, FileDataService fileDataService) {
        this.dataRepository = dataRepository;
        this.fileDataService = fileDataService;
    }

    @Transactional
    public List<DataWalletDTO> fetchDataFromFile(MultipartFile file, Integer fileNumber) throws IOException {
        validateFileFormat(file);

        List<DataWalletDTO> dataWalletDTOS = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            validateFileContent(bufferedReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] record = line.split(",");

                String phone = removeQuotes(record[PHONE_NUMBER]);
                String lastFourCardDigits = removeQuotes(record[LAST_FOUR_CARD_DIGITS]);

                validatePhoneNumberAndCardNumber(phone, lastFourCardDigits);

                Long phoneNumber = Long.parseLong(phone);
                Integer cardNumber = Integer.parseInt(lastFourCardDigits);

                RequestDTO requestDTO = RequestDTO.builder().
                        phoneNumber(phoneNumber).
                        lastFourDigitsOfCard(cardNumber).
                        build();

                DataWalletDTO dataWalletDTO = getDataFromFileNumber(requestDTO, fileNumber);

                dataWalletDTOS.add(dataWalletDTO);
            }
        } catch (IOException ioException) {
            throw new IOException("Error processing the file: " + ioException.getMessage(), ioException);
        }

        return dataWalletDTOS;
    }

    @Transactional
    public DataWalletDTO getDataByDataId(Integer dataId) {
        Optional<Data> data = dataRepository.findById(dataId);

        if (data.isEmpty()) {
            throw new DataNotFoundException("No data found for ID: " + dataId);
        }

        return convertDataToDataWalletDTO(data.get());
    }

    @Transactional
    public FileUploadResponse processFileUpload(MultipartFile file) throws IOException {
        validateFileFormat(file);

        FileData fileData = FileData.builder().
                fileName(file.getOriginalFilename()).
                dataList(new ArrayList<>()).
                build();

        fileDataService.createFileData(fileData);

        try (InputStream inputStream = file.getInputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            validateFileContent(bufferedReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Data data = processData(line, fileData);

                fileData.getDataList().add(data);
            }

            fileDataService.createFileData(fileData);

        } catch (IOException ioException) {
            throw new IOException("Error processing the file: " + ioException.getMessage(), ioException);
        }

        return FileUploadResponse.builder().
                fileName(file.getOriginalFilename()).
                fileNumber(fileData.getFileNumber()).
                message("File uploaded successfully").
                build();
    }

    private void validateFileFormat(MultipartFile file) throws FileNotFoundException {
        if (file == null) {
            throw new FileNotFoundException("No file provided. Please upload a valid CSV file.");
        } else if (file.isEmpty() || file.getSize() == 0) {
            throw new FileNotFoundException("The provided file is empty. Please upload a non-empty CSV file.");
        } else if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
            String fileName = file.getOriginalFilename();

            assert fileName != null;
            int index = fileName.indexOf('.');
            String fileExtension = fileName.substring(index);

            throw new UnsupportedFileExtension("Unsupported file type: " + fileName +
                    ". Only CSV files are supported. Found file extension: " + fileExtension);
        }
    }

    private void validateFileContent(BufferedReader bufferedReader) throws IOException {
        String columnNames = bufferedReader.readLine();
        String[] columns = columnNames.split(",");

        if (columns.length < 2 || !columns[PHONE_NUMBER].trim().equalsIgnoreCase("phone_number") ||
                !columns[LAST_FOUR_CARD_DIGITS].trim().equalsIgnoreCase("last_four_digits_of_card_number")) {

            throw new IncorrectFileFormatException("Incorrect file format. " +
                    "The file must contain at least two columns with headers: " +
                    "phone_number' and 'last_four_digits_of_card_number'.");
        }
    }

    private Data processData(String line, FileData fileData) throws IOException {
        String[] record = line.split(",");
        int recordLength = record.length;

        if (recordLength <= 2) {
            throw new IncorrectFileFormatException("Incorrect file format. " +
                    "Expected at least 4 values in the line, but found only " + recordLength + " values.");
        }

        String phone = removeQuotes(record[PHONE_NUMBER]);
        String lastFourCardDigits = removeQuotes(record[LAST_FOUR_CARD_DIGITS]);

        validatePhoneNumberAndCardNumber(phone, lastFourCardDigits);


        Long phoneNumber = Long.parseLong(phone);
        Integer cardNumber = Integer.parseInt(lastFourCardDigits);

        Data data = Data.builder().
                phoneNumber(phoneNumber).
                lastFourDigitsOfCard(cardNumber).
                wallets(new ArrayList<>()).
                fileData(fileData).
                build();

        Data existingData = dataRepository.
                findByPhoneNumberAndLastFourDigitsOfCardAndFileData_FileNumber(phoneNumber, cardNumber,
                        fileData.getFileNumber());

        if (existingData != null) {
            throw new DataAlreadyExistsException("Data already exists for phone number " +
                    phoneNumber + ", last four digits of card " + cardNumber +
                    ", and file number " + fileData.getFileNumber());
        }

        int walletNumber = 2;

        while (walletNumber < recordLength) {
            String walletName = removeQuotes(record[walletNumber]);

            if (walletNumber + 1 < recordLength) {
                try {
                    BigDecimal walletBalance = BigDecimal.valueOf(Double.parseDouble(record[walletNumber + 1]));

                    data.getWallets().add(createWallet(walletName, walletBalance, data));
                } catch (IncorrectFileFormatException incorrectFileFormatException) {
                    throw new IncorrectFileFormatException("Invalid wallet balance format for wallet: " + walletName);
                }
            } else {
                throw new IncorrectFileFormatException("Missing wallet balance for wallet: " + walletName);
            }

            walletNumber += 2;
        }

        uploadFile(data);

        return data;
    }

    private Wallet createWallet(String walletName, BigDecimal walletBalance, Data data) {

        return Wallet.builder().
                walletName(walletName).
                walletBalance(walletBalance).
                data(data).
                build();
    }

    private String removeQuotes(String value) {
        return value.replace("\"", "").trim();
    }

    private void validatePhoneNumberAndCardNumber(String phoneNumber, String cardNumber) {
        try {
            Long.parseLong(phoneNumber);
            Integer.parseInt(cardNumber);

            if (phoneNumber.length() != 10 || cardNumber.length() != 4 || !phoneNumber.matches("[6-9]\\d{9}")) {
                throw new IncorrectFileFormatException("Invalid input format. Please ensure that:\n" +
                        "- Phone number starts with 6, 7, 8, or 9.\n" +
                        "- Phone number is exactly 10 digits.\n" +
                        "- Card number is exactly 4 digits.");
            }
        } catch (NumberFormatException numberFormatException) {
            numberFormatException.printStackTrace();
        }
    }

    private void uploadFile(Data data) {
        dataRepository.save(data);
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

    private DataWalletDTO getDataFromFileNumber(RequestDTO requestDTO, Integer fileNumber) {
        Data data = dataRepository.findByPhoneNumberAndLastFourDigitsOfCardAndFileData_FileNumber(requestDTO.getPhoneNumber(),
                requestDTO.getLastFourDigitsOfCard(), fileNumber);

        if (data == null) {
            throw new DataNotFoundException("No data found for phoneNumber: " + requestDTO.getPhoneNumber() +
                    ", lastFourDigitsOfCard: " + requestDTO.getLastFourDigitsOfCard() +
                    ", and fileNumber: " + fileNumber);
        }

        return convertDataToDataWalletDTO(data);
    }
}