package io.codefresh.gradleexample.dao.entities.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class ErrorResponse extends ResponseBase {
    public ErrorResponse(String reason, HttpStatus status, Object data)
    {
        super(reason, status, data);
    }

}