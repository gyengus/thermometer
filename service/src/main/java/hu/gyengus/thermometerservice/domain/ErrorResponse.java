package hu.gyengus.thermometerservice.domain;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
    private final int status;
    private final String message;

    public ErrorResponse(final HttpStatus status, final String message) {
        this.status = status.value();
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
