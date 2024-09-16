package io.codefresh.gradleexample.exceptions.service.bids;

public class DecisionIsAlreadyCompletedException extends RuntimeException {
    public DecisionIsAlreadyCompletedException(String message) {
        super(message);
    }
}
