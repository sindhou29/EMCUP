package com.emc.settlement.common;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.emc.settlement.model.backend.pojo.AlertNotification;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AlertNotificationImpl {

	protected static final Logger logger = Logger.getLogger(AlertNotificationImpl.class);
	
	@Autowired
	private UtilityFunctions utilityFunctions;
	
    @Autowired
	private JdbcTemplate jdbcTemplate;

	public AlertNotificationImpl(UtilityFunctions utilityFunctions) {
		this.utilityFunctions = utilityFunctions;
	}

	public void sendAckUsapToMSSLviaAQ(AlertNotification notification)
	{
		logger.log(Priority.INFO,"[EMC] Starting EMC.AlertNotification.sendAckUsapToMSSLviaAQ() - notification :"+notification);

		try {
			
		    String noticeTypeId = null;
		    String msgType = "EMCMSSLData";
		    String msgChannel = "webservice";
		    String msgSecurity = "NONSSL";
		    notification.setNotfId(utilityFunctions.getEveId()); 
		    notification.setSender("");
		    String sqlCommand = "SELECT ID FROM nem.nem_settlement_notice_types " + 
		    "where notice_type_description = '" + notification.getNoticeType() + "'";

		    List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
		    for (Map row : list) {
		    	noticeTypeId = (String)row.get("ID");
		        break;
		    }
			
		    if (noticeTypeId == null) {
		        throw new Exception( "Cannot find the notice type: " + notification.getNoticeType());
		    }

		    Object[] params = new Object[11];
		    sqlCommand = "INSERT INTO nem.nem_settlement_notifications " + 
		                 "(ID, notice_type_id, MESSAGE_TYPE, message_channel, message_security, " + 
		                 "subject, BODY, importance, addr_from, addr_to, addr_cc, status, " + 
		                 "created_date) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,'P',SYSDATE )";
		    params[0] = notification.getNotfId();
		    params[1] = noticeTypeId;
		    params[2] = msgType;
		    params[3] = msgChannel;
		    params[4] = msgSecurity;
		    params[5] = "";
		    params[6] = "<![CDATA[" + notification.getContent() + "]]>";
		    params[7] = notification.getImportance();
		    params[8] = "";
		    params[9] = "";
		    params[10] = "";
		    jdbcTemplate.update(sqlCommand, params);

		    String content = notification.getContent().replace( "\\n",  "\n");
		    String xmlMsg = "<Notification>\n" + 
		    "<Type>" + msgType + "</Type>\n" + 
		    "<Channel>" + msgChannel + "</Channel>\n" + 
		    "<Security>" + msgSecurity + "</Security>\n" + 
		    "<Message>\n" + 
		    "<type>" + notification.getJmsType() + "</type>\n" + 
		    "<msg>" + "<![CDATA[" + content + "]]>" + "</msg>\n" + 
		    "</Message>\n" + 
		    "<Destination>" + notification.getDestination() + "</Destination>\n" + 
		    "<Priority>" + notification.getImportance() + "</Priority>\n" + 
		    "</Notification>";
		    notification.setRequestedQueueName("omsfo.aq_notification_queue");

		    // omsfo.aq_messages_queue - SRP by HCL modified
		    logger.log(Priority.INFO,"[EMC] ACK/USAP Message to Oracle AQ: xmlMsg [" + xmlMsg + "]");

		    try (Connection conn = jdbcTemplate.getDataSource().getConnection();
				 CallableStatement cstmt = conn.prepareCall("{call NEM.NEM$AQ_SERVICES.AQ_ENQUEUE_MESSAGE(?,?)}")){
		        // call NEMAQ_SERVICESAQ_ENQUEUE_MESSAGE
		        // using ppayloadchar = xmlMsg, 
		        // pqueuename = requestedQueueName
		        // Using SSR specific package to call Oracle AQ to avoid unwanted characters

				cstmt.setString(1, xmlMsg);
				cstmt.setString(2, notification.getRequestedQueueName());
				cstmt.executeUpdate();
					
					/*SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withSchemaName("SEBO")
					.withCatalogName("SEB$AQ_SERVICES")
					.withProcedureName("SSR_AQ_ENQUEUE_MESSAGE")
					.useInParameterNames("p_payload_char","p_queue_name")
					.declareParameters(new SqlInOutParameter("p_payload_char", OracleTypes.VARCHAR), 
							new SqlInOutParameter("p_queue_name", OracleTypes.VARCHAR));
					SqlParameterSource in = new MapSqlParameterSource()
							.addValue("p_payload_char", xmlMsg)
							.addValue("p_queue_name", notification.getRequestedQueueName());
					jdbcCall.execute(in);*/
		    }
		    catch (Exception e) {
		        // Update nem_settlement_notifications Status to "F" (Fail)
		        sqlCommand = "UPDATE nem.nem_settlement_notifications SET status = 'F', " + 
		                     "modified_date = SYSDATE WHERE id = '" + notification.getNotfId() + "'";

		        jdbcTemplate.update(sqlCommand, new Object[] {});
		        throw e;
		    }

		    // Update nem_settlement_notifications Status to "S" (Success)
		    sqlCommand = "UPDATE nem.nem_settlement_notifications SET status = 'S', " + 
		                 "modified_date = SYSDATE WHERE id = '" + notification.getNotfId() + "'";

		    jdbcTemplate.update(sqlCommand, new Object[] {});

	        if (notification.ackDBUpdate == true) {
		    //if ( true) {
		        logger.log(Priority.INFO,"[" + notification.getJmsType() + "] EMC.AlertNotification.sendAckUsapToMSSLviaAQ() : Updating NEM.NEM_EBT_EVENTS.ACK_SENT_YN.");

		        //  update DB Table for this ACK
		        String sqlCommandUpdAck = "UPDATE nem.nem_ebt_events SET ack_sent_yn = 'Y' WHERE ebt_transaction IS NOT NULL " + 
		        "AND (ack_sent_yn <> 'Y' OR ack_sent_yn IS NULL) AND event_type IN ('CTR', 'MTR', 'VST') " + 
		        "AND ID = '" + notification.getAckEbeId() + "' AND eve_id = '" + notification.getAckEbeEveId() + "'";

				jdbcTemplate.update(sqlCommandUpdAck, new Object[] {});
				
		        // Log JAM Message
		        utilityFunctions.logJAMMessage( notification.getAckEbeEveId(),  "I",  "EMC.AlertNotification.sendAckUsapToMSSLviaAQ()",
		        		notification.getJmsType() + " Acknowledgement sent to Oracle AQ and DB Table Updated successfully. ", 
		                                        "");
		    }
		    else if (notification.ackDBUpdate == false) {
		        logger.log(Priority.INFO,"[" + notification.getJmsType() + "] EMC.AlertNotification.sendAckUsapToMSSLviaAQ() : This is USAP Sending, no update required for ACK_SENT_YN.");
		    }

		    logger.log(Priority.INFO,"[EMC] Ending EMC.AlertNotification.sendAckUsapToMSSLviaAQ() ...");
		}catch (Exception e) {
		    logger.log(Priority.WARN,"[EMC] Failed to send ACK/USAP Message to Oracle AQ. " + e.getMessage());
		}

	}	
	
	
	public void sendEmail(AlertNotification notification)
	{
		
		logger.log(Priority.INFO,"[EMC] In AlertNotification.sendEmail() -  notification :"+notification);
		try{
		    String noticeTypeId = null;
		    String msgType = "ExternalEmailNotification";
		    String msgChannel = "Email";
		    String msgSecurity = "NONSSL";
		    notification.setNotfId(utilityFunctions.getEveId());
		    notification.setSender("settlement.app@emcsg.com");
		    String sqlCommand = "SELECT ID FROM nem.nem_settlement_notice_types " + 
		    "where notice_type_description = '" + notification.getNoticeType() + "'";

		    List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
		    for (Map row : list) {
		    	noticeTypeId = (String)row.get("ID");
		        break;
		    }


		    if (noticeTypeId == null) {
		        throw new Exception( "Cannot find the notice type: " + notification.getNoticeType());
		    }

		    Object[] params = new Object[11];
		    sqlCommand = "INSERT INTO nem.nem_settlement_notifications " + 
		                 "(ID, notice_type_id, MESSAGE_TYPE, message_channel, message_security, " + 
		                 "subject, BODY, importance, addr_from, addr_to, addr_cc, status, " + 
		                 "created_date) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,'P',SYSDATE )";
		    params[0] = notification.getNotfId();
		    params[1] = noticeTypeId;
		    params[2] = msgType;
		    params[3] = msgChannel;
		    params[4] = msgSecurity;

		    // params[5] = subject  // ITSM 15386
		    params[5] = utilityFunctions.getHostNEMSDBDetails() + notification.getSubject();

		    // ITSM 15386
		    params[6] = notification.getContent();
		    params[7] = notification.getImportance();
		    params[8] = notification.getSender();
		    params[9] = notification.getRecipients();
		    params[10] = notification.getCc();
			jdbcTemplate.update(sqlCommand, params);

		    String content = notification.getContent().replace( "\\n",  "\n");
		    String xmlMsg = "<?xml version=\"1.0\"?>\n" + 
		    "<OracleAQMessage>\n" + 
		    "<MessageType>SettlementEmailNotification</MessageType>\n" + 
		    "<MessageChannel>Email</MessageChannel>\n" + 
		    "<MessageSecurity>NONSSL</MessageSecurity>\n" + 
		    "<Email>\n" + 
		    "<Subject>" + utilityFunctions.getHostNEMSDBDetails() + notification.getSubject() + "</Subject>\n" + 
		    "<Importance>" + notification.getImportance() + "</Importance>\n" + 
		    "<From>" + notification.getSender() + "</From>\n" + 
		    "<To>" + notification.getRecipients() + "</To>\n" + 
		    "<Cc>" + notification.getCc() + "</Cc>\n" + 
		    "<Body><![CDATA[" + content + "]]></Body>\n" + 
		    "</Email>\n" + 
		    "</OracleAQMessage>";
		    notification.setRequestedQueueName("omsfo.aq_notification_queue");

		    // omsfo.aq_messages_queue - Commented for SRP by EMC
		    logger.log(Priority.INFO,"[EMC] Email: xmlMsg [" + xmlMsg + "]");

		    try (Connection conn = jdbcTemplate.getDataSource().getConnection();
				 CallableStatement cstmt = conn.prepareCall("{call NEM.NEM$AQ_SERVICES.AQ_ENQUEUE_MESSAGE(?,?)}")) {
				cstmt.setString(1, xmlMsg);
				cstmt.setString(2, notification.getRequestedQueueName());
				cstmt.executeUpdate();

		    	/*SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
						.withSchemaName("NEM")
						.withCatalogName("NEM$AQ_SERVICES")
						.withProcedureName("AQ_ENQUEUE_MESSAGE")
						.useInParameterNames("p_payload_char","p_queue_name")
						.declareParameters(new SqlInOutParameter("p_payload_char", OracleTypes.VARCHAR), 
								new SqlInOutParameter("p_queue_name", OracleTypes.VARCHAR));
						SqlParameterSource in = new MapSqlParameterSource()
								.addValue("p_payload_char", xmlMsg)
								.addValue("p_queue_name", notification.getRequestedQueueName());
						jdbcCall.execute(in);*/
		    }
		    catch (Exception e) {
		        // Update nem_settlement_notifications Status to "F" (Fail)
		        sqlCommand = "UPDATE nem.nem_settlement_notifications SET status = 'F', " + 
		                     "modified_date = SYSDATE WHERE id = '" + notification.getNotfId() + "'";
		        
		        jdbcTemplate.update(sqlCommand, new Object[] {});
		        throw e;
		    }

		    // Update nem_settlement_notifications Status to "S" (Success)
		    sqlCommand = "UPDATE nem.nem_settlement_notifications SET status = 'S', " + 
		                 "modified_date = SYSDATE WHERE id = '" + notification.getNotfId() + "'";
		    jdbcTemplate.update(sqlCommand, new Object[] {});
		}
		catch (Exception e) {
		    logger.log(Priority.WARN,"[EMC] Failed to send Email Alert. " + e.getMessage());
		}
		
	}
	
	public void sendSchAlertEmailIncExtParty(String processName, String p_sendEmailAlert2ExternalParty, String eveId, AlertNotification notification)
	{
		// This function is introduced for ITSM 15386 part of changes of alert function calls.
		logger.log(Priority.INFO,"[EMC] In EMC.AlertNotification.sendSchAlertEmailIncExtParty() -  processName :"+processName+" p_sendEmailAlert2ExternalParty :"+p_sendEmailAlert2ExternalParty+"  eveId :"+eveId+" notification :"+notification);

		//  ITSM 15386
		try {
		    String noticeTypeId = null;
		    String msgType = "ExternalEmailNotification";
		    String msgChannel = "Email";
		    String msgSecurity = "NONSSL";
		    notification.setNotfId(utilityFunctions.getEveId());
		    notification.setSender("settlement.app@emcsg.com");
		    String sqlCommand = "SELECT ID FROM nem.nem_settlement_notice_types " + 
		    "where notice_type_description = '" + notification.getNoticeType() + "'";

		    List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
		    for (Map row : list) {
		    	noticeTypeId = (String)row.get("ID");
		        break;
		    }


		    if (noticeTypeId == null) {
		        throw new Exception( "Cannot find the notice type: " + notification.getNoticeType());
		    }

		    String emailRecipients;

		    // ITSM 15386
		    emailRecipients = utilityFunctions.prepareSendAlertEmailRecipient( processName, p_sendEmailAlert2ExternalParty, notification.getRecipients(), eveId);

		    // ITSM 15386
		    Object[] params = new Object[11];
		    sqlCommand = "INSERT INTO nem.nem_settlement_notifications " + 
		                 "(ID, notice_type_id, MESSAGE_TYPE, message_channel, message_security, " + 
		                 "subject, BODY, importance, addr_from, addr_to, addr_cc, status, " + 
		                 "created_date) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,'P',SYSDATE )";
		    params[0] = notification.getNotfId();
		    params[1] = noticeTypeId;
		    params[2] = msgType;
		    params[3] = msgChannel;
		    params[4] = msgSecurity;

		    // params[5] = subject  // ITSM 15386
		    params[5] = utilityFunctions.getHostNEMSDBDetails() + notification.getSubject();

		    // ITSM 15386
		    params[6] = notification.getContent();
		    params[7] = notification.getImportance();
		    params[8] = notification.getSender();

		    // params[9] = recipients	  // ITSM 15386
		    params[9] = emailRecipients;

		    // ITSM 15386
		    params[10] = notification.getCc();
		    jdbcTemplate.update(sqlCommand, params);

		    String content = notification.getContent().replace( "\\n", "\n");
		    String xmlMsg = "<?xml version=\"1.0\"?>\n" + 
		    "<OracleAQMessage>\n" + 
		    "<MessageType>SettlementEmailNotification</MessageType>\n" + 
		    "<MessageChannel>Email</MessageChannel>\n" + 
		    "<MessageSecurity>NONSSL</MessageSecurity>\n" + 
		    "<Email>\n" + 
		    "<Subject>" + utilityFunctions.getHostNEMSDBDetails() + notification.getSubject() + "</Subject>\n" + 
		    "<Importance>" + notification.getImportance() + "</Importance>\n" + 
		    "<From>" + notification.getSender() + "</From>\n" + 
		    "<To>" + emailRecipients + "</To>\n" + 
		    "<Cc>" + notification.getCc() + "</Cc>\n" + 
		    "<Body><![CDATA[" + content + "]]></Body>\n" + 
		    "</Email>\n" + 
		    "</OracleAQMessage>";
		    notification.setRequestedQueueName("omsfo.aq_notification_queue");

		    // "omsfo.aq_messages_queue" - Commented for SRP by EMC
		    logger.log(Priority.INFO,"[EMC] Email: xmlMsg [" + xmlMsg + "]");

		    try(Connection conn = jdbcTemplate.getDataSource().getConnection();
				CallableStatement cstmt = conn.prepareCall("{call NEM.NEM$AQ_SERVICES.AQ_ENQUEUE_MESSAGE(?,?)}")) {

					cstmt.setString(1, xmlMsg);
					cstmt.setString(2, notification.getRequestedQueueName());
					cstmt.executeUpdate();
		    	/*DR1SHARP-83*/
		    	/*SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
						.withSchemaName("NEM")
						.withCatalogName("NEM$AQ_SERVICES")
						.withProcedureName("AQ_ENQUEUE_MESSAGE")
						.useInParameterNames("p_payload_char","p_queue_name")
						.declareParameters(new SqlInOutParameter("p_payload_char", OracleTypes.VARCHAR), 
								new SqlInOutParameter("p_queue_name", OracleTypes.VARCHAR));
						SqlParameterSource in = new MapSqlParameterSource()
								.addValue("p_payload_char", xmlMsg)
								.addValue("p_queue_name", notification.getRequestedQueueName());
						jdbcCall.execute(in);*/		
						
				/*SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
						.withSchemaName("NEM")
						.withCatalogName("NEM$AQ_SERVICES")
						.withProcedureName("AQ_ENQUEUE_MESSAGE");
				Map<String, Object> inMap = new HashMap<String, Object>();
				inMap.put("p_payload_char", xmlMsg);
				inMap.put("p_queue_name", notification.getRequestedQueueName());
				jdbcCall.execute(inMap);*/
				
				
				}
			    catch (Exception e) {
			        // Update nem_settlement_notifications Status to "F" (Fail)
			        sqlCommand = "UPDATE nem.nem_settlement_notifications SET status = 'F', " + 
		                     "modified_date = SYSDATE WHERE id = '" + notification.getNotfId() + "'";
			        
			        jdbcTemplate.update(sqlCommand, new Object[] {});
			        throw e;
			    }

		    // Update nem_settlement_notifications Status to "S" (Success)
		    sqlCommand = "UPDATE nem.nem_settlement_notifications SET status = 'S', " + 
		                 "modified_date = SYSDATE WHERE id = '" + notification.getNotfId() + "'";
		    jdbcTemplate.update(sqlCommand, new Object[] {});
		}catch (Exception e) {
		    logger.log(Priority.WARN,"[EMC] Failed to send Email Alert. " + e.getMessage());
		}

	}
}
