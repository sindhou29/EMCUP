package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;

public class AlertNotification implements Serializable{

	public String ackEbeEveId="";
	public String ackEbeId="";
	public String businessModule="";
	public String cc="";
	public String content="";
	public String destination="";
	public String importance="";
	public String jmsType="";
	public String messageType="";
	public String notfId="";
	public String noticeType="";
	public String notificationChannel="";
	public String recipients="";
	public String requestedQueueName="";
	public String security="";
	public String sender="";
	public String subject="";
	public String triggeredProcess="";

	public boolean ackDBUpdate;
	
	
	public String msgType="";
	public String msgChannel="";
	public String msgSecurity="";
	public Boolean notificationReady = new Boolean(false);
	public String hostNemsDBDetails="";
	public String enQueue="";


	public String getAckEbeEveId() {
		return ackEbeEveId;
	}

	public void setAckEbeEveId(String ackEbeEveId) {
		this.ackEbeEveId = ackEbeEveId;
	}

	public String getAckEbeId() {
		return ackEbeId;
	}

	public void setAckEbeId(String ackEbeId) {
		this.ackEbeId = ackEbeId;
	}

	public String getBusinessModule() {
		return businessModule;
	}

	public void setBusinessModule(String businessModule) {
		this.businessModule = businessModule;
	}

	public String getCc() {
		return _returnEmptyStringForNulls(cc);
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getImportance() {
		return importance;
	}

	public void setImportance(String importance) {
		this.importance = importance;
	}

	public String getJmsType() {
		return jmsType;
	}

	public void setJmsType(String jmsType) {
		this.jmsType = jmsType;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getNotfId() {
		return notfId;
	}

	public void setNotfId(String notfId) {
		this.notfId = notfId;
	}

	public String getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(String noticeType) {
		this.noticeType = noticeType;
	}

	public String getNotificationChannel() {
		return notificationChannel;
	}

	public void setNotificationChannel(String notificationChannel) {
		this.notificationChannel = notificationChannel;
	}

	public String getRecipients() {
		return _returnEmptyStringForNulls(recipients);
	}

	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}

	public String getRequestedQueueName() {
		return requestedQueueName;
	}

	public void setRequestedQueueName(String requestedQueueName) {
		this.requestedQueueName = requestedQueueName;
	}

	public String getSecurity() {
		return security;
	}

	public void setSecurity(String security) {
		this.security = security;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTriggeredProcess() {
		return triggeredProcess;
	}

	public void setTriggeredProcess(String triggeredProcess) {
		this.triggeredProcess = triggeredProcess;
	}

	public boolean isAckDBUpdate() {
		return ackDBUpdate;
	}

	public void setAckDBUpdate(boolean ackDBUpdate) {
		this.ackDBUpdate = ackDBUpdate;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getMsgChannel() {
		return msgChannel;
	}

	public void setMsgChannel(String msgChannel) {
		this.msgChannel = msgChannel;
	}

	public String getMsgSecurity() {
		return msgSecurity;
	}

	public void setMsgSecurity(String msgSecurity) {
		this.msgSecurity = msgSecurity;
	}

	public Boolean getNotificationReady() {
		return notificationReady;
	}

	public void setNotificationReady(Boolean notificationReady) {
		this.notificationReady = notificationReady;
	}

	public String getHostNemsDBDetails() {
		return hostNemsDBDetails;
	}

	public void setHostNemsDBDetails(String hostNemsDBDetails) {
		this.hostNemsDBDetails = hostNemsDBDetails;
	}

	public String getEnQueue() {
		return enQueue;
	}

	public void setEnQueue(String enQueue) {
		this.enQueue = enQueue;
	}
	
	private String _returnEmptyStringForNulls(String input) {
		if (input == null || "null".equalsIgnoreCase(input) || (input != null && input.length() < 1)) {
			return "";
		}
		return input;
	}
	
}
