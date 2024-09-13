package io.codefresh.gradleexample.exceptions.service.bids;

public class ReviewAlreadySentException extends RuntimeException {
    public ReviewAlreadySentException(String message) {
        super(message);
    }
}
