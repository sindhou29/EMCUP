package com.emc.settlement.model.backend.exceptions;

public class SettRunInfoException extends BaseException{

    public SettRunInfoException() {
        super();   
    }

    public SettRunInfoException(String msg) {
        super(msg);   
    }

    @Override
    public Integer getExceptionCode() {
        return ExceptionCode.SETTRUNINFOEXCEPTION.getValue();
    }
}
