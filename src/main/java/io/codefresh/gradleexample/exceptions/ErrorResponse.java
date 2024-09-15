package io.codefresh.gradleexample.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse{
    @JsonProperty("reason")
    private String reason;
    public ErrorResponse(String message) {
        this.reason = message;
    }
    public ResponseEntity<ErrorResponse> toResponseEntity(HttpStatus status) {
        return new ResponseEntity<>(this, status);
    }
}
