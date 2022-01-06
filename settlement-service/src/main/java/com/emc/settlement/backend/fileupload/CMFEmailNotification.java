/**
 *
 */
package com.emc.settlement.backend.fileupload;

import static com.emc.settlement.model.backend.constants.BusinessParameters.PROCESS_NAME_CMF_EMAIL_NOTIFICATION;

import java.math.BigDecimal;
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
public class CMFEmailNotification {

	/**
	 *
	 */
	public CMFEmailNotification() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */
	private static final Logger logger = Logger.getLogger(CMFEmailNotification.class);

	@Autowired
	private UtilityFunctions utilityFunctions;

	@Autowired
	private AlertNotificationImpl notificationImpl;

    @Autowired
	private JdbcTemplate jdbcTemplate;

    @Transactional
	public void checkEmailStatus(Map<String, Object> variableMap) {

		List<String> emailList = (List<String>) variableMap.get("emailList");
		String uploadUserId = (String) variableMap.get("uploadUserId");
		String msgStep = BusinessParameters.PROCESS_NAME_CMF_EMAIL_NOTIFICATION + ".checkEmailStatus()" ;

		logger.log(Priority.INFO, " Starting Activity - " + PROCESS_NAME_CMF_EMAIL_NOTIFICATION + ".checkEmalStatus()");
		logger.log(Priority.INFO, " Input Params - emailList :" + emailList + " uploadUserId :" + uploadUserId);
		try{
			for (String emailNotificationId : emailList) {
				String enId = null;
				String addrCC = null;
				String subject = null;
				String body = null;
				String sacId = null;
				String addrTo = null;
				String version = null;
				String sqlCommand = "SELECT en.ID, en.ADDR_CC, en.BODY, en.SUBJECT, "
						+ "ens.MP_EMAIL_NOTIFICATIONS_ID, ens.NOTIFICATION_STATUS_ID, ens.CREATED_TIME, en.sac_id "
						+ "FROM NEM.NEM_MP_EMAIL_NOTIFICATIONS en, NEM.NEM_MP_EMAIL_STATUS ens "
						+ "WHERE en.ID = ens.MP_EMAIL_NOTIFICATIONS_ID " + "AND en.ID = ?";

				logger.log(Priority.INFO, sqlCommand);
				logger.log(Priority.INFO, "emailNotificationId :"+emailNotificationId);

				Object[] params = new Object[1];
				params[0] =  emailNotificationId;
				List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, params);
				for (Map row : list) {
					enId = (String)row.get("ID");
					addrCC = (String)row.get("ADDR_CC");
					subject = (String)row.get("SUBJECT");
					body = (String)row.get("BODY");
					sacId = (String)row.get("sac_id");
				}
				
				sqlCommand = "SELECT s1.id, s1.EMAIL_ADDR, s2.version " + "FROM NEM.NEM_SETTLEMENT_ACCOUNTS s1, "
						+ "(SELECT id, MAX(TO_NUMBER(version)) as version FROM NEM.NEM_SETTLEMENT_ACCOUNTS GROUP BY id) s2 "
						+ "WHERE s1.id = s2.id\tand s1.version = s2.version " + "and s1.id = ?";

				logger.log(Priority.INFO, sqlCommand);
				logger.log(Priority.INFO, "sacId :"+sacId);
				Object[] params1 = new Object[1];
				params1[0] =  sacId;
				List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlCommand, params1);
				for (Map row : list1) {
					addrTo = (String)row.get("EMAIL_ADDR");
					version = ((BigDecimal) row.get("VERSION")).toString();
				}

				AlertNotification alert = new AlertNotification();
				alert.recipients = addrTo;
				alert.cc = addrCC;
				alert.content = body;
				alert.sender = "emcsettlement@emc.com.sg";
				alert.subject = subject;
				alert.noticeType = "MSSL Metering Data Input Preparation";
				notificationImpl.sendEmail(alert);

				sqlCommand = "UPDATE NEM.NEM_MP_EMAIL_NOTIFICATIONS SET SAC_VERSION = '" + version + "'"
						+ " WHERE ID = '" + enId + "'";

				jdbcTemplate.update(sqlCommand, new Object[] {});

				logger.log(Priority.INFO, sqlCommand);

				String notificationStausID = null;
				sqlCommand = "SELECT ID FROM NEM.NEM_NOTIFICATION_STATUS WHERE STATUS = 'ISSUED'";

				logger.log(Priority.INFO, sqlCommand);
				List<Map<String, Object>> list2 = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
				for (Map row : list2) {
					notificationStausID = (String)row.get("ID");
				}
				sqlCommand = "INSERT INTO NEM.NEM_MP_EMAIL_STATUS(ID, MP_EMAIL_NOTIFICATIONS_ID, NOTIFICATION_STATUS_ID, CREATED_TIME, CREATED_BY)"
						+ " VALUES (?,?,?,SYSDATE,?)";

				Object[] params2 = new Object[4];
				params2[0] =  utilityFunctions.getEveId();
				params2[1] =  enId;
				params2[2] =  notificationStausID;
				params2[3] =  uploadUserId;
				jdbcTemplate.update(sqlCommand, params2);
				logger.log(Priority.INFO, sqlCommand);
				logger.log(Priority.INFO, "enId :"+enId+"  notificationStausID :"+notificationStausID+"  uploadUserId :"+uploadUserId);
				logger.log(Priority.INFO, "Returning from service "+msgStep);
			}

		} catch (Exception e) {

			logger.error("Exception : "+ e.getMessage());
		}
	}

}
