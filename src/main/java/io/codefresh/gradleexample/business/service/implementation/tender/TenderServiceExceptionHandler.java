package io.codefresh.gradleexample.business.service.implementation.tender;

import io.codefresh.gradleexample.business.service.TenderServiceInterface;
import io.codefresh.gradleexample.exceptions.ErrorResponse;
import io.codefresh.gradleexample.exceptions.service.EmployeeHasNoResponsibleException;
import io.codefresh.gradleexample.exceptions.service.EmployeeNotFoundException;
import io.codefresh.gradleexample.exceptions.service.TenderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice(assignableTypes = TenderServiceInterface.class)
public class TenderServiceExceptionHandler {

    @ExceptionHandler(TenderNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleTenderNotFoundException(TenderNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Tender not found");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(EmployeeNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleEmployeeNotFoundException(EmployeeNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Пользователь не существует или некорректен.");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmployeeHasNoResponsibleException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleEmployeeHasNoResponsible(EmployeeHasNoResponsibleException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Недостаточно прав для выполнения действия");
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}