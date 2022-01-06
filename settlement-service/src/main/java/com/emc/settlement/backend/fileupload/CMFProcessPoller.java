/**
 *
 */
package com.emc.settlement.backend.fileupload;

import static com.emc.settlement.model.backend.constants.BusinessParameters.PROCESS_NAME_CMFPROCESS_POLLER;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.emc.settlement.common.AlertNotificationImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.constants.BusinessParameters;
import com.emc.settlement.model.backend.pojo.AlertNotification;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author DWTN1561
 *
 */
@Service
public class CMFProcessPoller {

	/**
	 *
	 */
	public CMFProcessPoller() {
		// TODO Auto-generated constructor stub
	}

	private static final Logger logger = Logger.getLogger(CMFProcessPoller.class);

	@Autowired
	private UtilityFunctions utilityFunctions;

	@Autowired
	private AlertNotificationImpl notificationImpl;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Transactional
	public Map<String, Object> checkUnresponseProcesses(Map<String, Object> variableMap) {

		Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		try{

			String ebtEventId = null;
			String eventId = null;
			String filename = null;
			Integer instanceCnt = 0;

			String msgStep = PROCESS_NAME_CMFPROCESS_POLLER + ".checkUnresponseProcesses()";
			logger.log(Priority.INFO, "[POLL] Start polling Corrected Metering Files ..." + msgStep);

			AlertNotification alert;
			Date startProcessingTime;
			Date uploadedTime = null;
			String sqlCmd = "select nee.id, nee.eve_id, nee.filename, " +
					"nee.start_processing_time, nee.uploaded_date " +
					"from nem_ebt_events nee, nem_settlement_raw_files nsr " +
					"where nsr.ebe_id = nee.id and event_type = 'CTR' " +
					"and ((nee.valid_yn <> 'Y' and nee.valid_yn <> 'N') " +
					"or nee.valid_yn is null) "	+
					"and nee.processing_yn = 'Y' ";
			List<Map<String, Object>> eventList = jdbcTemplate.queryForList(sqlCmd);
			for(Map eventMap : eventList) {
				ebtEventId = (String) eventMap.get("ID");
				eventId = (String) eventMap.get("EVE_ID");
				filename = (String) eventMap.get("FILENAME");
				startProcessingTime = (Date) eventMap.get("START_PROCESSING_TIME");
				uploadedTime = (Date) eventMap.get("UPLOADED_DATE");
				instanceCnt = instanceCnt + 1;

				Date delayStartProcessingTime = utilityFunctions.addMinuts(startProcessingTime, 30);
				Date delayEndProcessingTime = utilityFunctions.addMinuts(startProcessingTime, 47);
				
				logger.info( "delayStartProcessingTime : " + delayStartProcessingTime+" delayEndProcessingTime : "+delayEndProcessingTime+"  Now : "+new Date() );
				
				if (delayStartProcessingTime!=null && delayEndProcessingTime!=null && (new Date().compareTo(delayStartProcessingTime) > 0) && (new Date().compareTo(delayEndProcessingTime) < 0)) {
					logger.log(Priority.INFO, "[POLL] Corrected Metering File (CTR): " + filename +
							" has beeb processed more than 30 minutes.");

					// The CMF file has been processed more than 30 min, will send an alert
					alert = new AlertNotification();
					alert.businessModule = "Receive MSSL Metering File";
					alert.content = "Filename: " + filename + ";\n\n" +
							"File upload time: " + utilityFunctions.getddMMMyyyyhhmmss(uploadedTime) + ";\n\n" +
							"Error Message: the CTR file has been processed more than 30 minutes. Event ID: " + eventId;
					alert.recipients = propertiesMap.get("FILE_UPLOAD_FAIL_EMAIL");//BusinessParameters.FILE_UPLOAD_FAIL_EMAIL;
					alert.noticeType = "MSSL Metering Data Input Preparation";
					notificationImpl.sendEmail(alert);
				}
			}

			if (instanceCnt > 1) {
				logger.log(Priority.INFO, "[POLL] There are (" + instanceCnt + ") CTR files been processed in same time.");

				// The CMF file has been processed more than 30 min, will send an alert
				alert = new AlertNotification();
				alert.businessModule = "Receive MSSL Metering File";
				alert.content = "Filename: " + filename + ";\n\n" +
						"File upload time: " + utilityFunctions.getddMMMyyyyhhmmss(uploadedTime) + ";\n\n" +
						"Error Message: There are (" + instanceCnt + ") CTR files been processed in same time. Event ID: " +
						eventId;
				alert.recipients = propertiesMap.get("FILE_UPLOAD_FAIL_EMAIL");//BusinessParameters.FILE_UPLOAD_FAIL_EMAIL;
				alert.noticeType = "MSSL Metering Data Input Preparation";
				notificationImpl.sendEmail(alert);
			}

			variableMap.put("instanceCnt", instanceCnt);
			variableMap.put("ebtEventId", ebtEventId);
			variableMap.put("eventId", eventId);
			variableMap.put("filename", filename);
			logger.info("Returning from service "+msgStep+" - ( instanceCnt :" + instanceCnt
					+ " ebtEventId :" + ebtEventId
					+ " filename :" + filename + ")");
			return variableMap;
		}
		catch(Exception e) {
			throw e;
		}
	}

