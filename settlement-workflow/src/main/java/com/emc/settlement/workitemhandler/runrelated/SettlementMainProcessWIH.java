package com.emc.settlement.workitemhandler.runrelated;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.exceptions.SettlementRunException;
import com.emc.settlement.model.backend.pojo.PeriodNumber;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class SettlementMainProcessWIH implements WorkItemHandler {


	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		Map<String, Object> variableMap = new HashMap<String, Object>();

		String operationPath = (String) workItem.getParameter("Operation");
		variableMap.put("backUpMode", (String) workItem.getParameter("backUpMode"));
		variableMap.put("bizDay", (Boolean) workItem.getParameter("bizDay"));
		variableMap.put("comments", (String) workItem.getParameter("comments"));
		variableMap.put("endTime", (Date) workItem.getParameter("endTime"));
		variableMap.put("ignoreScheduledRunCheck", (Boolean) workItem.getParameter("ignoreScheduledRunCheck"));
		variableMap.put("invokedBy", (Boolean) workItem.getParameter("invokedBy"));
		variableMap.put("isFSCEffective", (Boolean) workItem.getParameter("isFSCEffective"));
		variableMap.put("mainEventId", (String) workItem.getParameter("mainEventId"));
		variableMap.put("mcrString", (String) workItem.getParameter("mcrString"));
		variableMap.put("msgStep", (String) workItem.getParameter("msgStep"));
		variableMap.put("nemsControllerEveId", (String) workItem.getParameter("nemsControllerEveId"));
		variableMap.put("nemsControllerSchEveId", (String) workItem.getParameter("nemsControllerSchEveId"));
		variableMap.put("pd", (PeriodNumber) workItem.getParameter("pd"));
		variableMap.put("runDate", (Date) workItem.getParameter("runDate"));
		variableMap.put("runFrom",  (String) workItem.getParameter("runFrom"));
		variableMap.put("schEventId", (String) workItem.getParameter("schEventId"));
		variableMap.put("settRunId", (String) workItem.getParameter("settRunId"));
		variableMap.put("settlementDate", (Date) workItem.getParameter("settlementDate"));
		variableMap.put("settlementParam", (SettlementRunParams) workItem.getParameter("settlementParam"));
		variableMap.put("settlementType",  (String) workItem.getParameter("settlementType"));
		variableMap.put("startMsg", (String) workItem.getParameter("startMsg"));
		variableMap.put("startTime", (Date) workItem.getParameter("startTime"));
		variableMap.put("testRun", (Boolean) workItem.getParameter("testRun"));
		variableMap.put("username",  (String) workItem.getParameter("username"));
		variableMap.put("logPrefix", (String) workItem.getParameter("logPrefix"));
		variableMap.put("wsParam", (Object) workItem.getParameter("wsParam"));
		variableMap.put("strRunDate", (String) workItem.getParameter("strRunDate"));
		variableMap.put("strSettlementDate",  (String) workItem.getParameter("strSettlementDate"));
		variableMap.put("soapServiceUrl", (String) workItem.getParameter("soapServiceUrl"));
		variableMap.put("exception", (SettlementRunException) workItem.getParameter("exception"));
		variableMap.put("isSchedule", (Boolean) workItem.getParameter("isSchedule"));
		
		variableMap = ContextInitializer.getInstance().invokeSettlementMainProcess(variableMap, operationPath);
		
		manager.completeWorkItem(workItem.getId(), variableMap);
	}

}
