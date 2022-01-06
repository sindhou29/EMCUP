package com.emc.settlement.workitemhandler.runrelated;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.emc.settlement.backend.runrelated.FinalAuthoriseSettlementRun;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.exceptions.AuthorisationException;
import com.emc.settlement.model.backend.pojo.SettlementRunInfo;
import com.emc.settlement.model.backend.service.task.runrelated.FinalAuthoriseSettlementRunTask;

public class FinalAuthoriseSettlementRunWIH implements WorkItemHandler {


	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		Map<String, Object> variableMap = new HashMap<String, Object>();

		String operationPath = (String) workItem.getParameter("Operation");
		String authorisation = (String) workItem.getParameter("authorisation");
		Boolean drEffective = (Boolean) workItem.getParameter("drEffective");
		Date drEffectiveDate = (Date) workItem.getParameter("drEffectiveDate");
		String packageId = (String) workItem.getParameter("packageId");
		SettlementRunInfo runInfo = (SettlementRunInfo) workItem.getParameter("runInfo");
		String username = (String) workItem.getParameter("username");
		AuthorisationException exception = (AuthorisationException) workItem.getParameter("exception");
		
		variableMap.put("authorisation", authorisation);
		variableMap.put("drEffective", drEffective);
		variableMap.put("drEffectiveDate", drEffectiveDate);
		variableMap.put("packageId", packageId);
		variableMap.put("username", username);
		variableMap.put("runInfo", runInfo);
		variableMap.put("exception", exception);
		

		variableMap = ContextInitializer.getInstance().invokeFinalAuthoriseSettlementRun(variableMap, operationPath);
		
		manager.completeWorkItem(workItem.getId(), variableMap);
	}

}
