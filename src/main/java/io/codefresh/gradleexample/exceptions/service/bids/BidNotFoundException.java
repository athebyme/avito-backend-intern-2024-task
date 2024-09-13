package io.codefresh.gradleexample.exceptions.service.bids;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BidNotFoundException extends BidsExceptionsBase{
    public BidNotFoundException(String message) {
        super(message);
    }
}
