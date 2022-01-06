package com.emc.settlement.workitemhandler.runrelated;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.emc.settlement.backend.runrelated.CaptureFSCPenaltyQuantity;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.exceptions.SettlementRunException;
import com.emc.settlement.model.backend.service.task.runrelated.CaptureFSCPenaltyQuantityTask;

public class CaptureFSCPenaltyQuantityWIH implements WorkItemHandler {

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		Map<String, Object> variableMap = new HashMap<String, Object>();

		String operationPath = (String) workItem.getParameter("Operation");
		String eveId = (String) workItem.getParameter("eveId");
		Boolean isFSCEffective = (Boolean) workItem.getParameter("isFSCEffective");
		Date settlementDate = (Date) workItem.getParameter("settlementDate");
		String settlementRunId = (String) workItem.getParameter("settlementRunId");
		String settlementType = (String) workItem.getParameter("settlementType");
		Boolean validSettRun = (Boolean) workItem.getParameter("validSettRun");
		SettlementRunException exception = (SettlementRunException) workItem.getParameter("exception");

		variableMap.put("eveId", eveId);
		variableMap.put("isFSCEffective", isFSCEffective);
		variableMap.put("settlementDate", settlementDate);
		variableMap.put("settlementRunId", settlementRunId);
		variableMap.put("settlementType", settlementType);
		variableMap.put("validSettRun", validSettRun);
		variableMap.put("exception", exception);

		variableMap = ContextInitializer.getInstance().invokeCaptureFSCPenaltyQuantity(variableMap, operationPath);

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

}
