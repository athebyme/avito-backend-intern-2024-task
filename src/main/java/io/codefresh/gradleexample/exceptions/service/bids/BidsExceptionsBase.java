package io.codefresh.gradleexample.exceptions.service.bids;

public abstract class BidsExceptionsBase extends RuntimeException {
    public BidsExceptionsBase(String message) {
        super(message);
    }
}
