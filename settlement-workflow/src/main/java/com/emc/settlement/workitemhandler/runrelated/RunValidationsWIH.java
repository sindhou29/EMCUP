package com.emc.settlement.workitemhandler.runrelated;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.emc.settlement.backend.runrelated.RunValidations;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.exceptions.SettlementRunException;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import com.emc.settlement.model.backend.service.task.runrelated.RunValidationsTask;

//@Component
public class RunValidationsWIH implements WorkItemHandler {


	/*@Autowired
	private SaveMSSLMeteringFile saveMSSLMeteringFile;*/

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		Map<String, Object> variableMap = new HashMap<String, Object>();

		String operationPath = (String) workItem.getParameter("Operation");
		Boolean isTestRun = (Boolean) workItem.getParameter("isTestRun");
		SettlementRunParams settlementParam = (SettlementRunParams) workItem.getParameter("settlementParam");
		String errorMsg = (String) workItem.getParameter("errorMsg");
		SettlementRunException exception = (SettlementRunException) workItem.getParameter("exception");

		variableMap.put("isTestRun", isTestRun);
		variableMap.put("settlementParam", settlementParam);
		variableMap.put("errorMsg", errorMsg);
		variableMap.put("exception", exception);

		variableMap = ContextInitializer.getInstance().invokeRunValidations(variableMap, operationPath);

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

}
