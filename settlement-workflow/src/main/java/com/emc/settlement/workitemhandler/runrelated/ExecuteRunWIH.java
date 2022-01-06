package com.emc.settlement.workitemhandler.runrelated;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.emc.settlement.backend.runrelated.ExecuteRun;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.pojo.PeriodNumber;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import com.emc.settlement.model.backend.service.task.runrelated.ExecuteRunTask;

public class ExecuteRunWIH implements WorkItemHandler {

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		Map<String, Object> variableMap = new HashMap<String, Object>();

		String operationPath = (String) workItem.getParameter("Operation");
		variableMap.put("bizDay", (Boolean) workItem.getParameter("bizDay"));
		variableMap.put("firstPrelim", (Boolean) workItem.getParameter("firstPrelim"));
		variableMap.put("invokedBy", (Boolean) workItem.getParameter("invokedBy"));
		variableMap.put("isTestRun", (Boolean) workItem.getParameter("isTestRun"));
		variableMap.put("jamMsg", (String) workItem.getParameter("jamMsg"));
		variableMap.put("mcrString", (String) workItem.getParameter("mcrString"));
		variableMap.put("msgStep", (Boolean) workItem.getParameter("msgStep"));
		variableMap.put("nemsControllerEveId", (String) workItem.getParameter("nemsControllerEveId"));
		variableMap.put("nemsControllerSchEveId", (String) workItem.getParameter("nemsControllerSchEveId"));
		variableMap.put("pd", (PeriodNumber) workItem.getParameter("pd"));
		variableMap.put("settRunId", (String) workItem.getParameter("settRunId"));
		variableMap.put("settlementParam", (SettlementRunParams) workItem.getParameter("settlementParam"));
		variableMap.put("username",  (String) workItem.getParameter("username"));
		variableMap.put("logPrefix", (String) workItem.getParameter("logPrefix"));
		variableMap.put("settlementType", (String) workItem.getParameter("settlementType"));
		variableMap.put("soapServiceUrl", (String) workItem.getParameter("soapServiceUrl"));

		variableMap = ContextInitializer.getInstance().invokeExecuteRun(variableMap, operationPath);

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

}
