package com.kameleoon.TrialTask.exception;

public class RequiredRequestParamIsMissing extends RuntimeException{
    public RequiredRequestParamIsMissing(String message){
        super(message);
    }
}
