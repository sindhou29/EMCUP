package com.emc.settlement.model.backend.exceptions;

public abstract class BaseException extends RuntimeException {

	public String message;
	public Integer exceptionCode;

	public BaseException() {
		super();
	}

	public BaseException(String message) {
		super(message);
		this.message = message;
	}

	public BaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public BaseException(Throwable cause) {
		super(cause);
	}

	public String getMessage() {
		return this.message;
	}

	public abstract Integer getExceptionCode();
}