	@Transactional
	public Map<String, Object> checkUnProcessedCMF(Map<String, Object> variableMap) {

		try{

			String ebtEventId = (String) variableMap.get("ebtEventId");
			String eventId = (String) variableMap.get("eventId");
			String filename = (String) variableMap.get("filename");
			Boolean pendingCMF = false;

			String msgStep = PROCESS_NAME_CMFPROCESS_POLLER + ".checkUnProcessedCMF()";

			logger.log(Priority.INFO, "[POLL] Starting Activity " + msgStep + " ...");
			logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( ebtEventId : " + ebtEventId
					+ " eventId : " + eventId
					+ " filename : " + filename + ")");

			Date uploadedTime;

			String sqlCmd = "select nee.id, nee.eve_id, nee.filename, " +
					"nee.uploaded_date, nee.uploaded_by " +
					"from nem_ebt_events nee, nem_settlement_raw_files nsr " +
					"where nsr.ebe_id = nee.id and event_type = 'CTR' " +
					"and nee.valid_yn is null order by nee.uploaded_date asc";

			List<Map<String, Object>> eventList = jdbcTemplate.queryForList(sqlCmd);
			for(Map eventMap : eventList) {
				uploadedTime = (Date) eventMap.get("uploaded_date");
				uploadedTime = utilityFunctions.addDays(uploadedTime, 3);

				logger.info( "uploadedTime : " + uploadedTime+"  Now : "+new Date() );
				if (uploadedTime.compareTo(new Date()) > 0) {
					// Record that uploaded more than 3 days will not be considered.
					ebtEventId = (String) eventMap.get("ID");
					eventId = (String) eventMap.get("EVE_ID");
					filename = (String) eventMap.get("FILENAME");
					pendingCMF = true;

					logger.log(Priority.INFO, "[POLL] Found un-processed CTR file: " + filename);

					break;
				}
			}

			if (pendingCMF == false) {
				logger.log(Priority.INFO, "[POLL] There is no un-processed CTR files.");
			}

			variableMap.put("pendingCMF", pendingCMF);
			variableMap.put("ebtEventId", ebtEventId);
			variableMap.put("eventId", eventId);
			variableMap.put("filename", filename);
			logger.info("Returning from service "+msgStep+" - ( pendingCMF :" + pendingCMF
					+ " ebtEventId :" + ebtEventId
					+ " filename :" + filename + ")");
			return variableMap;

		}catch (Exception e) {
			logger.log(Priority.INFO, "[POLL] Exception occurs: " + e.getMessage());
			throw e;
		}
	}

	@Transactional(readOnly = true)
	public Map<String, Object> getShareplexMode(Map<String, Object> variableMap) {
		String shareplexMode = utilityFunctions.getShareplexMode();
		variableMap.put("shareplexMode", shareplexMode);
		String msgStep = PROCESS_NAME_CMFPROCESS_POLLER + "."+"getShareplexMode()";
		logger.info("Returning from service "+msgStep+" - ( shareplexMode :" + shareplexMode + ")");
		return variableMap;
	}

}
