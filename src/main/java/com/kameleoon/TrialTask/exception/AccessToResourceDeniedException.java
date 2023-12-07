package com.kameleoon.TrialTask.exception;

public class AccessToResourceDeniedException extends RuntimeException{
    public AccessToResourceDeniedException(String msg){
        super(msg);
    }
}
