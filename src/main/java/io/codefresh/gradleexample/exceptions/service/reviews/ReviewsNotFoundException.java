package io.codefresh.gradleexample.exceptions.service.reviews;

public class ReviewsNotFoundException extends RuntimeException {
    public ReviewsNotFoundException(String message) {
        super(message);
    }
}
