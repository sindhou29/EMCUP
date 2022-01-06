package com.emc.settlement.workitemhandler.runrelated;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.config.ContextInitializer;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class PerformTestRunWIH implements WorkItemHandler {
	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		Map<String, Object> variableMap = new HashMap<String, Object>();
		String operationPath = (String) workItem.getParameter("Operation");

		variableMap = ContextInitializer.getInstance().invokePerformTestRun(variableMap, operationPath);

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {

		manager.abortWorkItem(workItem.getId());
	}
}
