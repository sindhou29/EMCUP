/**
 * 
 */
package com.emc.settlement.model.backend.service.task.scheduled;

/**
 * @author THC
 *
 */
public enum ScheduledMEUCDataVerificationTask {
	
	INITIALIZEVARIABLES(1, "initializeVariables"),
	CREATEEVENT(2, "createEvent"),
	CHECKMENU(3, "checkMEUC"),
	ALERTNOTIFICATION(4, "alertNotification"),
	UPDATEEVENT(5, "updateEvent"),
	GETTRADINGDATESRANGE(6, "getTradingDatesRange"),
	CHECKSCHEDULEANDSHAREPLEX(7, "checkScheduleAndShareplex"),
	CHECKBUSINESSDAY(8, "checkBusinessDay");

	private Integer key;
	private String value;
	
	ScheduledMEUCDataVerificationTask(Integer key, String value) {
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
