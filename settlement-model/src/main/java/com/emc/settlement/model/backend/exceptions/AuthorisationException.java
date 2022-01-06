/**
 * 
 */
package com.emc.settlement.model.backend.exceptions;

/**
 * @author DWTN1561
 *
 */
public class AuthorisationException extends BaseException{

	
    public AuthorisationException() {
        super();   
    }

    public AuthorisationException(String msg) {
        super(msg);   
    }

    @Override
    public Integer getExceptionCode() {
        return ExceptionCode.AUTHORISATIONEXCEPTION.getValue();
    }
}
