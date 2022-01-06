package com.emc.settlement.workitemhandler.fileupload;

import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.backend.fileupload.VerifyAndLoadClawbackQuantitiesData;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.exceptions.ClawbackQuantitiesException;
import com.emc.settlement.model.backend.pojo.UploadFileInfo;
import com.emc.settlement.model.backend.service.task.fileupload.VerifyAndLoadClawbackQuantitiesDataTask;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class VerifyAndLoadClawbackQuantitiesDataWIH implements WorkItemHandler {
	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		String operation = (String) workItem.getParameter("Operation");
		Boolean clwqEmpty = (Boolean) workItem.getParameter("clwqEmpty");
		String ebtEventId = (String) workItem.getParameter("ebtEventId");
//		emailNotifyList = () workItem.getParameter("emailNotifyList");
		String eventId = (String) workItem.getParameter("eventId");
		UploadFileInfo fileInfo = (UploadFileInfo) workItem.getParameter("fileInfo");
		Boolean fromSEW = (Boolean) workItem.getParameter("fromSEW");
//		String ntEffectiveDate = (String) workItem.getParameter("ntEffectiveDate");
		String sewOperationType = (String) workItem.getParameter("sewOperationType");
		String sewUploadEventsID = (String) workItem.getParameter("sewUploadEventsID");
		String userId = (String) workItem.getParameter("userId");
		ClawbackQuantitiesException clawbackException = (ClawbackQuantitiesException) workItem.getParameter("clawbackException");
		String logPrefix = "[" + fileInfo.fileType + "] ";

		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("clwqEmpty",clwqEmpty);
		variableMap.put("ebtEventId",ebtEventId);
//		variableMap.put("emailNotifyList",emailNotifyList);
		variableMap.put("eventId",eventId);
		variableMap.put("fileInfo",fileInfo);
		variableMap.put("fromSEW",fromSEW);
//		variableMap.put("ntEffectiveDate",ntEffectiveDate);
		variableMap.put("sewOperationType",sewOperationType);
		variableMap.put("sewUploadEventsID",sewUploadEventsID);
		variableMap.put("userId",userId);
		variableMap.put("clawbackException",clawbackException);
		variableMap.put("logPrefix",logPrefix);


		variableMap = ContextInitializer.getInstance().invokeVerifyAndLoadClawbackQuantitiesData(variableMap, operation);


		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
