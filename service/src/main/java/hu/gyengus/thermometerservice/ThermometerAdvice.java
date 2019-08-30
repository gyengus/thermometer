package hu.gyengus.thermometerservice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import hu.gyengus.thermometerservice.domain.ErrorResponse;
import hu.gyengus.thermometerservice.thermometer.ThermometerException;

@RestControllerAdvice
public class ThermometerAdvice {
    @ExceptionHandler(ThermometerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse thermometerErrorHandler(final ThermometerException e) {
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
