package com.emc.settlement.workitemhandler.runrelated;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.emc.settlement.backend.runrelated.RerunInclusions;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.exceptions.SettlementRunException;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import com.emc.settlement.model.backend.service.task.runrelated.RerunInclusionsTask;

//@Component
public class RerunInclusionsWIH implements WorkItemHandler {


	/*@Autowired
	private SaveMSSLMeteringFile saveMSSLMeteringFile;*/

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		Map<String, Object> variableMap = new HashMap<String, Object>();

		String operationPath = (String) workItem.getParameter("Operation");
		Boolean firstPrelim = (Boolean) workItem.getParameter("firstPrelim");
		String lastRunId = (String) workItem.getParameter("lastRunId");
		SettlementRunParams settlementParam = (SettlementRunParams) workItem.getParameter("settlementParam");
		String TEST_RERUN_INCLUDE = (String) workItem.getParameter("TEST_RERUN_INCLUDE");
		SettlementRunException exception = (SettlementRunException) workItem.getParameter("exception");
		
		variableMap.put("firstPrelim", firstPrelim);
		variableMap.put("lastRunId", lastRunId);
		variableMap.put("settlementParam", settlementParam);
		variableMap.put("TEST_RERUN_INCLUDE", TEST_RERUN_INCLUDE);
		variableMap.put("exception", exception);
		
		variableMap = ContextInitializer.getInstance().invokeRerunInclusions(variableMap, operationPath);

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

}
