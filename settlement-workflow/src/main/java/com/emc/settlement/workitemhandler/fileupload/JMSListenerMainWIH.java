package com.emc.settlement.workitemhandler.fileupload;

import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.backend.fileupload.JMSListenerMain;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.pojo.Message;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class JMSListenerMainWIH implements WorkItemHandler {
	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		Map<String, Object> variableMap = new HashMap<String, Object>();
		String operationPath = (String) workItem.getParameter("Operation");
		Message message = (Message) workItem.getParameter("message");

		variableMap.put("message", message);

		variableMap = ContextInitializer.getInstance().invokeJMSListenerMain(variableMap, operationPath);

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
