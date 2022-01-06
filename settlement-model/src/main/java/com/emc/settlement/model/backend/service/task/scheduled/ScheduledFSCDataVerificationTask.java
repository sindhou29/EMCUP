/**
 * 
 */
package com.emc.settlement.model.backend.service.task.scheduled;

/**
 * @author THC
 *
 */
public enum ScheduledFSCDataVerificationTask {
	
	INITIALIZESETTLEMENTDATE(1, "initializeSettlementDate"),
	SENDNOTIFICATION(2, "sendNotification"),
	UPDATEEVENT(3, "updateEvent"),
	CHECKCUTOFFTIME(4, "checkCutoffTime");

	private Integer key;
	private String value;
	
	ScheduledFSCDataVerificationTask(Integer key, String value) {
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
