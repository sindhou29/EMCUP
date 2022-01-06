package com.emc.settlement.model.backend.service.task.fileupload;

public enum JMSListenerMainTask {

	JMSRECEIVER(1, "jmsReceiver");

	private Integer key;
	private String value;

	JMSListenerMainTask(Integer key, String value) {
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
