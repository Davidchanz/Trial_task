package com.kameleoon.TrialTask.exception;

public class QuoteNotFoundException extends RuntimeException{
    public QuoteNotFoundException(String msg){
        super(msg);
    }
}
