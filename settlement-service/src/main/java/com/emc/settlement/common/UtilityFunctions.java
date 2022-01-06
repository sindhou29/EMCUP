package com.emc.settlement.common;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.emc.settlement.model.backend.constants.BusinessParameters;
import com.emc.settlement.model.backend.exceptions.SettRunInfoException;
import com.emc.settlement.model.backend.pojo.AlertNotification;
import com.emc.settlement.model.backend.pojo.DateRange;
import com.emc.settlement.model.backend.pojo.SettlementRunInfo;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import com.emc.settlement.model.backend.pojo.UploadFileInfo;

@Service
public class UtilityFunctions {

	private static final Logger logger = Logger.getLogger(UtilityFunctions.class);

	public static final String CLASS_NAME = "UtilityFunctions";
	
	public static final String BUSINESS_PARAMETERS_PRPPERTY_FILE = "/app/properties/settlement-bpm.properties";


	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	AlertNotificationImpl alertImpl;

	public String fileToString(String fileContentName) {
		String fileString = "";
		if (new File(fileContentName).exists()) {
			logger.log(Priority.INFO, "Inside " + CLASS_NAME + ".fileToString: Filename " + fileContentName + " Exists");
		}
		else {
			logger.log(Priority.WARN, "Inside " + CLASS_NAME + ". fileToString: Filename " + fileContentName + " does not Exist");
		}
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(fileContentName));
			return new String(encoded, StandardCharsets.UTF_8);
		}
		catch (IOException e) {
			logger.error("Exception "+e.getMessage());
		}
		return fileString;
	}

	public void writeToFile(String content, String filename) {

		try(BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {

			bw.write(content);
		} catch (IOException e) {
			logger.error("Exception "+e.getMessage());
		}
	}

	public String getUserId(String name) {

		logger.info("[EMC] Starting Method EMC." + CLASS_NAME + ".getUserId() ...");

		String sqlCommand = "SELECT Id FROM SEFO.SEF_USERS WHERE UPPER(user_name) = UPPER('" + name + "')";

		// resultset should have only just 1 row or zero rows
		String id = null;

		try {
			id = jdbcTemplate.queryForObject(sqlCommand, new Object[] {}, String.class);
		}
		catch (DataAccessException e) {
			logger.log(Priority.ERROR, "[EMC] getUserId() - Query from SEFO.SEF_USERS failed for " + name + ": " + e.getMessage());
		}

		logger.info("[EMC] User ID: " + id + " for User: " + name);

		if (id == null) {
			logger.info("ID not found in SEF_USERS for: " + name);
		}

		return id;
	}

	public void getMSSLMetaData(String xmlStr, UploadFileInfo fileInfo) throws Exception {
		try {
			logger.log(Priority.INFO, "[EMC] Starting Method " + CLASS_NAME + ".getMSSLMetaData() ...");

			// parse the XML and get the Root node
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			Document xmlDocument = null;
			try {
				builder = factory.newDocumentBuilder();
				xmlDocument = builder.parse(new InputSource(new StringReader(xmlStr)));
			}
			catch (Exception e) {
				logger.error("Exception "+e.getMessage());
			}
			// Get data of all node, using XPath
			XPath xPath = XPathFactory.newInstance().newXPath();
			NodeList nodes = (NodeList) xPath.evaluate("/DispatchData/*",
					xmlDocument.getDocumentElement(), XPathConstants.NODESET);

			String content = null;
			for (int i = 0; i < nodes.getLength(); ++i) {
				Element element = (Element) nodes.item(i);
				if (element.getTagName().equals("TransactionId")) {
					fileInfo.transId = element.getTextContent();
				}
				if (element.getTagName().equals("ContentFormat")) {
					fileInfo.contentFormat = element.getTextContent();
				}
				if (element.getTagName().equals("Compressed")) {
					fileInfo.compressed = element.getTextContent();
				}
				if (element.getTagName().equals("Data")) {
					content = element.getTextContent();
				}
			}

			if (fileInfo.transId == null || fileInfo.contentFormat == null || fileInfo.compressed == null || content == null) {
				throw new Exception("Invalid XML file.");
			}
		}
		catch (Exception e) {
			logger.log(Priority.INFO, "[EMC] getMSSLMetaData() - Parse XML fail: " + e.getMessage());
			throw new Exception("Invalid XML file.");
		}

	}


	public String getMSSLContent(String xmlStr) {
		String content = "";
		try {
			logger.log(Priority.INFO, "[EMC] Starting Method " + CLASS_NAME + ".getMSSLContent() ...");

			// parse the XML and get the Root node

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			Document xmlDocument = null;

			try {
				builder = factory.newDocumentBuilder();
				xmlDocument = builder.parse(new InputSource(new StringReader(xmlStr)));
			}
			catch (Exception e) {
				logger.error("Exception "+e.getMessage());
			}

			XPath xPath = XPathFactory.newInstance().newXPath();
			NodeList nodes = (NodeList) xPath.evaluate("/DispatchData/*",
					xmlDocument.getDocumentElement(), XPathConstants.NODESET);

			// Get data of all node, using XPath
			for (int i = 0; i < nodes.getLength(); ++i) {
				Element e = (Element) nodes.item(i);
				if (e.getTagName().equals("Data")) {
					content = e.getTextContent();
				}
			}

			if (content == null) {
				throw new Exception("Invalid XML file.");
			}
		}
		catch (Exception e) {
			logger.log(Priority.INFO, "[EMC] getMSSLContent() - Parse XML fail: " + e.getMessage());

			try {
				throw new Exception("Invalid XML file.");
			}
			catch (Exception e1) {
				logger.error("Exception "+e1.getMessage());
			}
		}

		return content;
	}

	public String base64Decode(String inStr) throws IOException {

		logger.log(Priority.INFO, "[EMC] Starting Method " + CLASS_NAME + ".base64Decode() String...");
		String msgStep = CLASS_NAME + "." + "base64Decode().";

		byte[] stringArray = new byte[1024];
		String decodedString = null;

		try {
			byte[] zipArray = org.apache.commons.codec.binary.Base64.decodeBase64(inStr.getBytes());

			InputStream byteArrayInputStream = new ByteArrayInputStream(zipArray);
			ZipInputStream zipIn = new ZipInputStream(byteArrayInputStream);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ZipEntry entry = zipIn.getNextEntry();
			int len = 1;
			while (len > 0) {
				len = zipIn.read(stringArray);

				if (len > 0) {
					byteArrayOutputStream.write(stringArray, 0, len);
				}
			}
			decodedString = byteArrayOutputStream.toString();
		}
		catch (IOException e) {
			e.printStackTrace();
			logger.error("base64Decode String IOException : "+e.getMessage());
			//throw new MsslException(FILE_VALIDATION, 0, 0, e.getMessage(),msgStep);
			throw e;
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("base64Decode String Exception : "+e.getMessage());
			//throw new MsslException(FILE_VALIDATION, 0, 0, e.getMessage(),msgStep);
			throw e;
		}
		return decodedString;
	}

	public String base64Encode(byte[] bin) {
		logger.log(Priority.INFO, "[EMC] Starting Method " + CLASS_NAME + ".base64Encode() byte[] ...");

		String decodedString = null;
		try {
			Encoder encoder = Base64.getEncoder();
			decodedString = encoder.encodeToString(bin);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("base64Decode byte[] Exception : "+e.getMessage());
			//throw new MsslException(FILE_VALIDATION, 0, 0, e.getMessage(),msgStep);
			throw e;
		}
		return decodedString;
	}

	public Date getSysParamTime(String paramName) {
		Date tm = null;
		try {
			String sqlCommand = "SELECT date_value FROM NEM.aps_system_parameters" +
					" WHERE upper(name) = upper(?) ";
			tm = jdbcTemplate.queryForObject(sqlCommand, new Object[] {paramName}, Date.class);

			if (tm == null) {
				throw new Exception("Can not find the " + paramName + " in the system (APS_SYSTEM_PARAMETERS)");
			}

		}
		catch (SQLException sqle) {
			logger.error("Exception "+sqle.getMessage());
		}
		catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}
		return tm;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateSEWApproveProcessStatus(String ebeId, String processingYN) throws SQLException {

		try {
			// This method will update whether a processing going on for SEW File Approval
			logger.log(Priority.INFO, "[EMC] " + CLASS_NAME + ".updateSEWApproveProcessStatus() ...");

			String sqlCommand = "update nem_ebt_events set processing_yn = ?, start_processing_time = ? " +
					"where id = ? ";
			Object[] paramsProcessingStatus = new Object[3];
			paramsProcessingStatus[0] = processingYN;
			paramsProcessingStatus[1] = new java.sql.Date(Calendar.getInstance().getTimeInMillis());
			paramsProcessingStatus[2] = ebeId;
			jdbcTemplate.update(sqlCommand, paramsProcessingStatus);
		}
		catch (Exception e) {
			logger.log(Priority.INFO, "[EMC] " + CLASS_NAME + ".updateSEWApproveProcessStatus() - Updating SEW Approval Ongoing Processing Status failed for EBE ID :" + ebeId +
					", Time :" + new Date() + ", Details :" + e.getMessage());

			throw e;
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateSEWApprovalOutcome(String ebeId, boolean success) throws SQLException {
		try {
			// This method will update the outcome of SEW File Approve activity
			logger.log(Priority.INFO, "[EMC] " + CLASS_NAME + ".updateSEWApprovalOutcome() ...");

			String sqlCommand = "UPDATE NEM.NEM_EBT_EVENTS " +
					" SET APPROVAL_STATUS = '" + (success ? "A" : "R") + "', " +
					"     APPROVAL_TIMESTAMP = SYSDATE " +
					" WHERE ID = ? ";
			String[] paramsApproveStatus = new String[1];
			paramsApproveStatus[0] = ebeId;
			jdbcTemplate.update(sqlCommand, paramsApproveStatus);
		}

		catch (Exception e) {
			logger.log(Priority.INFO, "[EMC] " + CLASS_NAME + ".updateSEWApprovalOutcome() - Updating SEW File Approval Outcome failed for EBE ID :" + ebeId +
					", Time :" + new Date() + ", Details :" + e.getMessage());

			throw e;
		}
	}

	public boolean isBeforeFSCEffectiveEndDate(Date settDate) {
		Date fscEffectiveEndDate = this.getSysParamTime("FSC_EFF_END_DATE");

		if (settDate.compareTo(fscEffectiveEndDate) <= 0) {
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isAfterFSCEffectiveStartDate(Date settDate) {
		// Get Parameter: FSC_EFF_START_DATE
		Date fscEffectiveStartDate = this.getSysParamTime("FSC_EFF_START_DATE");

		if (settDate.compareTo(fscEffectiveStartDate) >= 0) {
			return true;
		}
		else {
			return false;
		}
	}

	public String getSysParamVarChar(String paramName) throws Exception {
		String retStr = null;
		try {
			/*
			 * return the character value corresponding to paramName in
			 * aps_system_parameters table
			 *
			 * @param paramName
			 */
			String sqlCommand = "SELECT character_value FROM NEM.aps_system_parameters" + " WHERE UPPER(NAME) = UPPER('"
					+ paramName + "')";

			retStr = queryForObject(sqlCommand, String.class);
			if (retStr == null) {
				logger.log(Priority.INFO,
						"[EMC] " + CLASS_NAME + ".getSysParamVerChar() -- value not found, paramName=" + paramName);
				throw new Exception("Can not find " + paramName + " in aps_system_parameters table.");
			}
		}
		catch (SQLException sqle) {
			logger.error("Exception "+sqle.getMessage());
			throw sqle;
		}
		return retStr;
	}

	public String isFSCAllowedForSacId(String sacId, String standingVersion) {
		String fscAllowed = null;
		// 8.0.01 Changes FSC By All
		// UtilityFunctions.isFSCAllowedForSacId
		// Added to get FSC Submission Allow Status based on Individual Settlement
		// Account Details
		String sqlIsFSCAllowed = "SELECT CASE WHEN nem.nem$util.get_sp_vc('FSC_ALLOW_ALL_MPS') = 'N'  THEN CASE WHEN sac.sac_type NOT IN ('G', 'E', 'R') THEN 'N' ELSE 'Y' END "
				+ "ELSE  'Y'  END FSC_Allowed_YN FROM nem.nem_settlement_accounts sac WHERE sac.ID = ? and sac.version = ? ";

		Object[] params = new Object[2];
		params[0] = sacId;
		params[1] = standingVersion;
		fscAllowed = jdbcTemplate.queryForObject(sqlIsFSCAllowed, params, String.class);
		return fscAllowed;
	}


	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String createJAMEvent(String eveType, String source) {
		String eveId = null;
		eveId = this.getEveId();
		if (source.length() > 30)
			source = source.substring(0, 30);

		String sqlCommand = "INSERT INTO JAM_EVENTS (id, eve_type, start_date, source) " + " VALUES ( '" + eveId
				+ "', '" + eveType + "', SYSDATE, '" + source + "' )";
		jdbcTemplate.update(sqlCommand);

		logger.info("[EMC] Created JAM Event with Id: " + eveId);

		return eveId;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String createJAMEvent(String eveType, String completed, String esd_id) {
		String eveId = null;
		eveId = this.getEveId();
		
		String sqlCommand = "INSERT INTO NEM.JAM_EVENTS (id, eve_type, start_date, completed, esd_id) " + 
                "VALUES ('" + eveId + "','SDE',SYSDATE,'N','" + esd_id + "')";
	    
		jdbcTemplate.update(sqlCommand);

		logger.info("[EMC] Created JAM Event with Id: " + eveId);

		return eveId;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String createEventSchedules(String runFrom, Date runDate, Date settlementDate, String settlementType, String username, String srEvtId)
	{
		String sqlCommand = "INSERT INTO NEM.JAM_EVENT_SCHEDULES (ID, IMMEDIATELY, FREQUENCY, FREQUENCY_VALUE, " + 
	                "STREAM, TRACE_EXECUTION, ACTIVE, START_DATE, START_HOUR, START_MINUTE, PARAMETERS, COMMENTS, " + 
	                "EVT_ID) VALUES (?,'Y',?,1,'A','N','Y',?,?,?,?,?,?) ";
	
	   // sqlCommand = "INSERT INTO NEM.JAM_EVENT_SCHEDULES VALUES ( '" + schEventId + "'," + "'Y', '"
	   // 	+ frequency + "', '1', 'A','N','N','','','','','','','" + srEvtId + "','')"
	
		String eveId = null;
		eveId = this.getEveId();
		
		String frequency = (runFrom.equalsIgnoreCase("B") ? "D" : "O");
		
		Object[] params = new Object[8];
		params[0] =  eveId;
		params[1] =  frequency;
		params[2] =  this.convertUDateToSDate(runDate);
		params[3] =  runDate.getHours();
		params[4] =  runDate.getMinutes();
		params[5] =  settlementDate == null ? "'','" : "'" + this.getddMMMyyyy(settlementDate) + "','" + 
			    settlementType + "','" + runFrom + "','" + username + "'";
		params[6] =  "Settlement Run";
		params[7] =  srEvtId;
		jdbcTemplate.update(sqlCommand, params);

		return eveId;
	}

	public String getEveId() {
		String eveId = null;
		try {
			logger.debug("[EMC] Before getEveId: " + eveId);
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withSchemaName("NEM")
					.withFunctionName("GET_GUID");
			Map<String, Object> map = jdbcCall.execute();
			eveId = (String) map.get("return");

			logger.info("[EMC] getEveId: " + eveId);
		}
		catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}

		return eveId;
	}

	public String getShareplexMode() {
		String splex_mode = null;
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withSchemaName("NEM")
					.withFunctionName("GET_SPLEX_MODE");
			Map<String, Object> resultMap = jdbcCall.execute();
			splex_mode = (String) resultMap.get("return");

			logger.log(Priority.INFO, "[EMC] splex_mode: " + splex_mode);
		}
		catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}
		return splex_mode;
	}
	
	public Map<String, Object> checkScheduleAndShareplex(Map<String, Object> variableMap)
	{
		String splexMode = (String) variableMap.get("splexMode");
		String scheduleFlag = (String) variableMap.get("scheduleFlag");
		String msgStep="UtilityFunctions.checkScheduleAndShareplex()";
		logger.log(Priority.INFO, "Starting Activity " + msgStep);
		Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		try {
			splexMode = this.getShareplexMode();
			scheduleFlag = propertiesMap.get("ALLOW_SCHEDULED_TASK");
			variableMap.put("splexMode", splexMode);
			variableMap.put("scheduleFlag", scheduleFlag);
		}
		catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}
		logger.log(Priority.INFO, "Returning from service "+msgStep+"... splexMode : " + splexMode+"  scheduleFlag : "+scheduleFlag);
		return variableMap;
	}

	public String computeDueDate(String dateIn, String dateFormat, int daysDue) {
		// computes new date and returns it as string in same format as input
		Date currDate;
		String dateOut = null;
		SimpleDateFormat fmt = new SimpleDateFormat(dateFormat);
		try {
			currDate = fmt.parse(dateIn);
			Calendar cal = Calendar.getInstance();
			cal.setTime(currDate);
			cal.add(Calendar.DATE, daysDue);
			dateOut = fmt.format(cal.getTime());
		}
		catch (ParseException e) {
			logger.error("Exception "+e.getMessage());
		}

		return dateOut;
	}


	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String createEbtEvent(String eveId, String ebtEveType, String filename, String userId, String comment,
			String transId) {
		logger.info("[EMC] " + CLASS_NAME + ".createEbtEvent() ...");

		if(transId != null) {
			if("null".equalsIgnoreCase(transId)) 
				transId = "";
		}else {
			if(transId == null) 
				transId = "";
		}
		String ebtEveId = this.getEveId();
		String comment1 = comment.replace("'", "''");
		String sqlCommand = "INSERT into nem_ebt_events "
				+ "( id, eve_id, filename, ebt_transaction, event_type, uploaded_date, uploaded_by, comments ) "
				+ " VALUES ( '" + ebtEveId + "', '" + eveId + "', '" + filename + "', '" + transId + "', '" + ebtEveType
				+ "', SYSDATE, '" + userId + "', '" + comment1 + "' )";

		jdbcTemplate.update(sqlCommand);

		return ebtEveId;

	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void logJAMMessage(String eventId, String severity, String execStep, String text, String errorCode) {
		try {

			logger.info("[JAM] Exec Step: " + execStep + ", Message Type: " + severity + ", Text: " + text
					+ ", Event Id: " + eventId);

			String id = null;

			id = this.getEveId();

			String sqlCommand = "INSERT INTO NEM.JAM_MESSAGES ( ID, ERROR_CODE, SEQ, SEVERITY, "
					+ "TEXT, MESSAGE_DATE, EXECUTION_STEP, EVE_ID ) "
					+ "VALUES ( ?,?,get_mstimestamp,?,?,SYSDATE,?,? )";
			Object[] params = new Object[6];
			params[0] = id;
			params[1] = errorCode;
			params[2] = severity;
			params[3] = text;
			params[4] = execStep;
			params[5] = eventId;
			jdbcTemplate.update(sqlCommand, params);

		}
		catch (Exception e) {
			logger.info("[JAM] Log JAM Messages failed. Message Type: " + severity + ", Time: " + new Date()
					+ ", Details: " + e.getMessage());
		}

	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateSEWProcessStatusAutoAuth(String msgstr, String sewUploadEventsId) {
		try {
			// This method will update SEW Table with final SEW File Upload and Approval
			// activities as well errors and alerts (when TD+5 >5pm)
			if (!msgstr.equals("") || msgstr != null) {
				String sqlCommand = "UPDATE sewfo.sew_file_uploads " + "   SET message = ? "
						+ " WHERE SEW_UPLOAD_EVENTS_ID =? ";
				Object[] paramsSEWFileStatus = new Object[2];
				paramsSEWFileStatus[0] = msgstr;
				paramsSEWFileStatus[1] = sewUploadEventsId;
				int rowUpdated = jdbcTemplate.update(sqlCommand, paramsSEWFileStatus);
			}
		}
		catch (Exception e) {
			logger.info(
					"[EMC] " + CLASS_NAME + ".updateSEWProcessStatusAutoAuth() - Error while Updating SEW File Process Status for "
							+ ", SEW Upload Events ID :" + sewUploadEventsId + ", Time :" + new Date() + ", Details :"
							+ e.getMessage());

			throw e;
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateSEWFileUploadStatus(String ebeId, boolean success) {
		try {
			// This method will update the outcome of SEW File Upload activity (Not
			// Approval)
			logger.info("[EMC] " + CLASS_NAME + ".updateSEWFileUploadStatus() ...");

			String sqlCommand = "UPDATE NEM.NEM_EBT_EVENTS " + " SET UPLOAD_STATUS = '" + (success ? "A" : "R") + "'"
					+ " WHERE ID = ? ";
			Object[] paramsUploadStatus = new Object[1];
			paramsUploadStatus[0] = ebeId;
			int rowUpdated = jdbcTemplate.update(sqlCommand, paramsUploadStatus);
		}
		catch (Exception e) {
			logger.info(
					"[EMC] " + CLASS_NAME + ".updateSEWFileUploadStatus() - Updating SEW File Upload Status failed for EBE ID :"
							+ ebeId + ", Time :" + new Date() + ", Details :" + e.getMessage());
			throw e;
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateEBTEvent(String ebtEventId, boolean success) {
		logger.info("[EMC] " + CLASS_NAME + ".updateEBTEvent() ...");
		// Update NEM_EBT_EVENTS table
		String sqlCommand = "UPDATE NEM.NEM_EBT_EVENTS " + " SET VALID_YN = '" + (success ? "Y" : "N")
				+ "', VALIDATED_DATE = SYSDATE, " + " PROCESSING_YN = 'N' WHERE ID = '" + ebtEventId + "' ";
		try {
			jdbcTemplate.update(sqlCommand);
		}
		catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateEBTEventValid(String ebtEventId, boolean success) {
		logger.info("[EMC] " + CLASS_NAME + ".updateEBTEventValid() ...");
		// Update NEM_EBT_EVENTS table
		String sqlCommand = "UPDATE NEM.NEM_EBT_EVENTS " + " SET VALID_YN = '" + (success ? "Y" : "N")
				+ "', VALIDATED_DATE = SYSDATE " + " WHERE ID = '" + ebtEventId + "' ";
		try {
			jdbcTemplate.update(sqlCommand);
		} catch (Exception e) {
			logger.error("Exception " + e.getMessage());
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateSettlementDateEBTEvent(Date settlementDate, String ebtEventId) {
		logger.info("[EMC] " + CLASS_NAME + ".updateSettlementDateEBTEvent() ...");

		String sqlUpdateEBTEvent = " UPDATE NEM.NEM_EBT_EVENTS " + " SET SETTLEMENT_DATE = ? " + " WHERE ID = ? ";
		try {
			Object[] params = new Object[2];
			params[0] = convertUDateToSDate(settlementDate);
			params[1] = ebtEventId;
			jdbcTemplate.update(sqlUpdateEBTEvent, params);
		} catch (Exception e) {
			logger.error("Exception " + e.getMessage());
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateEBTEventComments(String ebtEventId, String comments) {
		logger.info("[EMC] " + CLASS_NAME + ".updateEBTEventComments() ...");

		String sqlCommand = " UPDATE NEM.NEM_EBT_EVENTS SET COMMENTS = '" + comments + "' WHERE ID = '" + ebtEventId + "' ";
		try {
			jdbcTemplate.update(sqlCommand);
		} catch (Exception e) {
			logger.error("Exception " + e.getMessage());
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateSEWEventId(String sewUploadEventsID, String ebtEventId) {
		logger.info("[EMC] " + CLASS_NAME + ".updateSEWEventId() ...");

		String sqlUpdateEBTEventSEW = " UPDATE NEM.NEM_EBT_EVENTS " + " SET SEW_UPLOAD_EVENTS_ID = '"
				+ sewUploadEventsID + "' WHERE ID = '" + ebtEventId + "' ";
		try {
			jdbcTemplate.update(sqlUpdateEBTEventSEW);
		} catch (Exception e) {
			logger.error("Exception " + e.getMessage());
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateEBTEventPkgVersion(Date settlementDate, String currVersion, String ebtEventId) {
		logger.info("[EMC] " + CLASS_NAME + ".updateEBTEventPkgVersion() ...");

		String settDate = "TO_DATE('" + new SimpleDateFormat("dd-MMM-yyyy").format(settlementDate) + "', 'dd-MON-yyyy')";
		String sqlCommand = "UPDATE NEM.NEM_EBT_EVENTS " + " SET PKG_VERSION = '" + currVersion + "', SETTLEMENT_DATE = " + settDate + " WHERE ID = '" + ebtEventId + "' ";
		try {
			jdbcTemplate.update(sqlCommand);
		} catch (Exception e) {
			logger.error("Exception " + e.getMessage());
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateJAMEvent(boolean success, String eventId) {
		logger.info("[EMC] " + CLASS_NAME + ".updateJAMEvent() ..."); // Update JAM_EVENTS table
		String sqlCommand = "UPDATE NEM.JAM_EVENTS " + " SET COMPLETED = 'Y', SUCCESS = '" + (success ? "Y" : "N")
				+ "', " + " END_DATE = SYSDATE WHERE ID = '" + eventId + "' ";
		try {
			jdbcTemplate.update(sqlCommand);
		}
		catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateJAMEventCompleted(String eventId) {
		logger.info("[EMC] " + CLASS_NAME + ".updateJAMEventCompleted() ..."); // Update JAM_EVENTS table
		String sqlCommand = "UPDATE JAM_EVENTS SET COMPLETED = 'Y', SUCCESS = 'N' WHERE id = '" + eventId + "' ";
		try {
			jdbcTemplate.update(sqlCommand);
		} catch (Exception e) {
			logger.error("Exception " + e.getMessage());
		}
	}

	public String getHostNEMSDBDetails() {
		Integer rowCount = 0;
		String envBackUpMode = "";
		String environmentName = "";
		String envHostName = "";
		try {
			// 15386 - This function returns "" if the system environment is Production otherwise it will return "N"

			String sqlCmd = " SELECT output, (SELECT GLOBAL_NAME FROM GLOBAL_NAME) GLOBAL_NAME, rtrim(ltrim(sys.UTL_INADDR.GET_HOST_NAME||' - '||sys.UTL_INADDR.GET_HOST_ADDRESS)) GET_HOST_NAME " +
					" , NEM.GET_SPLEX_MODE() backup_mode " +
					" FROM (SELECT COUNT (*) output FROM nem.nem_db_environment " +
					" WHERE database_name = (SELECT GLOBAL_NAME " +
					" FROM GLOBAL_NAME) AND database_type = 'PROD') ";

			Object[] result = queryforList(sqlCmd,"OUTPUT", "GLOBAL_NAME", "GET_HOST_NAME", "BACKUP_MODE");
			BigDecimal output = (BigDecimal) result[0];
			rowCount = Integer.valueOf(output.intValue());
			environmentName = (String) result[1];
			envHostName = (String) result[2];
			envBackUpMode = (String) result[3];
		}
		catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}

//		 	if non prod will be 0, find non 0 and mark the global value in PoductionEnv = <blank>, else PoductionEnv = <[TEST : EMCP - PRUXDB01 - 10.1.10.81]>
		if (rowCount != 0 && (envBackUpMode.equals("N"))) {
			return "";

			// "Y" for Real PRODUCTION / DR in ACTIVE Mode
		}
		else if (rowCount != 0 && (envBackUpMode.equals("Y"))) {
			return "[DR]";

			// Non ACTIVE PROD DB (DR)
		}
		else {
			return "[TEST : " + environmentName + " : " + envHostName + "]";

			// NON PROD including Parallel / DRY Run Environments
		}

	}

	public String prepareSendAlertEmailRecipient(String processName, String p_sendEmailAlert2ExternalParty, String pSettlementRunEmail, String eveId) {
		// 15386 - This function verify whether the process name is allowed to send Email Alert provided it is mentioned in the database layer.
		logger.log(Priority.INFO, "[EMC] In EMC.UtilityFunctions.prepareSendAlertEmailRecipient() ... ");

		if (this.getSendEmail2ExternalPartyFlag(p_sendEmailAlert2ExternalParty).equalsIgnoreCase("Y")) {
			String emailAddForAlert2ExternalParty = "";
			String activeYN = "";
			String sqlCmd = " SELECT (CASE " +
					"            WHEN env.environment_type <> 'PRODUCTION' " +
					"               THEN 'settlement.project2008@emcsg.com' " +
					"            ELSE email_addr " +
					"         END " +
					"        ) email_address, active_yn " +
					" FROM nem.nem_sett_module_notif_mail, " +
					"        (SELECT (CASE " +
					"                    WHEN output <> 0 AND nem.get_splex_mode () = 'N' " +
					"                       THEN 'PRODUCTION' " +
					"                    ELSE 'UAT' || '(' || (SELECT GLOBAL_NAME " +
					"                                            FROM GLOBAL_NAME) || ')' " +
					"                 END " +
					"                ) environment_type " +
					"           FROM (SELECT COUNT (*) output " +
					"                   FROM nem.nem_db_environment " +
					"                  WHERE database_name = (SELECT GLOBAL_NAME " +
					"                                           FROM GLOBAL_NAME) " +
					"                    AND database_type = 'PROD')) env " +
					" WHERE module_name = decode (?,'ClawbackFileReceivingVerification','SCHEDULED - CLAWBACK FILE RECEIVING VERIFICATION','MSSLFileReceivingVerification','SCHEDULED - MSSL FILE RECEIVING VERIFICATION','ScheduledVestingContractFileReceivingVerification','SCHEDULED - VESTING CONTRACT FILE RECEIVING VERIFICATION') ";

			try {
				List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCmd, processName);

				for(Map map : list) {
					emailAddForAlert2ExternalParty = (String) map.get("EMAIL_ADDRESS");
					activeYN = (String) map.get("ACTIVE_YN");
				}
			}
			catch (Exception e) {
				logger.error("Exception "+e.getMessage());
			}

			if (emailAddForAlert2ExternalParty == null || emailAddForAlert2ExternalParty.equals("")) {
				AlertNotification alert = new AlertNotification();
				alert.businessModule = "Settlement Module details for Sending Email to External Party.";
				alert.recipients = pSettlementRunEmail;
				alert.subject = "Settlement Module details (" + processName + ") for Sending Email to External Party is not specified. ";
				alert.content = "BPM Parameter for Sending Email to External Party set as Y, but Settlement Module details (" + processName + ") for Sending Email to External Party is NOT specified. Kindly contact Support." + "\n Alert timing " + this.geHHmm(new Date());
				alert.noticeType = "Settlement Module details verification for Sending Email to External Party";
				alertImpl.sendEmail(alert);

				// Log JAM Message
				this.logJAMMessage(eveId, "W", "EMC." + CLASS_NAME + ".prepareSendAlertEmailRecipient", "BPM Parameter for Sending Email to External Party set as Y, but Settlement Module details (" + processName + ") is NOT specified.", "");

				return pSettlementRunEmail;
			}
			else if (activeYN.equals("N")) {
				return pSettlementRunEmail;
			}
			else {
				if (pSettlementRunEmail.trim().equals("")) {
					return emailAddForAlert2ExternalParty;
				}
				else {
					return pSettlementRunEmail.trim() + " ," + emailAddForAlert2ExternalParty;
				}
			}
		}
		else {
			return pSettlementRunEmail;
		}

	}

	public String getSendEmail2ExternalPartyFlag(String p_sendEmailAlert2ExternalParty) {
		// 15386 - This function modifies BPM Param SEND_EMAIL_ALERT_TO_EXTERNAL_PARTY to "N" in Non-PROD environment, in PROD it will same 
		//	         as being set at the BPM Parameter Level.
		logger.log(Priority.INFO, "[EMC] In EMC." + CLASS_NAME + ".getSendEmail2ExternalPartyFlag() ... ");

		if (this.getHostNEMSDBDetails().equals("")) {
			return p_sendEmailAlert2ExternalParty;
		}
		else {
			// NON PROD including Parallel Environments
			return "N";
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void storeStringIntoDbClob(String ebtEventsRowId, String largeString) {
		Object[] params = new Object[2];
		params[0] = ebtEventsRowId;
		params[1] = largeString;
		String sqlCommand = "INSERT INTO NEM.nem_settlement_raw_files (id, ebe_id, raw_file) "
				+ " VALUES ( SYS_GUID(), ?, ? )";
		try {
			int rowUpdated = jdbcTemplate.update(sqlCommand, params);
		}
		catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}
	}

	public void sendNotification(String filename, String uploadUser, String uploadMethod, String ebtEventsRowId,
			String errorMsg) {
		String sqlCommand = "SELECT TO_CHAR(settlement_date, 'DD-MON-YYYY') settlement_date, "
				+ "to_char(uploaded_date, 'DD-MON-YYYY HH24:MI:SS') uploaded_date, uploaded_by, valid_yn, "
				+ "to_char(validated_date, 'DD-MON-YYYY HH24:MI:SS') validated_date, comments " + " FROM nem_ebt_events WHERE id = '"
				+ ebtEventsRowId + "' ";

		StringBuilder content = new StringBuilder();
		content.append("File Name: " + filename + "\n\n");

		try {
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand);
			for(Map map : list) {
				content.append("File Name: " + filename + "\n\n");
				content.append("File Upload Date and Time: " + map.get("UPLOADED_DATE") + "\n\n");
				content.append("File Upload User: " + uploadUser + "\n\n");
				
				logger.info("COMMENTS : "+map.get("COMMENTS"));
				
				if(map.get("COMMENTS") == null)
					content.append("User Comments:  "+ "\n\n");
				else
					content.append("User Comments: " + map.get("COMMENTS") + "\n\n");
				content.append("Validated Time: " + map.get("VALIDATED_DATE") + "\n\n");
				content.append("Valid: " + map.get("VALID_YN") + "\n\n");
			}
		}
		catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}

		content.append("Error Message: " + errorMsg);
		Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		AlertNotification alertNotifier = new AlertNotification();
		alertNotifier.businessModule = "Bilateral Contract File Upload via " + uploadMethod;
		alertNotifier.recipients = propertiesMap.get("FILE_UPLOAD_FAIL_EMAIL");//BusinessParameters.FILE_UPLOAD_FAIL_EMAIL;
		alertNotifier.subject = "Bilateral Contract file upload failed";
		alertNotifier.content = content.toString();
		alertNotifier.noticeType = "Bilateral Contract File Upload";
		alertImpl.sendEmail(alertNotifier);
	}

	public Date stringToDate(String dateStr, String dateFormat) {
		// convert a date string into a Time object
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
		Date timeValue = null;
		try {
			timeValue = simpleDateFormat.parse(dateStr);
		}
		catch (ParseException e) {
			logger.error("Exception "+e.getMessage());
		}

		return timeValue;
	}

	public int getSysParamNum(String paramName) throws Exception {
		logger.log(Priority.INFO, "[EMC] Starting Function getSysParamNum() for: " + paramName);

		int rowcnt = 0;
		String sqlCommand;

		sqlCommand = "SELECT number_value FROM NEM.aps_system_parameters WHERE upper(name) = upper(?) ";

		rowcnt = jdbcTemplate.queryForObject(sqlCommand, new Object[] {paramName}, Integer.class);

		if (rowcnt == 0) {
			throw new Exception("Can not find the " + paramName + " in the system (APS_SYSTEM_PARAMETERS)");
		}

		logger.log(Priority.INFO, "[EMC] Value of " + paramName + ": " + rowcnt);

		return rowcnt;
	}

	public boolean getFileUploadAllowed(Date tradingdate, int allowedBDplus1, String uploadModule, int bilCutOffTime) throws Exception {
		// ITSM 17449 Merge with DRCAP 2
		// ITSM-17449-Suggested enhancement for BIL Phase 2
		logger.log(Priority.INFO, "[EMC] Starting method EMC." + CLASS_NAME + ".getFileUploadAllowed() - tradingdate :" + tradingdate + " allowedBDplus1 :" + allowedBDplus1 + " uploadModule :" + uploadModule + " bilCutOffTime :" + bilCutOffTime);

		// get Cut off date for file upload with condition TD+N DB 5pm
		if (allowedBDplus1 < 0) {
			logger.log(Priority.INFO, "[EMC] Parameter <allowedBDplus1> cannot be negative.");

			return false;
		}

		if (bilCutOffTime < 0) {
			logger.log(Priority.INFO, "[EMC] Parameter <bilCutOffTime> cannot be negative.");

			return false;
		}

		int upload_allowed = 0;

		// initialize with Not Allowed
		int rowcnt = 0;
		String sqlCommand;
		sqlCommand = "select " + "decode(sign(trunc(max(settlement_date)) + ? / 24 - sysdate),-1,0,0,1,1,1) upload_allowed"
				+ "from (select * from nem.nem_settlement_calendar where settlement_date > ? " + "and day_type = 'B' "
				+ "order by settlement_date " + ") " + "where rownum < ? ";

		try {

			if (rowcnt == 0) {
				throw new Exception("Trading Date is not defined in nem_settlement_calendar !!!");
			}

			Object[] params = new Object[3];
			params[0] = bilCutOffTime;
			params[1] = convertUDateToSDate(tradingdate, "dd-MMM-yyyy");
			params[2] = allowedBDplus1;
			Map<String, Object> map = jdbcTemplate.queryForMap(sqlCommand, params);

			if (map.isEmpty()) {
				throw new Exception("Trading Date is not defined in nem_settlement_calendar !!!");
			} else {
				Integer uploadAllowed = (Integer) map.get("UPLOAD_ALLOWED");
				upload_allowed = (uploadAllowed == null) ? 0 : uploadAllowed;
			}

		}
		catch (Exception e) {
			logger.error("Exception "+e.getMessage());
			throw e;
		}
		if (upload_allowed == 1) {
			return true;
		}
		else {
			// if upload_allowed = 1 then
			return false;
		}
	}

	public boolean getFileUploadAllowedAfterCP66(Date tradingdate, int allowedMinusCD, String uploadModule,
			int bilCutOffTime) {
		// ITSM 17449 Merge with DRCAP 2
		// ITSM-17449-Suggested enhancement for BIL Phase 2
		logger.log(Priority.INFO, "[EMC] Starting method EMC." + CLASS_NAME + ".getFileUploadAllowedAfterCP66() ...");

		// get Cut off date for file upload with condition TD+N DB 5pm
		if (bilCutOffTime < 0) {
			logger.log(Priority.INFO, "[EMC] Parameter <bilCutOffTime> cannot be negative.");

			return false;
		}

		int upload_allowed = 0;
		String sqlCommand;
		Date latestCalendarDay = null;
		Date latestBusinessDay = null;
		Date latestCalendarTime = null;
		Date latestBusinessTime = null;
		Date currentTime = new Date();

		try {
			// Get -10 day
			Calendar cal = Calendar.getInstance();
			cal.setTime(tradingdate);
			cal.add(Calendar.DATE, allowedMinusCD);
			latestCalendarDay = cal.getTime();

			logger.log(Priority.INFO, "tradingdate1=" + tradingdate);

			logger.log(Priority.INFO, "allowedMinusCD1=" + allowedMinusCD);

			logger.log(Priority.INFO, "latestCalendarDay1=" + latestCalendarDay);

			logger.log(Priority.INFO, "latestBusinessDay1=" + latestBusinessDay);

			logger.log(Priority.INFO, "bilCutOffTime1=" + bilCutOffTime);

			logger.log(Priority.INFO, "currentTime1=" + currentTime);

			sqlCommand = "SELECT nsc2.rowindex, " + "      nsc2.settlement_date settlement_date, " + "      nsc2.day_type "
					+ " FROM (  SELECT ROWNUM -1 rowindex, nsc.settlement_date, nsc.day_type "
					+ "           FROM nem.nem_settlement_calendar nsc " + "          WHERE settlement_date >= ? "
					+ "          and nsc.day_type = 'B' " + "       ORDER BY settlement_date asc) nsc2   "
					+ " WHERE nsc2.rowindex  = ABS ( ?)  ";

			Object[] params = new Object[2];
			params[0] = convertUDateToSDate(latestCalendarDay);
			params[1] = new Integer(1);
			List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlCommand, params);

			// Plus 1 BD
			for(Map recordMap : resultList) {
				latestBusinessDay = (Date) recordMap.get("SETTLEMENT_DATE");
			}

			cal = Calendar.getInstance();
			cal.setTime(latestCalendarDay);
			cal.add(Calendar.HOUR, bilCutOffTime);
			latestCalendarTime = cal.getTime();

			cal = Calendar.getInstance();
			cal.setTime(latestBusinessDay);
			cal.add(Calendar.HOUR, bilCutOffTime);
			latestBusinessTime = cal.getTime();

			logger.log(Priority.INFO, "tradeingdate=" + tradingdate);

			logger.log(Priority.INFO, "allowedMinusCD=" + allowedMinusCD);

			logger.log(Priority.INFO, "latestCalendarDay=" + latestCalendarDay);

			logger.log(Priority.INFO, "latestBusinessDay=" + latestBusinessDay);

			logger.log(Priority.INFO, "latestCalendarTime=" + latestCalendarTime);

			logger.log(Priority.INFO, "latestBusinessTime=" + latestBusinessTime);

			logger.log(Priority.INFO, "bilCutOffTime=" + bilCutOffTime);

			if (uploadModule.equals("S")) {
				if (latestCalendarTime.compareTo(currentTime) > 0) {
					upload_allowed = 1;
				}
			}
			else {
				if (latestBusinessTime.compareTo(currentTime) > 0) {
					upload_allowed = 1;
				}
			}
		}
		catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}
		if (upload_allowed == 1) {
			return true;
		}
		else {
			// if upload_allowed = 1 then
			return false;
		}

	}

	public boolean getFileUploadAllowedBeforeCP66(Date tradingdate, int allowedBDplus1, String uploadModule,
			int bilCutOffTime) throws Exception {
		// ITSM 17449 Merge with DRCAP 2
		// ITSM-17449-Suggested enhancement for BIL Phase 2
		logger.log(Priority.INFO, "[EMC] Starting method EMC." + CLASS_NAME + ".getFileUploadAllowedBeforeCP66() ...");

		int upload_allowed = 0;
		try {

			// get Cut off date for file upload with condition TD+N DB 5pm
			if (allowedBDplus1 < 0) {
				logger.log(Priority.INFO, "[EMC] Parameter <allowedBDplus1> cannot be negative.");

				return false;
			}

			if (bilCutOffTime < 0) {
				logger.log(Priority.INFO, "[EMC] Parameter <bilCutOffTime> cannot be negative.");

				return false;
			}

			// initialize with Not Allowed
			int rowcnt = 0;

			String sqlCommand;
			sqlCommand = "select " + "decode(sign(trunc(max(settlement_date)) + ? / 24 - sysdate),-1,0,0,1,1,1) upload_allowed "
					+ "from " + "(select * from nem.nem_settlement_calendar where settlement_date > ? "
					+ "and day_type = 'B' " + "order by settlement_date " + ") " + "where rownum < ? ";

			Object[] params = new Object[3];
			params[0] = bilCutOffTime;
			params[1] = convertUDateToSDate(tradingdate);
			params[2] = allowedBDplus1;
			Map<String, Object> recordMap = jdbcTemplate.queryForMap(sqlCommand, params);

			if (recordMap.size() == 0) {
				throw new Exception("Trading Date is not defined in nem_settlement_calendar !!!");
			} else {
				Integer uploadAllowed = (Integer) recordMap.get("UPLOAD_ALLOWED");
				upload_allowed = (uploadAllowed == null) ? 0 : uploadAllowed;
			}

		}
		catch (Exception e) {
			logger.error("Exception "+e.getMessage());
			throw e;
		}

		if (upload_allowed == 1) {
			return true;
		}
		else {
			// if upload_allowed = 1 then
			return false;
		}

	}


	public String convertUDateToSDate(Date uDate, String format) {
		DateFormat df = new SimpleDateFormat(format);
		java.sql.Date sDate = new java.sql.Date(uDate.getTime());
		return df.format(sDate);
	}

    public java.sql.Date convertUDateToSDate(Date uDate) {
        java.sql.Date sDate = new java.sql.Date(uDate.getTime());
        return sDate;
    }
	
	public List<String> stringToTokenList(String inputString)
	{
		/* 
		split inputString into a list by given a space or a comma

		e.g:
		inputString : Hi, There ,  "Hello World" "12345" test
		delimiter : a space
		result:

		ArrayList [Hi,There,Hello World,12345,test]

		Double Quotes (") will be omitted

		*/

		String delimiterRegex = "((?:\"[^\"]*+\")|[^\\s,]++)*+";

		Pattern p = Pattern.compile(delimiterRegex);
		Matcher m = p.matcher(inputString);

		List<String> myStringTokenList = new ArrayList<String>();

		while (m.find()) {
			String tokenData = m.group().replace("\"", "").trim();

			if (tokenData.length() > 0) {
				myStringTokenList.add(tokenData);
			}
		}

		return myStringTokenList;
	}

	public boolean isBusinessDay(Date day) throws Exception {
		logger.log(Priority.INFO, "[EMC] Starting Function isBusinessDay() ... ");

		// checking whether is non-business day, defining period to settle ...
		String daytype = null;
		try {

			String runDate;
			SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
			runDate = df.format(day);
			String sqlCommand;
			sqlCommand = "select day_type, to_char(settlement_date,'dd-mm-yyyy') from NEM.NEM_SETTLEMENT_CALENDAR where to_char(settlement_date,'dd-mm-yyyy') = ? ";

			List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlCommand, runDate);
			if (resultList.isEmpty()) {
				throw new Exception("settlement date is not defined in nem_settlement_calendar !!!");

			}
			for(Map recordMap : resultList) {
				daytype = (String) recordMap.get("DAY_TYPE");
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		catch (Exception e) {
			logger.error("Exception "+e.getMessage());
			throw e;
		}
		if (!daytype.equals("B")) {
			logger.info("Today (" + new Date() +
					") is not a Business Day. No Scheduled NEMS Controller Run will performed.");
			return false;
		}
		else {
			return true;
		}
	}


	public void updateSEWFileProcessingStatus(String sewOperationType, String msgstr, String sewUploadEventsID, String sewUploadEventsStatus) {
		try {
			// This method will update SEW Table with final SEW File Upload and Approval
			// activities as well errors and alerts (when TD+5 >5pm)
			logger.log(Priority.INFO, "[EMC] " + CLASS_NAME + ".updateSEWFileProcessingStatus() ...");

			if (!msgstr.equals("") || msgstr != null) {
				String sqlCommand = "UPDATE sewfo.sew_file_uploads " + "   SET message = ? "
						+ " WHERE SEW_UPLOAD_EVENTS_ID =? ";
				String[] paramsSEWFileStatus = new String[2];
				paramsSEWFileStatus[0] = msgstr;
				paramsSEWFileStatus[1] = sewUploadEventsID;
				jdbcTemplate.update(sqlCommand,paramsSEWFileStatus);
			}

			if (sewOperationType.equals("APP")) {
				logger.log(Priority.INFO,
						"[EMC] " + CLASS_NAME + ".updateSEWFileProcessingStatus() - Start Updating SEW Upload Event Status for Approval.");
				// For Clawback it is Approval. For BIL it is successful upload.
				String sqlCommand1 = "UPDATE sewfo.sew_upload_events " + "   SET event_status = ? "
						+ " WHERE ID           =? ";
				String[] paramsSEWEventStatus = new String[2];
				paramsSEWEventStatus[0] = sewUploadEventsStatus;
				paramsSEWEventStatus[1] = sewUploadEventsID;
				jdbcTemplate.update(sqlCommand1, paramsSEWEventStatus);
			}
		}
		catch (Exception e) {
			logger.log(Priority.INFO,
					"[EMC] " + CLASS_NAME + ".updateSEWFileProcessingStatus() - Updating SEW File Process / Upload Event Status failed for Operation Type :"
							+ sewOperationType + ", SEW Upload Events ID :" + sewUploadEventsID + ", Time :"
							+ new Date() + ", Details :" + e.getMessage());

			throw e;
		}
	}

	public boolean isAfterRSVREGZeroQtyDispEffDt(Date settDate) {
		// Get Parameter: MPRPT_RSVREG_ZEROQTY_DISP_EFDT
		UtilityFunctions utils = new UtilityFunctions();
		Date RSVREGZeroQtyDispEffDt = this.getSysParamTime("MPRPT_RSVREG_ZEROQTY_DISP_EFDT");

		if (settDate.compareTo(RSVREGZeroQtyDispEffDt) >= 0) {
			return true;
		}
		else {
			return false;
		}
	}

	public Date addDays(Date initDay, int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(initDay);
		cal.add(Calendar.DATE, day);
		return cal.getTime();
	}

	public Date addHours(Date initDay, int hour) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(initDay);
		cal.add(Calendar.HOUR, hour);
		return cal.getTime();
	}
	
	public Date get5PMTime() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY,17);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		return cal.getTime();
	}

	public Date addMinuts(Date initDay, int minute) {
		if (initDay == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(initDay);
		cal.add(Calendar.MINUTE, minute);
		return cal.getTime();
	}

	public DateRange getSettlementDateRange(Date runDate, String runType, boolean checkLastRun) {
		logger.info("[EMC] Starting method " + CLASS_NAME + ".getSettlementDateRage() ...");
		int dayCount;
		int runCount;
		Date toSettDate;
		Date fromSettDate;
		String dayType;
		DateRange dateRange = new DateRange();

		try {
			toSettDate = this.addDays(runDate, -1);

			// get dayCount, dayCount means how many Working Days after Trading Day should
			// perform Settlement Run.
			switch (runType) {
				case "P":
				case "p":
					dayCount = ((int) this.getSysParamNum("PRILIM_RUN_DAYS"));
					break;
				case "F":
				case "f":
					dayCount = ((int) this.getSysParamNum("FINAL_RUN_DAYS"));
					break;
				case "R":
				case "r":
					dayCount = ((int) this.getSysParamNum("SETT_RERUN_R_DAYS"));
					break;
				case "S":
				case "s":
					dayCount = ((int) this.getSysParamNum("SETT_RERUN_S_DAYS"));
					break;
				default:
					throw new Exception("Invalid Run Type: " + runType + " !!!");
			}

			// Calculate the upper range of Settlement Date
			runCount = 0;
			String sqlCommand = "select day_type from NEM.NEM_SETTLEMENT_CALENDAR" +
					" where trunc(settlement_date) = trunc(?)";

			while (dayCount != runCount) {

				Map<String, Object> recordMap = jdbcTemplate.queryForMap(sqlCommand, this.convertUDateToSDate(toSettDate));
				dayType = null;
				if(!recordMap.isEmpty()) {
					dayType = (String) recordMap.get("DAY_TYPE");
				}

				if (dayType == null) {
					throw new Exception("Date not defined in NEM_SETTLEMENT_CALENDAR !!!");
				}
				else if (!dayType.equals("B")) {
					// Not Business Day
					if ((runCount + 1) == dayCount) {
						break;
					}
				}
				else {
					runCount = runCount + 1;

					if (runCount == dayCount) {
						break;
					}
				}

				toSettDate = this.addDays(toSettDate, -1);
			}

			// Calculate lower range of Settlement Date
			fromSettDate = toSettDate;

			while (true) {
				dayType = null;

				Map<String, Object> recordMap = jdbcTemplate.queryForMap(sqlCommand, this.convertUDateToSDate(fromSettDate));
				if(!recordMap.isEmpty()) {
					dayType = (String) recordMap.get("DAY_TYPE");
				}

				if (dayType == null) {
					throw new Exception("Date not defined in NEM_SETTLEMENT_CALENDAR !!!");
				}
				else if (!dayType.equals("B")) {
					// Not Business Day
					fromSettDate = this.addDays(fromSettDate, -1);
				}
				else {
					break;
				}
			}
			logger.info("runDate: "+runDate+" fromSettDate: "+ fromSettDate+" toSettDate: "+toSettDate);
			if (checkLastRun) {
				Date tempFromSettDate = fromSettDate;

				logger.info("[EMC] Get latest Completed and Success Run");

				int rowcnt = 0;
				sqlCommand = "select max(settlement_date) settlement_date from NEM.nem_settlement_runs str, NEM.jam_events eve " +
						"where eve.completed = 'Y' and eve.success = 'Y' and str.eve_id = eve.id " +
						"and run_type = ?";

				List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlCommand, runType);
				for(Map map : resultList) {
					Date maxSettDate = (Date) map.get("SETTLEMENT_DATE");
					if (maxSettDate != null) {
						rowcnt = rowcnt + 1;
						tempFromSettDate = maxSettDate;
						tempFromSettDate = this.addDays(tempFromSettDate, 1);
					}
					break;
				}

				logger.info("[EMC] Get earliest date with Quantities.");

				if (rowcnt == 0) {
					sqlCommand = "SELECT MIN(settlement_date) settlement_date FROM NEM.NEM_SETTLEMENT_QUANTITIES";

					Map<String, Object> recordMap = jdbcTemplate.queryForMap(sqlCommand);
					Date minSettDate = (Date) recordMap.get("SETTLEMENT_DATE");
					if (minSettDate != null) {
						tempFromSettDate = minSettDate;
					}

				}

				// If Last Sett Run is earlier than calculated From Date, use Last Sett Run Date
				int b4 = tempFromSettDate.compareTo(fromSettDate);

				if (b4 < 0) {
					fromSettDate = tempFromSettDate;
				}

				if (runType.equals("R")) {
					Date rRunMinCutoffDate = this.getSysParamTime("SETT_RERUN_R_MIN_CUTOFF");

					if (fromSettDate.compareTo(rRunMinCutoffDate) < 0) {
						fromSettDate = rRunMinCutoffDate;
					}
				}
				else if (runType.equals("S")) {
					Date sRunMinCutoffDate = this.getSysParamTime("SETT_RERUN_S_MIN_CUTOFF");

					if (fromSettDate.compareTo(sRunMinCutoffDate) < 0) {
						fromSettDate = sRunMinCutoffDate;
					}
				}
			}


			dateRange.startDate = truncateTime(fromSettDate);
			dateRange.endDate = truncateTime(toSettDate);

			logger.info("[EMC] Settlement Date Range(" + runType + "): " +
					this.getddMMMyyyy(dateRange.startDate) + " - " + this.getddMMMyyyy(dateRange.endDate));

		}
		catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}
		finally {

		}
		return dateRange;
	}

	public boolean isAfterDRCAPEffectiveDate(Date settDate) {
		// Get Parameter: DR_EFFECTIVE_DATE
		Date effectiveDate = this.getSysParamTime("DR_EFFECTIVE_DATE");

		return isGivenDateAfterReferredDate(settDate, effectiveDate);
	}

	public boolean isGivenDateAfterReferredDate(Date givenDate, Date referredDate) {
		if (givenDate.compareTo(referredDate) >= 0) {
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isDummyDREffectiveDate(Date settDate) {
		// ByPassforSPServices
		// Get Parameter: DUMMY_DR_EFFECTIVE_DATE
		Date DummyDREffDate = this.getSysParamTime("DUMMY_DR_EFFECTIVE_DATE");

		if (settDate != null && settDate.compareTo(DummyDREffDate) >= 0) {
			return true;
		}
		else {
			return false;
		}
	}

	public String getSACByNodeName(String nodeName, String version) throws Exception {
		String sqlCommand = "select external_id from nem_settlement_accounts a, nem_nodes b " +
				"where a.id = b.sac_id and a.version = b.SAC_VERSION " +
				"and b.name = ? and b.version = ? ";
		String sacName = null;

		try {

			Object[] params = new Object[2];
			params[0] = nodeName;
			params[1] = version;
			List<Map<String, Object>> recordList = jdbcTemplate.queryForList(sqlCommand, params);
			for(Map map : recordList) {
				sacName = (String) map.get("external_id");
				break;
			}

			if (sacName == null) {
				logger.log(Priority.INFO, "[EMC] SAC not found for Node Name: " + nodeName);

				throw new Exception("SAC not found for Node Name: " + nodeName);
			}
		}
		catch (SQLException e) {
			logger.error("Exception "+e.getMessage());
		}

		return sacName;
	}

	public void makeDirs(String dirName) throws Exception {
		File f = new File(dirName);
		boolean rst = f.mkdirs();

		if (rst == false) {
			throw new Exception("Cannot Create Directory: " + dirName);
		}

		logger.log(Priority.INFO, "[EMC] Make directory: " + dirName + ": " + (rst == true ? "Success" : "Fail"));
	}

	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "";
	}

	public String dumpArray(List<String> ar) {
		// return array contents as comma-delimited string
		String dbgStr = "[";

		for (int x = 0; x < ar.size(); x++) {
			dbgStr = dbgStr + x + ",";
		}

		dbgStr = dbgStr.substring(0, dbgStr.length() - 1) + "]";

		return dbgStr;
	}


	public SettlementRunInfo getSettRunInfo(String pkgId) throws SettRunInfoException {
		/*
		 * Get Settlement Run Information from Package ID. The Settlement Run
		 * Information includes: Run Id, Run Event Id, Run Type, Run Date, Settlement
		 * Date, Authorisation Status etc.
		 *
		 * @param pkgId
		 *
		 * @return SettlementRunInfo
		 *
		 */

		logger.log(Priority.INFO, "[EMC] Starting method " + CLASS_NAME + ".getSettRunInfo() ...");

		// Get Settlement Run Id, Run Event Id, Run Status from PackageId
		String sqlCommand = "SELECT str.id, str.run_type, str.run_date, str.settlement_date, "
				+ "str.eve_id, NVL(eve.success,'N') success, NVL(eve.completed, 'N') completed "
				+ "FROM NEM.jam_events eve, NEM.nem_settlement_runs str "
				+ "WHERE eve.ID = str.eve_id and str.pkg_id = ?";
		SettlementRunInfo runInfo = new SettlementRunInfo();
		runInfo.runId = null;

		try {

			List<Map<String, Object>> recordList = jdbcTemplate.queryForList(sqlCommand, pkgId);
			for(Map map : recordList) {
				runInfo.runId = (String) map.get("ID");
				runInfo.runType = (String) map.get("RUN_TYPE");
				runInfo.runDate = (Date) map.get("RUN_DATE");
				runInfo.settlementDate = (Date) map.get("SETTLEMENT_DATE");
				runInfo.runEveId = (String) map.get("EVE_ID");
				runInfo.success = (String) map.get("SUCCESS");
				runInfo.completed = (String) map.get("COMPLETED");

				logger.log(Priority.INFO,
						"[EMC] Run Id: " + runInfo.runId + ", runDate: " + this.getddMMMyyyyhhmmss(runInfo.runDate)
								+ ", Settlement Date: " + this.getddMMMyyyyhhmmss(runInfo.settlementDate)
								+ ", EventId: " + runInfo.runEveId + ", Success: " + runInfo.success + ", Completed: "
								+ runInfo.completed);

				break;
			}

			if (runInfo.runEveId == null) {
				throw new SettRunInfoException("Settlement Run Event not found for package Id: " + pkgId);
			}

			// Get Authorisation Status of the Settlement Run
			runInfo.authStatus = null;

			sqlCommand = "SELECT authorisation_status FROM NEM.nem_package_authorisations"
					+ " WHERE pkg_id = ? ORDER BY authorisation_date DESC";

			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, pkgId);
			for(Map map : list) {
				runInfo.authStatus = (String) map.get("AUTHORISATION_STATUS");

				logger.log(Priority.INFO, "[EMC] Authorisation Status: " + runInfo.authStatus);

				// Only get the latest Authorisation Status
				break;
			}

			if (runInfo.authStatus == null) {

				sqlCommand = "SELECT mrr.approved FROM NEM.nem_market_clearing_runs mcr,"
						+ " nem_market_clearing_reruns mrr WHERE mcr.pkg_id = ? AND"
						+ " mcr.id = mrr.price_identifier AND mcr.id = mrr.quantity_identifier AND"
						+ " mcr.run_date = mrr.run_date AND mcr.period = mrr.period AND"
						+ " mcr.mcr_type = 'RER' AND ROWNUM = 1";



				List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlCommand, pkgId);
				runInfo.authStatus = null;
				for(Map recordMap : resultList) {
					runInfo.authStatus = (String) recordMap.get("APPROVED");

					if (runInfo.authStatus.equals("Y")) {
						runInfo.authStatus = "AUTHORISED";
					}
					else {
						runInfo.authStatus = null;
					}
				}

			}

			if (runInfo.authStatus == null) {
				runInfo.authStatus = "WAITING";
			}

		}
		catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}
		return runInfo;

	}


	public String getLatestRunId(Date settlementDate, String runType) {
		// This function get latest Success RunId for give Settlement Date and Run Type
		String runId = null;
		String sqlCmd = "SELECT run_date, settlement_date, SR.id, seq, run_type" +
				" FROM NEM.NEM_SETTLEMENT_RUNS SR, NEM.JAM_EVENTS EVE" +
				" WHERE SR.EVE_ID = EVE.ID AND RUN_TYPE = ? AND" +
				" settlement_date = trunc(?) AND SR.RUN_DATE = " +
				" (SELECT MAX(SR1.RUN_DATE) FROM NEM.NEM_SETTLEMENT_RUNS SR1 " +
				" WHERE SR1.SETTLEMENT_DATE = SR.SETTLEMENT_DATE AND " +
				" SR1.RUN_TYPE = ?) AND COMPLETED = 'Y' AND SUCCESS = 'Y'";

		try {
			Object[] params = new Object[3];
			params[0] = runType;
			params[1] = this.convertUDateToSDate(settlementDate);
			params[2] = runType;
			List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlCmd, params);
			for (Map recordMap : resultList) {
				runId = (String) recordMap.get("ID");
				break;
			}
		}
		catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}
		return runId;

	}

	public Map<Integer, List<String>> getMCRIds(Date settlementDate) {
		logger.log(Priority.INFO, "[EMC] Getting MCR string for new settlement run...");
		Map<Integer, List<String>> mcrIds = new HashMap<Integer, List<String>>();
		try {

			String sqlMCRun;
			String sqlMCRerun;
			String settlementdate;
			Date authDate;
			String mcrRunId;
			Date appDate;
			String mcrRerunId;
			SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
			// mcrIds as String[]

			String mcrRunType = null;
			String mcrReRunType = null;
			String mcrType = null;
			settlementdate = df.format(settlementDate);
			sqlMCRun = "SELECT DENSE_RANK() over (order by PKA.authorisation_date desc ) rnk_no, " +
					"PKA.authorisation_date auth_date, MCR.ID mcr_id, 'NONRER' mcr_run_type FROM NEM.NEM_MARKET_CLEARING_RUNS MCR, " +
					"NEM.PAV_PACKAGES PKG, NEM.NEM_PACKAGE_AUTHORISATIONS PKA " +
					"WHERE to_char(MCR.RUN_DATE,'dd-mm-yyyy') = ? AND MCR.MCR_TYPE IN ('DPR','ADM','RGP') " +
					"AND mcr.period = ? AND mcr.pkg_id = pkg.id " +
					"AND PKG.ID = PKA.PKG_ID AND PKA.authorisation_status ='AUTHORISED' ";
			sqlMCRerun = "SELECT DENSE_RANK() over (order by mcr.approval_datetime desc ) rnk_no, " +
					"mcr.approval_datetime auth_date, mcr.ID mcr_id, 'RER' mcrReRunType  " +
					"FROM NEM.NEM_MARKET_CLEARING_RERUNS mcr " +
					"WHERE to_char(mcr.run_date,'dd-mm-yyyy') = ? AND mcr.period = ? " +
					"AND MCR.MCR_TYPE = 'RER' AND mcr.approved = 'Y'";

			// Get Total Periods from System Parameters
			int totalPeriod = this.getSysParamNum("NO_OF_PERIODS");


			for (int i = 1; i <= totalPeriod; i++) {
				// Clear variable values for a new loop
				authDate = null;
				mcrRunId = null;
				appDate = null;
				mcrRerunId = null;

				Object[] params = new Object[2];
				params[0] = settlementdate;
				params[1] = new Integer(i);
				List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlMCRun, params);
				BigDecimal rnkNo;
				for(Map recordMap : resultList) {
					rnkNo = (BigDecimal) recordMap.get("RNK_NO");
					if (rnkNo.intValue() == 1) {
						authDate = (Date) recordMap.get("AUTH_DATE");
						mcrRunId = (String) recordMap.get("MCR_ID");
						mcrRunType = (String) recordMap.get("MCR_RUN_TYPE");
					}
				}


				// Get the latest authorised market clearing *** RERUN ***
				Object[] params1 = new Object[2];
				params1[0] = settlementdate;
				params1[1] = new Integer(i);
				List<Map<String, Object>> resultList1 = jdbcTemplate.queryForList(sqlMCRerun, params1);
				BigDecimal rnkNo1;
				for(Map recordMap : resultList1) {
					rnkNo1 = (BigDecimal) recordMap.get("RNK_NO");
					if (rnkNo1.intValue() == 1) {
						appDate = (Date) recordMap.get("AUTH_DATE");
						mcrRerunId = (String) recordMap.get("MCR_ID");
						mcrReRunType = (String) recordMap.get("mcrreruntype");
					}
				}


				String mcrId = null;

				// Determining which MC run id to use for the settlement...';
				if (mcrRerunId != null && appDate != null && mcrRunId != null && authDate != null) {
					if (appDate.compareTo(authDate) > 0) {
						mcrId = mcrRerunId;
						mcrType = mcrReRunType;
					}
					else {
						mcrId = mcrRunId;
						mcrType = mcrRunType;
					}
				}
				else {
					if (mcrRerunId != null) {
						mcrId = mcrRerunId;
						mcrType = mcrReRunType;
					}
					else {
						if (mcrRunId != null) {
							mcrId = mcrRunId;
							mcrType = mcrRunType;
						}
						else {
							throw new Exception("No Authorised Market Clearing Run available for the Trading Date " + settlementdate + " and Period " + i);
						}
					}
				}

				List<String> lstMcrIds = new ArrayList<String>();
				lstMcrIds.add(0, mcrId);
				lstMcrIds.add(1, mcrType);
				mcrIds.put((i - 1), lstMcrIds);
			}
		}
		catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}

		return mcrIds;

	}

	public boolean isAfterNTEffectiveDate(Date settDate) {
		// Get Parameter: NT_EFFECTIVE_DATE
		Date ntEffectiveDate = this.getSysParamTime("NT_EFFECTIVE_DATE");

		if (settDate.compareTo(ntEffectiveDate) >= 0) {
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isAfterPriceCapEffectiveDate(Date settDate) {
		// Get Parameter: PRICECAP_EFF_DATE
		Date lngEffectiveDate = this.getSysParamTime("PRICECAP_EFF_DATE");

		if (settDate.compareTo(lngEffectiveDate) >= 0) {
			return true;
		}
		else {
			return false;
		}
	}

	public Date getSettlementDate(Date today, int daysBefore) {
		logger.log(Priority.INFO, "[EMC] Starting method UtilityFunctions.getSettlementDate() ...");

		Date settlementDate = today;
		try {
			// get Settlement Date for N days ago
			if (daysBefore < 0) {
				logger.log(Priority.INFO, "[EMC] Parameter <daysBefore> cannot be negative.");

				return null;
			}

			int daycount = daysBefore;
			int runcount = 0;
			String daytype = null;
			String sqlCommand;


			while (daycount != runcount) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(settlementDate);
				cal.add(Calendar.DATE, (-1));
				settlementDate = cal.getTime();
				int rowcnt = 0;
				sqlCommand = "select day_type from NEM.NEM_SETTLEMENT_CALENDAR where to_char(settlement_date,'dd-mon-yyyy') = to_char(?,'dd-mon-yyyy') ";

				List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlCommand, this.convertUDateToSDate(settlementDate));
				for(Map recordMap : resultList) {
					rowcnt = rowcnt + 1;
					daytype = (String) recordMap.get("DAY_TYPE");
					break;
				}


				if (rowcnt == 0) {
					throw new Exception("settlement date is not defined in nem_settlement_calendar !!!");
				}

				if (!daytype.equals("B")) {
					// Not Business Day
				}
				else {
					runcount = runcount + 1;

					if (runcount == daycount) {
						break;
					}
				}
			}

		}
		catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}
		return settlementDate;

	}

	public String getSettRunStatus(Date settlementDate, String runType) {
		String runStatus = null;
		String packageId;
		String completed = "";
		String success = "";
		String authStatus = "";
		String sqlCommand;
		sqlCommand = "SELECT eve_id, pkg_id, completed, success from NEM.nem_settlement_runs nsr, " +
				"NEM.jam_events e, NEM.jam_event_schedules es " +
				"WHERE nsr.eve_id = e.id and e.esd_id = es.id (+) and " +
				"run_type = ? and settlement_date = trunc(?) order by nsr.seq";
		String sqlCommand2 = "SELECT DECODE (authorisation_status,'WAITING', 'W', " +
				"'AUTHORISED', 'A', 'NOT AUTHORISED', 'N', '1ST AUTHORISED', '1', '') status " +
				"FROM NEM.NEM_PACKAGE_AUTHORISATIONS " +
				"WHERE pkg_id = ? ORDER BY authorisation_date DESC";

		try {
			Object[] params = new Object[2];
			params[0] = runType;
			params[1] = this.convertUDateToSDate(settlementDate);
			List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlCommand, params);
			for(Map recordMap : resultList) {
				packageId = (String) recordMap.get("PKG_ID");
				completed = (String) recordMap.get("COMPLETED");
				success = (String) recordMap.get("SUCCESS");

				if (completed.equalsIgnoreCase("N")) {
					runStatus = "P";

					// In Progress
				}
				else {
					if (success.equalsIgnoreCase("N")) {
						runStatus = "E";

						// Error
						// exit
					}
					else if (success.equalsIgnoreCase("Y")) {
						List<Map<String, Object>> resultList2 = jdbcTemplate.queryForList(sqlCommand2, packageId);
						for(Map recordMap2 : resultList2) {
							authStatus = (String) recordMap2.get("status");
							break;
						}
						if (authStatus.equalsIgnoreCase("W") || authStatus.equalsIgnoreCase("A") || authStatus.equalsIgnoreCase("1")) {
							runStatus = "F";

							// Finished
						}
						else {
							runStatus = "C";

							// Canceled
						}
					}
				}
			}
		}
		catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}

		return runStatus;

	}
	
	public String getProcessUrl(String procesName, Date settlementDate)
	{
		String versionSql = "select URL from SEBO.SEB_SETTLEMENT_SERVICE_URL where application = '"+procesName+"' and "+
				" seq=(select max(seq) from SEBO.SEB_SETTLEMENT_SERVICE_URL where application = '"+procesName+"' and "+
				" '"+this.dateToStringddMonYYYY(settlementDate)+"' between effective_date and expiry_date)";
		
		String processUrl = null;
		try {
			processUrl = queryForObject(versionSql, String.class);
		}
		catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}
		return processUrl;
	}


	//dd MMM yyyy HH:mm:ss

	public String getddMMMyyyyhhmmss(Date date) {
		DateFormat df = new SimpleDateFormat(UtilityFunctions.getProperty("DISPLAY_TIME_FORMAT"));
		return df.format(date);
	}
	//HH:mm

	public String geHHmm(Date date) {
		DateFormat df = new SimpleDateFormat(BusinessParameters.HHMM_FORMAT);
		return df.format(date);
	}
	//dd-MM-yyyy

	public String getddMMyyyy(Date date) {
		if(date==null) {
			return null;
		}
		DateFormat df = new SimpleDateFormat(UtilityFunctions.getProperty("DATE_FORMAT"));
		return df.format(date);
	}
	//dd MMM yyyy

	public String getddMMMyyyy(Date date) {
		DateFormat df = new SimpleDateFormat(UtilityFunctions.getProperty("DISPLAY_DATE_FORMAT"));
		return df.format(date);
	}
	//yyyyMMdd_HHmmss

	public String getyyyyMMdd_HHmmss(Date date) {
		DateFormat df = new SimpleDateFormat(BusinessParameters.DISPLAY_DATE_FORMAT_1);
		return df.format(date);
	}
	//yyyyMMddHHmmss

	public String getyyyyMMddHHmmss(Date date) {
		DateFormat df = new SimpleDateFormat(BusinessParameters.DISPLAY_DATE_FORMAT_2);
		return df.format(date);
	}
	//dd-MMM-yyyy

	public String getddMMMyyyyHyphen(Date date) {
		DateFormat df = new SimpleDateFormat(BusinessParameters.DISPLAY_DATE_FORMAT_3);
		return df.format(date);
	}
	//MM/dd/yyyy HH:mm:ss

	public String getTimeFormat(Date date) {
		DateFormat df = new SimpleDateFormat(UtilityFunctions.getProperty("TIME_FORMAT"));
		return df.format(date);
	}
	public String getyyyyMMddHHmmssSSS(Date date) {
		DateFormat df = new SimpleDateFormat(BusinessParameters.TIME_FORMAT_1);
		return df.format(date);
	}

	public Date getFirstDateOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}


	public Date lastDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}

	public Date firstDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}

	public String dateToString(Date date) {
		SimpleDateFormat UTC_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		String retDate = "";
		try {
			retDate = UTC_DATE_FORMATTER.format(date);
		} catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}
		return retDate;
	}

	public String dateToStringddMonYYYY(Date date) {
		SimpleDateFormat UTC_DATE_FORMATTER = new SimpleDateFormat("dd-MMM-yyyy");
		String retDate = "";
		try {
			retDate = UTC_DATE_FORMATTER.format(date);
		} catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}
		return retDate;
	}

	public Date stringToDate(String strDate) {
		SimpleDateFormat UTC_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		Date retDate = null;
		try {
			retDate = UTC_DATE_FORMATTER.parse(strDate);
		} catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}
		return retDate;
	}
	
	public static XMLGregorianCalendar toXMLGregorianCalendar(Date date) throws DatatypeConfigurationException {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date(date.getTime()));
		XMLGregorianCalendar tradingDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		return tradingDate;
	}

	public static void close(ResultSet rs) {
		try {
			rs.close();
		}
		catch (SQLException e) {
			logger.error("Exception "+e.getMessage());
		}
	}

	public static void close(Statement stmt) {
		try {
			stmt.close();
		}
		catch (SQLException e) {
			logger.error("Exception "+e.getMessage());
		}
	}

	public static void close(Connection connection) {
		try {
			connection.close();
		}
		catch (SQLException e) {
			logger.error("Exception "+e.getMessage());
		}
	}

	public List<Map<String, Object>> getNemEbtEvents(String ebtEventsRowId) {
		String sqlCommand = "SELECT TO_CHAR(settlement_date, 'DD-MON-YYYY') settlement_date,"
				+ " TO_CHAR(uploaded_date, 'DD-MM-YYYY HH24:MI:SS') uploaded_date, uploaded_by, valid_yn,"
				+ " TO_CHAR(validated_date, 'DD-MM-YYYY HH24:MI:SS') validated_date, comments "
				+ " FROM nem_ebt_events "
				+ " WHERE id = '" + ebtEventsRowId + "' ";

		return jdbcTemplate.queryForList(sqlCommand);
	}

	public int convertMonth(String item) {
		int result = 0;

		if (item.toLowerCase().equals("jan")) {
			result = 1;
		}
		else if (item.toLowerCase().equals("feb")) {
			result = 2;
		}
		else if (item.toLowerCase().equals("mar")) {
			result = 3;
		}
		else if (item.toLowerCase().equals("apr")) {
			result = 4;
		}
		else if (item.toLowerCase().equals("may")) {
			result = 5;
		}
		else if (item.toLowerCase().equals("jun")) {
			result = 6;
		}
		else if (item.toLowerCase().equals("jul")) {
			result = 7;
		}
		else if (item.toLowerCase().equals("aug")) {
			result = 8;
		}
		else if (item.toLowerCase().equals("sep")) {
			result = 9;
		}
		else if (item.toLowerCase().equals("oct")) {
			result = 10;
		}
		else if (item.toLowerCase().equals("nov")) {
			result = 11;
		}
		else if (item.toLowerCase().equals("dec")) {
			result = 12;
		}

		return result;

	}

	public String base64Encode(String fileName) {
		String fileContent = null;
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(fileName));
			fileContent = base64Encode(encoded);
		}
		catch (IOException e) {
			logger.error("Exception "+e.getMessage());
		}
		return fileContent;
	}

	public int getHoursFromDate(Date date) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	public boolean isAfterWMEPunroundedEMCfeesEffectiveDate(Date settDate)
	{
		// Get Parameter: WMEP_UNROUNDED_ADMFEE_EFFDATE
		Date WMEPunroundedEMCFEEsEffDate = this.getSysParamTime("WMEP_UNROUNDED_ADMFEE_EFFDATE");

		if (settDate.compareTo(WMEPunroundedEMCFEEsEffDate) >= 0) {
		    return true;
		}
		else {
		    return false;
		}
	}

	public Date getLastBusinessDayOfMonth(Date dateArg)
	{
		/*
		Returns the date of the last business day of previous month in @dateArg
		Logic:  1) Set Day to 01
				2) Move back 1 day to bring us to last day of month in @datein
				3) Loop and keep moving back 1 day until day type is 'B' */
		logger.log(Priority.INFO, "[EMC] Starting method EMC.UtilityFunctions.getLastBusinessDayOfMonth() ...");
		Date varDate = null;
		try {

			int runcount = 0;
			String daytype = "";
			String sqlCommand;

			SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
			SimpleDateFormat fmt1 = new SimpleDateFormat("MM-yyyy");


			String dateStr = "01-" + fmt1.format(dateArg);

			varDate = fmt.parse(dateStr);

			// bring varDate back to last day of @dateArg
			Calendar cal = Calendar.getInstance();
			cal.setTime(varDate);
			cal.add(Calendar.DATE, (-1));
			varDate = cal.getTime();
			sqlCommand = "select day_type from NEM.NEM_SETTLEMENT_CALENDAR "
					+ "where to_char(settlement_date,'dd-mm-yyyy') = to_char(?,'dd-mm-yyyy') ";

			while (runcount < 31) {
				int rowcnt = 0;

				List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlCommand, this.convertUDateToSDate(varDate));
				for(Map recordMap : resultList) {
					rowcnt = rowcnt + 1;
					daytype = (String) recordMap.get("DAY_TYPE");
					break;
				}

				if (rowcnt == 0) {
						throw new Exception("settlement date is not defined in nem_settlement_calendar !!!");
				}

				if (daytype != "B") {
					// Not Business Day, check previous day
					runcount = runcount + 1;
					cal = Calendar.getInstance();
					cal.setTime(varDate);
					cal.add(Calendar.DATE, (-1));
					varDate = cal.getTime();
				} else {
					break;
				}
			}
			logger.log(Priority.INFO,
					"[EMC] Last Business Day of Month for " + fmt.format(dateArg) + " is " + fmt.format(varDate));


		} catch (Exception e) {
			logger.error("Exception "+e.getMessage());

		}
		
		return varDate;

	}
	
	public static String getProperty(String key)
	{
		Properties prop = new Properties();
		InputStream input = null;

		String ret = "";
		try {
			input = new FileInputStream(BUSINESS_PARAMETERS_PRPPERTY_FILE);
			prop.load(input);
			ret = prop.getProperty(key, "");
		} catch (IOException ex) {
			logger.error("Exception "+ex.getMessage());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.error("Exception "+e.getMessage());
				}
			}
		}
		return ret;
	}
	
	public static int getIntProperty(String key)
	{
		try {
			String ss = UtilityFunctions.getProperty(key);
			return Integer.parseInt(ss);
		} catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}
		return 0;
	}
	
	public static Map<String, String> getProperties()
	{
		Properties prop = new Properties();
		InputStream input = null;

		Map<String, String> retMap = new HashMap<String, String>();
		try {

			//input = new FileInputStream("\\\\\\\\192.168.0.3\\\\Sharing\\\\EMC\\\\SHaRP\\\\Properties\\\\settlement-bpm.properties");
			//input = new FileInputStream("D:\\Sharp-Migration\\settlement-bpm.properties");
			input = new FileInputStream("/app/properties/settlement-bpm.properties");
			// load a properties file
			prop.load(input);
			retMap.put("soapServiceUrl", prop.getProperty("drcap.soa.soap.service.url"));
			retMap.put("bpmsUserId", prop.getProperty("kie.server.auth.username"));
			retMap.put("bpmsEncrypted", prop.getProperty("kie.server.auth.encrypted"));
			retMap.put("bpmsKey", prop.getProperty("kie.server.emc.key"));
			retMap.put("bpmsPWD", prop.getProperty("kie.server.auth.password"));
			retMap.put("rulesHost" , prop.getProperty("settlement.bpm.rules.url"));
			retMap.put("csvStorage" , prop.getProperty("settlement.bpm.rules.csv.storage"));
			retMap.put("invrptservicesURL" , prop.getProperty("drcap.invrptservices.service.url"));
			retMap.put("penservicesURL" , prop.getProperty("drcap.penservices.service.url"));
			// get the property value and print it out
			
			retMap.put("ALLOW_SCHEDULED_TASK" , prop.getProperty("ALLOW_SCHEDULED_TASK"));
			retMap.put("DAILY_RUN_SUMMARY_EMAIL" , prop.getProperty("DAILY_RUN_SUMMARY_EMAIL"));
			retMap.put("EMCPSO_UPLOAD_FAIL_EMAIL" , prop.getProperty("EMCPSO_UPLOAD_FAIL_EMAIL"));
			retMap.put("FILE_UPLOAD_EBT_EMAIL" , prop.getProperty("FILE_UPLOAD_EBT_EMAIL"));
			retMap.put("FILE_UPLOAD_FAIL_EMAIL" , prop.getProperty("FILE_UPLOAD_FAIL_EMAIL"));
			retMap.put("MSSL_DESTINATION_URL" , prop.getProperty("MSSL_DESTINATION_URL"));
			retMap.put("SETTLEMENT_RUN_EMAIL" , prop.getProperty("SETTLEMENT_RUN_EMAIL"));
			retMap.put("DAILY_RUN_SUMMARY_EMAIL" , prop.getProperty("DAILY_RUN_SUMMARY_EMAIL"));
		} catch (IOException ex) {
			logger.error("Exception "+ex.getMessage());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.error("Exception "+e.getMessage());
				}
			}
		}
		return retMap;
	}

	public <T> T queryForObject(String versionSql, Class<T> clazz) {
		try {
			return jdbcTemplate.queryForObject(versionSql, clazz);
		}
		catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public Date truncateTime(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}


	public Object[] queryforList(String sqlCommand, Object[] params, String... resultSet) {
		Object[] result = new Object[resultSet.length];
		List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlCommand, params);
		int idx = 0;
		for(Map recordMap : resultList) {
			result[idx] = recordMap.get(resultSet[idx++]);
		}
		return result;
	}

	public Object[] queryforList(String sqlCommand, String... resultSet) {
		Object[] result = new Object[resultSet.length];
		List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlCommand);
		int idx = 0;
		
		for (Iterator iterator = resultList.iterator(); iterator.hasNext();) {
			Map<String, Object> map = (Map<String, Object>) iterator.next();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
		        result[idx++] = entry.getValue();
		    }
		}
		return result;
	}

	public static List<Date> getDaysBetweenDates(Date startdate,
			Date enddate) {

		List<Date> dates = new ArrayList<Date>();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startdate);

		while (calendar.getTime().before(enddate)) {
			Date result = calendar.getTime();
			java.sql.Date changedDate = new java.sql.Date(result.getTime());

			dates.add(changedDate);
			calendar.add(Calendar.DATE, 1);
		}
		dates.add(enddate);


		return dates;
	}
}
