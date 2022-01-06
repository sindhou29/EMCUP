package com.emc.settlement.model.backend.service.task.runrelated;


public enum CaptureFSCPenaltyQuantityTask {

	ISFSCEFFECTIVE(1, "isFSCEffective"),
	CAPTUREFSCPENALTYQTY(2, "captureFSCPenaltyQty"),
	UPDATEEVENT(3, "updateEvent"),
	EXCEPTIONHANDLER(4, "exceptionHandler");

	private Integer key;
	private String value;

	CaptureFSCPenaltyQuantityTask(Integer key, String value) {
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
