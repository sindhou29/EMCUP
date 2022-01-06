package com.emc.sett.common;

public class SettlementRunException extends Exception {

	/**
	 * Variable to hold the serialVesion id of the class
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default Constructor
	 */
	public SettlementRunException() {
		super();
	}

	/**
	 * Parameterized Constructor
	 * 
	 * @param errorMessage
	 */
	public SettlementRunException(String errorMessage) {
		super(errorMessage);
	}

	/**
	 * Parameterized Constructor
	 * 
	 * @param errorMessage
	 */
	public SettlementRunException(String errorMessage, String step) {
		super(errorMessage + "-" + step);
	}
	
	/**
	 * Parameterized Constructor
	 * 
	 * @param errorMessage
	 */
	public SettlementRunException(String message, String step, Throwable cause) {
		super(message + "-" + step, cause);
	}
	/**
	 * Parameterized Constructor
	 * 
	 * @param errorMessage
	 */
	public SettlementRunException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Parameterized Constructor
	 * 
	 * @param errorMessage
	 */
	public SettlementRunException(Throwable error) {
		super(error);		 
	}
}
