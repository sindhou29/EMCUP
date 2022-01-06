package com.emc.settlement.workitemhandler.fileupload;

import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.backend.fileupload.CMFProcessPoller;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.service.task.fileupload.CMFProcessPollerTask;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class CMFProcessPollerWIH implements WorkItemHandler {
	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		Map<String, Object> variableMap = new HashMap<String, Object>();
		String operationPath = (String) workItem.getParameter("Operation");
		String ebtEventId = (String) workItem.getParameter("ebtEventId");
		String eventId = (String) workItem.getParameter("eventId");
		String filename = (String) workItem.getParameter("filename");
		Integer instanceCnt = (Integer) workItem.getParameter("instanceCnt");
		Boolean pendingCMF = (Boolean) workItem.getParameter("pendingCMF");
		String shareplexMode = (String) workItem.getParameter("shareplexMode");

		variableMap.put("ebtEventId",ebtEventId);
		variableMap.put("eventId",eventId);
		variableMap.put("filename",filename);
		variableMap.put("instanceCnt",instanceCnt);
		variableMap.put("pendingCMF",pendingCMF);
		variableMap.put("shareplexMode", shareplexMode);

		variableMap = ContextInitializer.getInstance().invokeCMFProcessPoller(variableMap, operationPath);


		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
