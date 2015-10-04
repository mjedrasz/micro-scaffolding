package com.scaffold.common.rest.exceptions;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.VndErrors.VndError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(SQLException.class)
    @ResponseBody
    public ResponseEntity<VndError> handleSQLException(HttpServletRequest request, Exception ex) {
    	logger.error("SQLException handler executed", ex);
        return createErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURL().toString());
    }

    @ExceptionHandler(IOException.class)
    @ResponseBody
    public ResponseEntity<VndError> handleIOException(HttpServletRequest request, Exception ex) {
        logger.error("IOException handler executed", ex);
        return createErrorResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURL().toString());
    }
    
    @ExceptionHandler(value = {ConstraintViolationException.class, IllegalArgumentException.class})
    @ResponseBody
    public ResponseEntity<VndError> handleConstraintViolationException(HttpServletRequest request, Exception ex) {
    	logger.error("ConstraintViolationException or IllegalArgumentException handler executed", ex);
    	return createErrorResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURL().toString());
    }
            
    @ExceptionHandler(value = {Exception.class, RuntimeException.class, Throwable.class})
    @ResponseBody
    public ResponseEntity<VndError> defaultErrorHandler(HttpServletRequest request, HttpServletResponse response, Exception ex){
      logger.error("DefaultError handler executed", ex);
      return createErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURL().toString());
    }

    private ResponseEntity<VndError> createErrorResponseEntity(HttpStatus httpStatus, String exceptionMessage, String requestUrl){
    	VndError error = new VndError(exceptionMessage, requestUrl);
    	return new ResponseEntity<VndError>(error, httpStatus);	 
    }
}