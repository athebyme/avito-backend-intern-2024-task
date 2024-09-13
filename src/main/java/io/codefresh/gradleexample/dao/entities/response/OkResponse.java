package io.codefresh.gradleexample.dao.entities.response;

import org.springframework.http.HttpStatus;

public class OkResponse extends ResponseBase{
    public OkResponse(String reason, HttpStatus status, Object data) {
        super(reason, status, data);
    }
}
