package com.luckeat.luckeatbackend.common.exception.reservation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReservationNotFoundException extends RuntimeException {
    
    public ReservationNotFoundException(String message) {
        super(message);
    }
    
    public ReservationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 