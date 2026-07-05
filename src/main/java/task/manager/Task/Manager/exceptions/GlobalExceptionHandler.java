package task.manager.Task.Manager.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import task.manager.Task.Manager.dto.responses.ErrorResponse;

import java.util.HashMap;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException exception){
        Map<String, String> details = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach( error -> details.put(error.getField(), error.getDefaultMessage()));
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "VALIDATION_ERROR", "Invalid request body", details);
        return ResponseEntity.badRequest().body(response);
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException exception) {
        ErrorResponse response = new ErrorResponse(exception.getStatusCode().value(), exception.getStatusCode().toString(), exception.getReason(), null);

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(response);
    }
}
