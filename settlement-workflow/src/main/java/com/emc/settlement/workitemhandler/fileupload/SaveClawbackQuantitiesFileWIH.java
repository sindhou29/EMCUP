package com.emc.settlement.workitemhandler.fileupload;

import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.backend.fileupload.SaveClawbackQuantitiesFile;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.exceptions.ClawbackQuantitiesException;
import com.emc.settlement.model.backend.pojo.UploadFileInfo;
import com.emc.settlement.model.backend.service.task.fileupload.SaveClawbackQuantitiesFileTask;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class SaveClawbackQuantitiesFileWIH implements WorkItemHandler {
	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		String operationPath = (String) workItem.getParameter("Operation");
		String ebtEventId = (String) workItem.getParameter("ebtEventId");
		String eventId = (String) workItem.getParameter("eventId");
		String fileContentName = (String) workItem.getParameter("fileContentName");
		UploadFileInfo fileInfo = (UploadFileInfo) workItem.getParameter("fileInfo");
		Boolean fromSEW = (Boolean) workItem.getParameter("fromSEW");
		String sewOperationType = (String) workItem.getParameter("sewOperationType");
		String sewUploadEventsID = (String) workItem.getParameter("sewUploadEventsID");
		String userId = (String) workItem.getParameter("userId");
		String comments = (String) workItem.getParameter("comments");
		String filename = (String) workItem.getParameter("filename");
		String uploadUser = (String) workItem.getParameter("uploadUser");
		String fileType = (String) workItem.getParameter("fileType");
		String uploadMethod = (String) workItem.getParameter("uploadMethod");
		ClawbackQuantitiesException clawbackEx = (ClawbackQuantitiesException) workItem.getParameter("clawbackEx");
		String logPrefix = "[" + fileInfo.fileType + "] ";


		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("ebtEventId",ebtEventId);
		variableMap.put("eventId",eventId);
		variableMap.put("fileContentName",fileContentName);
		variableMap.put("fileInfo",fileInfo);
		variableMap.put("fromSEW",fromSEW);
		variableMap.put("sewOperationType",sewOperationType);
		variableMap.put("sewUploadEventsID",sewUploadEventsID);
		variableMap.put("userId",userId);
		variableMap.put("comments",comments);
		variableMap.put("filename",filename);
		variableMap.put("uploadUser",uploadUser);
		variableMap.put("fileType",fileType);
		variableMap.put("uploadMethod",uploadMethod);
		variableMap.put("clawbackEx",clawbackEx);
		variableMap.put("logPrefix", logPrefix);


		variableMap = ContextInitializer.getInstance().invokeSaveClawbackQuantitiesFile(variableMap, operationPath);

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
