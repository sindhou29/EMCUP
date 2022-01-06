package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;

public class Message  implements Serializable{

	public String jmsType;
	public String correlationId;
	public String destination;
	public String messageId;
	public String replyTo;
	public String textValue;
}
