package com.card91.wallet.exception;

import com.card91.wallet.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFileNotFoundException(FileNotFoundException fileNotFoundException) {
        ErrorResponse errorResponse = ErrorResponse.builder().
                message(fileNotFoundException.getMessage()).
                errorCode("FILE_NOT_FOUND").timestamp(LocalDateTime.now()).
                build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(IncorrectFileFormatException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectFileFormatException(IncorrectFileFormatException incorrectFileFormatException) {
        ErrorResponse errorResponse = ErrorResponse.builder().
                message(incorrectFileFormatException.getMessage()).
                errorCode("INCORRECT_FILE_FORMAT").
                timestamp(LocalDateTime.now()).
                build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedFileExtension.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedFileExtension(UnsupportedFileExtension unsupportedFileExtension) {
        ErrorResponse errorResponse = ErrorResponse.builder().
                message(unsupportedFileExtension.getMessage()).
                errorCode("UNSUPPORTED_FILE_FORMAT").
                timestamp(LocalDateTime.now()).
                build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ioException) {
        ErrorResponse errorResponse = ErrorResponse.builder().
                message(ioException.getMessage()).
                errorCode("IO_ERROR").
                timestamp(LocalDateTime.now()).
                build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDataNotFoundException(DataNotFoundException dataNotFoundException) {
        ErrorResponse errorResponse = ErrorResponse.builder().
                message(dataNotFoundException.getMessage()).
                errorCode("DATA_NOT_FOUND").
                timestamp(LocalDateTime.now()).
                build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleDataAlreadyExistsException(DataAlreadyExistsException dataAlreadyExistsException) {
        ErrorResponse errorResponse = ErrorResponse.builder().
                message(dataAlreadyExistsException.getMessage()).
                errorCode("DATA_ALREADY_EXISTS").
                timestamp(LocalDateTime.now()).
                build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
}
