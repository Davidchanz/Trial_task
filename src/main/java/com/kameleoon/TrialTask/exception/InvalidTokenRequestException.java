package com.kameleoon.TrialTask.exception;

public class InvalidTokenRequestException extends RuntimeException {

    public InvalidTokenRequestException(String message) {
        super(message);
    }
}