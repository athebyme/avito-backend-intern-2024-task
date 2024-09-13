package io.codefresh.gradleexample.dao.entities.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public abstract class ResponseBase {
    private String reason;
    private HttpStatus status;
    private Object data;
}
