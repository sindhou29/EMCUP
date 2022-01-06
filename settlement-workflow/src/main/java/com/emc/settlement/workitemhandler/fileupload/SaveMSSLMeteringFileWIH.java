package com.emc.settlement.workitemhandler.fileupload;

import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.backend.fileupload.SaveMSSLMeteringFile;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.exceptions.MsslException;
import com.emc.settlement.model.backend.pojo.UploadFileInfo;
import com.emc.settlement.model.backend.service.task.fileupload.SaveMSSLMeteringFileTask;
import org.apache.commons.lang3.StringUtils;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

//@Component
public class SaveMSSLMeteringFileWIH implements WorkItemHandler {

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		Map<String, Object> variableMap = new HashMap<String, Object>();
		String operationPath = (String) workItem.getParameter("Operation");
		String ebtEventId = (String) workItem.getParameter("ebtEventId");
		String eventId = (String) workItem.getParameter("eventId");
		String fileContentName = (String) workItem.getParameter("fileContentName");
		UploadFileInfo fileInfo = (UploadFileInfo) workItem.getParameter("fileInfo");
		Boolean fromEBT = (Boolean) workItem.getParameter("fromEBT");
		String userId = (String) workItem.getParameter("userId");
		MsslException msslException = (MsslException) workItem.getParameter("msslException");
		String logPrefix = "["+fileInfo.fileType+"] ";

		variableMap.put("ebtEventId",ebtEventId);
		variableMap.put("eventId",eventId);
		variableMap.put("fileContentName",fileContentName);
		variableMap.put("fileInfo",fileInfo);
		variableMap.put("fromEBT",fromEBT);
		variableMap.put("userId",userId);
		variableMap.put("msslException",msslException);
		variableMap.put("logPrefix", logPrefix);

		variableMap = ContextInitializer.getInstance().invokeSaveMSSL(variableMap, operationPath);

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	private void validateRequiredParams(WorkItem workItem) {
		if (StringUtils.isEmpty((String) workItem.getParameter("OperationPath"))) {

			//throw new IllegalArgumentException("Operation is required ");
		}
		if (StringUtils.isEmpty((String) workItem.getParameter("OperationType"))) {
			//throw new IllegalArgumentException("Operation Type is required ");
		}
	}

}
