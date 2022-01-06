/**
 * 
 */
package com.emc.settlement.model.backend.exceptions;

/**
 * @author DWTN1561
 *
 */
public class AccountValidationException extends BaseException{

	/**
	 * 
	 */
	public AccountValidationException() {
		super();
	}
	
	public String message;
	
	public AccountValidationException(String msg) {
		super(msg);
		this.message = msg;
	}

	@Override
	public Integer getExceptionCode() {
		return ExceptionCode.ACCOUNTVALIDATIONEXCEPTION.getValue();
	}
}
