package com.emc.settlement.workitemhandler.runrelated;

import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.exceptions.SettlementRunException;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class IPDataVerificationsValidationsWIH implements WorkItemHandler {

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		Map<String, Object> variableMap = new HashMap<String, Object>();

		String operationPath = (String) workItem.getParameter("Operation");
		SettlementRunParams settlementParam = (SettlementRunParams) workItem.getParameter("settlementParam");
		String severity = (String) workItem.getParameter("severity");
		String execStep = (String) workItem.getParameter("execStep");
		String processName = (String) workItem.getParameter("processName");
		SettlementRunException exception = (SettlementRunException) workItem.getParameter("exception");

		variableMap.put("eventId", settlementParam.getMainEveId());
		variableMap.put("severity", severity);
		variableMap.put("execStep", execStep);
		variableMap.put("text", "Exception while processing "+processName+". Run ID - "+settlementParam.getRunId());
		variableMap.put("exception", exception);

		ContextInitializer.getInstance().invokeIPDataVerificationsValidations(variableMap, operationPath);

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
