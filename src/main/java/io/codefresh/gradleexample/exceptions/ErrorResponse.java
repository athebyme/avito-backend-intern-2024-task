package io.codefresh.gradleexample.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Getter
@Setter
public class ErrorResponse extends ResponseEntity<Map<String, String>> {
    private String reason;
    public ErrorResponse(String message, HttpStatus status) {
        super(status);
        this.reason = message;
    }
}
