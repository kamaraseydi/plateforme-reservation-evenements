package com.seydi.plateformereservationevenements.exception;

public class EventAccessDeniedException extends RuntimeException {
    public EventAccessDeniedException(String message) {
        super(message);
    }
}
