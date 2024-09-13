package io.codefresh.gradleexample.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse {
    @JsonProperty("reason")
    private String reason;

    public ErrorResponse(String reason) {
        this.reason = reason;
    }

}