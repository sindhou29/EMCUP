package com.emc.settlement.backend.fileupload;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.exceptions.MsslException;
import com.emc.settlement.model.backend.pojo.UploadFileInfo;
import com.emc.settlement.model.backend.pojo.fileupload.MSSL;
import com.emc.settlement.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class VerifyAndLoadMSSLMeteringDataTest {

	@Autowired
	private UtilityFunctions  utilityFunctions;

	@Autowired
	private VerifyAndLoadMSSLMeteringData verifyAndLoadMSSLMeteringData;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private UploadFileInfo fileInfo = new UploadFileInfo();

	private String ebtEventId;

	private String ntEffectiveDate;

	private String drEffectiveDate;

	private String eventId;

	private String userId;

	private List<MSSL> cmfDataRejected;

	private List<String> emailNotifyList;

	private boolean fromEBT;

	private Object ebtEventId1;

	private Map<String, Object> variableMap;

	@Before
	public void setup() {

		fileInfo.setComments("Save MSSL Metering File Test");
		fileInfo.setFileType("CMF");
		fileInfo.setFilename("01234355.csv");
		fileInfo.setUploadUsername("rahul.oza");
		fileInfo.setUploadTime(new Date());

		eventId = utilityFunctions.createJAMEvent("EXE", "MSSL UPLOAD FILE");
		userId = utilityFunctions.getUserId(fileInfo.uploadUsername);
		ebtEventId = utilityFunctions.createEbtEvent(eventId, fileInfo.fileType,
				fileInfo.filename, userId,
				fileInfo.comments, fileInfo.transId);
		ntEffectiveDate = utilityFunctions.getddMMMyyyyhhmmss(utilityFunctions.getSysParamTime("NT_EFFECTIVE_DATE"));
		drEffectiveDate = utilityFunctions.getddMMMyyyyhhmmss(utilityFunctions.getSysParamTime("DR_EFFECTIVE_DATE"));
		cmfDataRejected = new ArrayList<>();
		emailNotifyList = null;
		fromEBT = false;
		String logPrefix = "[" + fileInfo.fileType + "]";

		variableMap = new HashMap<String, Object>();
		variableMap.put("cmfDataRejected",cmfDataRejected);
		variableMap.put("drEffectiveDate",drEffectiveDate);
		variableMap.put("ebtEventId",ebtEventId);
		variableMap.put("emailNotifyList",emailNotifyList);
		variableMap.put("eventId",eventId);
		variableMap.put("fileInfo",fileInfo);
		variableMap.put("fromEBT",fromEBT);
//		variableMap.put("msslData",msslData);
		variableMap.put("ntEffectiveDate",ntEffectiveDate);
		variableMap.put("userId",userId);
//		variableMap.put("msslException",msslException);
		variableMap.put("logPrefix", logPrefix);
	}

	@Test
	public void testSqlException() {
		SQLException sqlException = new SQLException();
//		verifyAndLoadMSSLMeteringData.sqlException(sqlException, fileInfo, eventId, ebtEventId);
	}

	@Test
	public void testUpdateProcessedStatus() {

		Map<String, Object> map = verifyAndLoadMSSLMeteringData.updateProcessedStatus(variableMap);
		assertNotNull(map.get("ntEffectiveDate"));
		assertNotNull(map.get("drEffectiveDate"));
		assertNotNull(map.get("fileInfo"));
	}

	@Test
	public void testValidateAndLoadCMFData() {
		try {
			verifyAndLoadMSSLMeteringData.validateAndLoadCMFData(variableMap);
		}
		catch (MsslException e) {
			fail("Exception thrown: "+e.getMessage());
		}
	}

	@Test
	public void testValidateAndLoadDailyMeteringData() {

		try {
			verifyAndLoadMSSLMeteringData.validateAndLoadDailyMeteringData(variableMap);
		}
		catch (MsslException e) {
			fail("Exception thrown: "+e.getMessage());
		}
	}

	@Test
	public void testUpdateEvent() {
		verifyAndLoadMSSLMeteringData.updateEvent(variableMap);
	}
}
