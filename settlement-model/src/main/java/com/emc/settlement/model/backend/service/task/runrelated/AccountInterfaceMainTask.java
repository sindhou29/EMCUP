package com.emc.settlement.model.backend.service.task.runrelated;


public enum AccountInterfaceMainTask {

	DOACCOUNTINGINTERFACE(1, "doAccountingInterface"),
	EXCEPTIONHANDLER(2, "exceptionHandler");

	private Integer key;
	private String value;

	AccountInterfaceMainTask(Integer key, String value) {
		this.key = key;
		this.value = value;
	}

	public Integer getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

}
