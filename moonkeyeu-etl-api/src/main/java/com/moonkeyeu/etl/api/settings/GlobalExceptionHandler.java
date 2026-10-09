package com.moonkeyeu.etl.api.settings;

import com.moonkeyeu.etl.api.settings.exceptions.CleanupException;
import com.moonkeyeu.etl.api.settings.exceptions.InvalidStoreOperationException;
import com.moonkeyeu.etl.api.settings.exceptions.InvalidStoreProviderException;
import com.moonkeyeu.etl.api.settings.exceptions.RateLimitExceededException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import static com.moonkeyeu.etl.api.settings.BusinessErrorCodes.*;
import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ExceptionResponse> handleException(RateLimitExceededException exp, WebRequest request) {
        return ResponseEntity
                .status(TOO_MANY_REQUESTS)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(RATE_LIMIT_EXCEED.getCode())
                        .businessErrorDescription(RATE_LIMIT_EXCEED.getDescription())
                        .delay(exp.getDelay())
                        .error(exp.getMessage())
                        .path(request.getDescription(false))
                        .build()
                );
    }

    @ExceptionHandler(value = CleanupException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidCleanupOperationException(CleanupException ex, WebRequest request) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(UNSUPPORTED_OPERATION.getCode())
                        .businessErrorDescription(UNSUPPORTED_OPERATION.getDescription())
                        .error(ex.getMessage())
                        .path(request.getDescription(false))
                        .build()
                );
    }

    @ExceptionHandler(value = InvalidStoreProviderException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidStoreProviderException(InvalidStoreProviderException ex, WebRequest request) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(UNSUPPORTED_PROVIDER.getCode())
                        .businessErrorDescription(UNSUPPORTED_PROVIDER.getDescription())
                        .error(ex.getMessage())
                        .path(request.getDescription(false))
                        .build()
                );
    }

    @ExceptionHandler(value = InvalidStoreOperationException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidStoreOperationException(InvalidStoreProviderException ex, WebRequest request) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(UNSUPPORTED_OPERATION.getCode())
                        .businessErrorDescription(UNSUPPORTED_OPERATION.getDescription())
                        .error(ex.getMessage())
                        .path(request.getDescription(false))
                        .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exp) {
        exp.printStackTrace();
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.builder()
                        .businessErrorDescription("Oops! somthing went wrong, try again later.")
                        .error(exp.getMessage())
                        .build()
                );
    }
}
