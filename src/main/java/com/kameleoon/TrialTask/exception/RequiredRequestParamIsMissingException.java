package com.kameleoon.TrialTask.exception;

public class RequiredRequestParamIsMissingException extends RuntimeException{
    public RequiredRequestParamIsMissingException(String message){
        super(message);
    }
}
