package com.emc.settlement.backend.fileupload;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.model.backend.exceptions.MsslException;
import com.emc.settlement.model.backend.pojo.UploadFileInfo;
import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

public class SaveMSSLMeteringFileTest extends BaseTest {

	@Autowired
	private SaveMSSLMeteringFile saveMSSLMeteringFile;

	private UploadFileInfo fileInfo = new UploadFileInfo();

	private String fileContentName;

	private String eventId;

	private String ebtEventId;

	private String userId;

	private Map<String, Object> variableMap;

	@Before
	public void setup() {

		fileInfo.setComments("Receive MSSL Metering Data Test");
		fileInfo.setFileType("CMF");
		fileInfo.setFilename("DMS01233163.csv");
		fileInfo.setUploadUsername("rahul.oza");
		fileInfo.setCompressed("Y");
		fileContentName = "\\\\192.168.0.3\\Sharing\\EMC\\SHaRP\\BPMSampleFiles\\Metering-mtr_20180419_060553\\01234355_trunc.csv";
		eventId = utilityFunctions.createJAMEvent( "EXE",  "MSSL UPLOAD FILE");
		userId = utilityFunctions.getUserId( fileInfo.uploadUsername);
		ebtEventId = utilityFunctions.createEbtEvent( eventId,  fileInfo.fileType,
				fileInfo.filename,  userId,
				fileInfo.comments,  fileInfo.transId);

		variableMap = new HashMap<String, Object>();
		String logPrefix = "["+fileInfo.fileType+"] ";

		variableMap.put("ebtEventId",ebtEventId);
		variableMap.put("eventId",eventId);
		variableMap.put("fileContentName",fileContentName);
		variableMap.put("fileInfo",fileInfo);
//		variableMap.put("fromEBT",fromEBT);
		variableMap.put("userId",userId);
//		variableMap.put("msslException",msslException);
		variableMap.put("logPrefix", logPrefix);
	}

	@Test
	public void testcaptureMetaData() {

		try {
			Map<String, Object> map = saveMSSLMeteringFile.captureMetaData(variableMap);
			assertNotNull(map.get("eventId"));
			assertNotNull(map.get("userId"));
			assertNotNull(map.get("ebtEventId"));
		}
		catch (MsslException e) {
			fail("Exception thrown: "+e.getMessage());
		}
	}

	@Test
	public void testSendACKToMSSL() {

		saveMSSLMeteringFile.sendACKToMSSL(variableMap);
	}

	@Test
	public void testStoreMeteringFile() {
		saveMSSLMeteringFile.storeMeteringFile(variableMap);
	}


}
