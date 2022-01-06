package com.emc.settlement.workitemhandler.fileupload;

import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.backend.fileupload.BilateralContractUploadMain;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.exceptions.BilateralContractUploadException;
import com.emc.settlement.model.backend.pojo.CsvFileValidator;
import com.emc.settlement.model.backend.pojo.UploadFileInfo;
import com.emc.settlement.model.backend.service.task.fileupload.BilateralContractUploadMainTask;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class BilateralContractUploadMainWIH implements WorkItemHandler {
	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		Map<String, Object> variableMap = new HashMap<String, Object>();
		String operationPath = (String) workItem.getParameter("Operation");
		String comments = (String) workItem.getParameter("comments");
		CsvFileValidator csvFileValidator = (CsvFileValidator) workItem.getParameter("csvFileValidator");
		String ebtEventsRowId = (String) workItem.getParameter("ebtEventsRowId");
		String errorMsg = (String) workItem.getParameter("errorMsg");
		String eveId = (String) workItem.getParameter("eveId");
		String eventType = (String) workItem.getParameter("eventType");
		String fileContentName = (String) workItem.getParameter("fileContentName");
		String fileContent = (String) workItem.getParameter("fileContent");
		UploadFileInfo fileInfo = (UploadFileInfo) workItem.getParameter("fileInfo");
		String filename = (String) workItem.getParameter("filename");
		Boolean fromSEW = (Boolean) workItem.getParameter("fromSEW");
		String inputPTPID = (String) workItem.getParameter("inputPTPID");
		String sewUploadEventsId = (String) workItem.getParameter("sewUploadEventsId");
		String uploadMethod = (String) workItem.getParameter("uploadMethod");
		String uploadUser = (String) workItem.getParameter("uploadUser");
		String userId = (String) workItem.getParameter("userId");
		BilateralContractUploadException bilateralContractUploadException = (BilateralContractUploadException) workItem.getParameter("bilateralContractUploadException");


		variableMap.put("comments",comments);
		variableMap.put("csvFileValidator",csvFileValidator);
		variableMap.put("ebtEventsRowId",ebtEventsRowId);
		variableMap.put("errorMsg",errorMsg);
		variableMap.put("eveId",eveId);
		variableMap.put("eventType",eventType);
		variableMap.put("fileContentName",fileContentName);
		variableMap.put("fileContent",fileContent);
		variableMap.put("fileInfo",fileInfo);
		variableMap.put("filename",filename);
		variableMap.put("fromSEW",fromSEW);
		variableMap.put("inputPTPID",inputPTPID);
		variableMap.put("sewUploadEventsId",sewUploadEventsId);
		variableMap.put("uploadMethod",uploadMethod);
		variableMap.put("uploadUser",uploadUser);
		variableMap.put("userId",userId);
		variableMap.put("bilateralContractUploadException",bilateralContractUploadException);

		variableMap = ContextInitializer.getInstance().invokeBilateralContractUploadMain(variableMap, operationPath);

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
