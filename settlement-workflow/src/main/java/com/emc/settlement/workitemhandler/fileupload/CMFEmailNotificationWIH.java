package com.emc.settlement.workitemhandler.fileupload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.emc.settlement.backend.fileupload.CMFEmailNotification;
import com.emc.settlement.config.ContextInitializer;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class CMFEmailNotificationWIH implements WorkItemHandler {
	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		Map<String, Object> variableMap = new HashMap<String, Object>();
		String operationPath = (String) workItem.getParameter("Operation");
		List<String> emailList = (List<String>) workItem.getParameter("emailList");
		String uploadUserId = (String) workItem.getParameter("uploadUserId");

		variableMap.put("emailList",emailList);
		variableMap.put("uploadUserId",uploadUserId);

		variableMap = ContextInitializer.getInstance().invokeCMFEmailNotification(variableMap, operationPath);

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
