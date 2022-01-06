package com.emc.settlement.workitemhandler.fileupload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.emc.settlement.backend.fileupload.SaveMSSLMeteringFile;
import com.emc.settlement.backend.fileupload.VerifyAndLoadMSSLMeteringData;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.exceptions.MsslException;
import com.emc.settlement.model.backend.pojo.UploadFileInfo;
import com.emc.settlement.model.backend.pojo.fileupload.MSSL;
import com.emc.settlement.model.backend.service.task.fileupload.VerifyAndLoadMSSLMeteringDataTask;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class VerifyAndLoadMSSLMeteringDataWIH implements WorkItemHandler {
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		Map<String, Object> variableMap = new HashMap<String, Object>();
		String operationPath = (String) workItem.getParameter("Operation");
		ArrayList<MSSL> cmfDataRejected = (ArrayList<MSSL>) workItem.getParameter("cmfDataRejected");
		UploadFileInfo fileInfo = (UploadFileInfo) workItem.getParameter("fileInfo");
		String userId = (String) workItem.getParameter("userId");
		String ntEffectiveDate = (String) workItem.getParameter("ntEffectiveDate");
		String drEffectiveDate = (String) workItem.getParameter("drEffectiveDate");
		List<MSSL> msslData = (List<MSSL>) workItem.getParameter("msslData");
		String ebtEventId = (String) workItem.getParameter("ebtEventId");
		List<String> emailNotifyList = (List<String>) workItem.getParameter("emailNotifyList");
		String eventId = (String) workItem.getParameter("eventId");
		Boolean fromEBT = (Boolean) workItem.getParameter("fromEBT");
		String logPrefix = "["+fileInfo.fileType+"] ";
		MsslException msslException = (MsslException) workItem.getParameter("msslException");

		variableMap.put("cmfDataRejected",cmfDataRejected);
		variableMap.put("drEffectiveDate",drEffectiveDate);
		variableMap.put("ebtEventId",ebtEventId);
		variableMap.put("emailNotifyList",emailNotifyList);
		variableMap.put("eventId",eventId);
		variableMap.put("fileInfo",fileInfo);
		variableMap.put("fromEBT",fromEBT);
		variableMap.put("msslData",msslData);
		variableMap.put("ntEffectiveDate",ntEffectiveDate);
		variableMap.put("userId",userId);
		variableMap.put("msslException",msslException);
		variableMap.put("logPrefix", logPrefix);


		variableMap = ContextInitializer.getInstance().invokeVerifyMSSL(variableMap, operationPath);
		/*VerifyAndLoadMSSLMeteringData verifyAndLoadMSSLMeteringData = ContextInitializer.getInstance().getContext().getBean(VerifyAndLoadMSSLMeteringData.class);
//		VerifyAndLoadMSSLMeteringData verifyAndLoadMSSLMeteringData = new VerifyAndLoadMSSLMeteringData();
		if(operationPath.equalsIgnoreCase(VerifyAndLoadMSSLMeteringDataTask.UPDATEPROCESSEDSTATUS.getValue())) {
			variableMap = verifyAndLoadMSSLMeteringData.updateProcessedStatus(variableMap);
		} else if(operationPath.equalsIgnoreCase(VerifyAndLoadMSSLMeteringDataTask.VALIDATEANDLOADDAILYMETERINGDATA.getValue())) {
			variableMap = verifyAndLoadMSSLMeteringData.validateAndLoadDailyMeteringData(variableMap);
		} else if(operationPath.equalsIgnoreCase(VerifyAndLoadMSSLMeteringDataTask.VALIDATEANDLOADCMFDATA.getValue())) {
			variableMap = verifyAndLoadMSSLMeteringData.validateAndLoadCMFData(variableMap);
		} else if(operationPath.equalsIgnoreCase(VerifyAndLoadMSSLMeteringDataTask.UPDATEEVENT.getValue())) {
			variableMap = verifyAndLoadMSSLMeteringData.updateEvent(variableMap);
		} else if(operationPath.equalsIgnoreCase(VerifyAndLoadMSSLMeteringDataTask.UPDATEFAILEVENT.getValue())) {
			variableMap = verifyAndLoadMSSLMeteringData.updateFailEvent(variableMap);
		} else if(operationPath.equalsIgnoreCase(VerifyAndLoadMSSLMeteringDataTask.ALERTNOTIFY.getValue())) {
			variableMap = verifyAndLoadMSSLMeteringData.alertNotify(variableMap);
		}*/

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

}
