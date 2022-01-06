package com.emc.settlement.workitemhandler.runrelated;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.emc.drcap.nemsprerequisite.StringListList;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.exceptions.SettlementRunException;
import com.emc.settlement.model.backend.pojo.AlertNotification;
import com.emc.settlement.model.backend.pojo.PeriodNumber;
import com.emc.settlement.model.backend.pojo.SettRunPkg;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class SettlementRunProcessWIH implements WorkItemHandler {


	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		Map<String, Object> variableMap = new HashMap<String, Object>();

		String operationPath = (String) workItem.getParameter("Operation");
		variableMap.put("clwqExists", (Boolean) workItem.getParameter("clwqExists"));
		variableMap.put("firstPrelim", (Boolean) workItem.getParameter("firstPrelim"));
		variableMap.put("invokedBy", (Boolean) workItem.getParameter("invokedBy"));
		variableMap.put("invokedByModule", (String) workItem.getParameter("invokedByModule"));
		variableMap.put("isTestRun", (Boolean) workItem.getParameter("isTestRun"));
		variableMap.put("mcrChange", (Boolean) workItem.getParameter("mcrChange"));
		variableMap.put("mcrIds", (StringListList) workItem.getParameter("mcrIds"));
		variableMap.put("mcrString", (String) workItem.getParameter("mcrString"));
		variableMap.put("nemsControllerEveId", (String) workItem.getParameter("nemsControllerEveId"));
		variableMap.put("pd", (PeriodNumber) workItem.getParameter("pd"));
		variableMap.put("settRunId", (String) workItem.getParameter("settRunId"));
		variableMap.put("settRunPackage", (SettRunPkg) workItem.getParameter("settRunPackage"));
		variableMap.put("settlementParam", (SettlementRunParams) workItem.getParameter("settlementParam"));
		variableMap.put("srInfo", (String) workItem.getParameter("srInfo"));
		variableMap.put("username",  (String) workItem.getParameter("username"));
		variableMap.put("startTime", (Date) workItem.getParameter("startTime"));
		variableMap.put("endTime", (Date) workItem.getParameter("endTime"));
		variableMap.put("result", (String) workItem.getParameter("result"));
		variableMap.put("alertNotification", (AlertNotification) workItem.getParameter("alertNotification"));
		variableMap.put("exception", (SettlementRunException) workItem.getParameter("exception"));
		
		variableMap = ContextInitializer.getInstance().invokeSettlementRunProcess(variableMap, operationPath);
		
		manager.completeWorkItem(workItem.getId(), variableMap);
	}

}
