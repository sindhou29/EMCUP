package com.emc.sett.utils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.emc.sett.common.SettlementRunException;
import com.emc.settlement.model.backend.pojo.AlertNotification;

public class DBUtility {
	
	static public final String EMC_DATASOURCE_JNDI = "emc.nems.jndi";
	
	static public DataSource getDatasource(String jndiName) throws NamingException {
		Context ctx = new InitialContext();
		return (javax.sql.DataSource) ctx.lookup (jndiName);
	}
	
	static public DataSource getDatasource() throws NamingException {
		String jndiName = System.getProperty(EMC_DATASOURCE_JNDI);
		return getDatasource(jndiName);
	}

	static public String getHostNemsDBDetails(DataSource ds) throws Exception {
		
		Connection conn = ds.getConnection();;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		int rowCount = 0;
		String environmentName = "";
		String envHostName = "";
		String envBackUpMode = "";
		
		try {
			String sqlCmd = " SELECT output, (SELECT GLOBAL_NAME FROM GLOBAL_NAME), " +
							" rtrim(ltrim(sys.UTL_INADDR.GET_HOST_NAME||' - '||sys.UTL_INADDR.GET_HOST_ADDRESS)), " + 
							" NEM.GET_SPLEX_MODE() backup_mode " + 
							" FROM (SELECT COUNT (*) output FROM nem.nem_db_environment " + 
							" WHERE database_name = (SELECT GLOBAL_NAME " + 
							" FROM GLOBAL_NAME) AND database_type = 'PROD') ";
			
			stmt = conn.prepareStatement(sqlCmd);
			stmt.executeQuery();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
	            rowCount = rs.getInt(1);
	            environmentName = rs.getString(2);
	            envHostName = rs.getString(3);
	            envBackUpMode = rs.getString(4);
	
	            break;
			}
			rs.close();
			stmt.close();
			
	//	 	if non prod will be 0, find non 0 and mark the global value in PoductionEnv = <blank>, else PoductionEnv = <[TEST : EMCP - PRUXDB01 - 10.1.10.81]>
			if (rowCount != 0 && envBackUpMode.equals("N") == true) {
			    return "";		// "Y" for Real PRODUCTION / DR in ACTIVE Mode
			} else if (rowCount != 0 && envBackUpMode.equals("Y") == true) {
			    return "[DR]";	// Non ACTIVE PROD DB (DR)
			} else {
			    return "[TEST : " + environmentName + " : " + envHostName + "]";	// NON PROD including Parallel / DRY Run Environments
			}
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				// No actions required
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				// No actions required
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				// No actions required
			}
		}
	}

	static public String getGUID(DataSource ds) throws Exception {
		
		Connection conn = ds.getConnection();;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String guid = "";
		
		try {
			String sqlCmd = " select NEM.GET_GUID from dual ";
			
			stmt = conn.prepareStatement(sqlCmd);
			stmt.executeQuery();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
				guid = rs.getString(1);
	            break;
			}
			rs.close();
			stmt.close();
			
			return guid;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				// No actions required
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				// No actions required
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				// No actions required
			}
		}
	}
	
	static public void sendEmail(DataSource ds, AlertNotification notification) {
		
		System.out.println("[EMC] In DBUtility.sendEmail() - notification :" + notification);

		ResultSet rs = null;
		PreparedStatement pstmt = null;
		CallableStatement cstmt = null;
		Connection conn = null;
		
		try {
			conn = ds.getConnection();
			
			notification.notfId = DBUtility.getGUID(ds);
			notification.hostNemsDBDetails =  DBUtility.getHostNemsDBDetails(ds);
			notification.msgType = "ExternalEmailNotification";
			notification.msgChannel = "Email";
			notification.msgSecurity = "NONSSL";
			notification.sender = "settlement.app@emcsg.com";
			notification.cc = "";
			notification.requestedQueueName = "omsfo.aq_notification_queue";
			
		    String noticeTypeId = null;
		    String sqlCommand = "SELECT ID FROM nem.nem_settlement_notice_types " + 
		    "where notice_type_description = '" + notification.getNoticeType() + "'";

		    pstmt = conn.prepareStatement(sqlCommand);
		    rs = pstmt.executeQuery();
		    while(rs.next()) {
		    	noticeTypeId = rs.getString(1);
		        break;
		    }
		    pstmt.close();
			rs.close();		

		    if (noticeTypeId == null) {
		    	throw new SettlementRunException("Cannot find the notice type: " + notification.getNoticeType());
		    } else {
			    sqlCommand = "INSERT INTO nem.nem_settlement_notifications " + 
			                 "(ID, notice_type_id, MESSAGE_TYPE, message_channel, message_security, " + 
			                 "subject, BODY, importance, addr_from, addr_to, addr_cc, status, " + 
			                 "created_date) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,'P',SYSDATE )";
			    
				pstmt = conn.prepareStatement(sqlCommand);
				pstmt.setString(1, notification.getNotfId());
				pstmt.setString(2, noticeTypeId);
				pstmt.setString(3, notification.getMsgType());
				pstmt.setString(4, notification.getMsgChannel());
				pstmt.setString(5, notification.getMsgSecurity());
				
			    // params[5] = subject  // ITSM 15386
				pstmt.setString(6, notification.getHostNemsDBDetails() + notification.getSubject());
				
			    // ITSM 15386
				pstmt.setString(7, notification.getContent());
				pstmt.setString(8, notification.getImportance());
				pstmt.setString(9, notification.getSender());
				pstmt.setString(10, notification.getRecipients());
				pstmt.setString(11, notification.getCc());
				pstmt.executeUpdate();
			    pstmt.close();

			    String content = notification.getContent().replace( "\\n",  "\n");
			    String xmlMsg = "<?xml version=\"1.0\"?>\n" + 
			    "<OracleAQMessage>\n" + 
			    "<MessageType>SettlementEmailNotification</MessageType>\n" + 
			    "<MessageChannel>Email</MessageChannel>\n" + 
			    "<MessageSecurity>NONSSL</MessageSecurity>\n" + 
			    "<Email>\n" + 
			    "<Subject>" + notification.getHostNemsDBDetails() + notification.getSubject() + "</Subject>\n" + 
			    "<Importance>" + notification.getImportance() + "</Importance>\n" + 
			    "<From>" + notification.getSender() + "</From>\n" + 
			    "<To>" + notification.getRecipients() + "</To>\n" + 
			    "<Cc>" + notification.getCc() + "</Cc>\n" + 
			    "<Body><![CDATA[" + content + "]]></Body>\n" + 
			    "</Email>\n" + 
			    "</OracleAQMessage>";

			    // omsfo.aq_messages_queue - Commented for SRP by EMC
			    System.out.println("[EMC] Email: xmlMsg [" + xmlMsg + "]");
	
			    try {
					cstmt = conn.prepareCall("{CALL NEM.NEM$AQ_SERVICES.AQ_ENQUEUE_MESSAGE(?, ?) }");
					cstmt.setString(1, xmlMsg);
					cstmt.setString(2, notification.getRequestedQueueName());
					cstmt.executeUpdate();
					cstmt.close();
	
				    // Update nem_settlement_notifications Status to "S" (Success)
				    sqlCommand = "UPDATE nem.nem_settlement_notifications SET status = 'S', " + 
				                 "modified_date = SYSDATE WHERE id = '" + notification.getNotfId() + "'";
			        pstmt = conn.prepareStatement(sqlCommand);
			        pstmt.executeUpdate();
			        pstmt.close();
			    } catch (Exception e) {
			        // Update nem_settlement_notifications Status to "F" (Fail)
			        sqlCommand = "UPDATE nem.nem_settlement_notifications SET status = 'F', " + 
			                     "modified_date = SYSDATE WHERE id = '" + notification.getNotfId() + "'";
			        
			        pstmt = conn.prepareStatement(sqlCommand);
			        pstmt.executeUpdate();
			        pstmt.close();
			        throw e;
			    }
		    }
		} catch (Exception e) {
			System.out.println("[EMC] Failed to send Email Alert. " + e.toString());
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				// nothing needed
			}
			try {
				pstmt.close();
			} catch (Exception e) {
				// nothing needed
			}
			try {
				cstmt.close();
			} catch (Exception e) {
				// nothing needed
			}
			try {
				conn.close();
			} catch (Exception e) {
				// nothing needed
			}
		}
		
	}
}
