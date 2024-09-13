package io.codefresh.gradleexample.exceptions.service.bids;

public class UserAlreadySentDecisionException extends RuntimeException {
    public UserAlreadySentDecisionException(String message) {
        super(message);
    }
}
