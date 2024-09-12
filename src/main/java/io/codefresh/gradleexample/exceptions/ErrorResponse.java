package io.codefresh.gradleexample.exceptions;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse {
    private String reason;

    public ErrorResponse(String reason) {
        this.reason = reason;
    }

}