package com.emc.settlement.workitemhandler.runrelated;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.emc.settlement.backend.runrelated.LoadPendingCMFFiles;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.exceptions.SettlementRunException;
import com.emc.settlement.model.backend.pojo.SettlementRunInfo;
import com.emc.settlement.model.backend.service.task.runrelated.LoadPendingCMFFilesTask;

//@Component
public class LoadPendingCMFFilesWIH implements WorkItemHandler {


	/*@Autowired
	private SaveMSSLMeteringFile saveMSSLMeteringFile;*/

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		Map<String, Object> variableMap = new HashMap<String, Object>();

		String operationPath = (String) workItem.getParameter("Operation");
		SettlementRunInfo runInfo = (SettlementRunInfo) workItem.getParameter("runInfo");
		String runEveId = (String) workItem.getParameter("runEveId");
		String runType = (String) workItem.getParameter("runType");
		Date settlementDate = (Date) workItem.getParameter("settlementDate");
		SettlementRunException exception = (SettlementRunException) workItem.getParameter("exception");

		variableMap.put("runInfo", runInfo);
		variableMap.put("runEveId", runEveId);
		variableMap.put("runType", runType);
		variableMap.put("settlementDate", settlementDate);
		variableMap.put("exception", exception);

		variableMap = ContextInitializer.getInstance().invokeLoadPendingCMFFiles(variableMap, operationPath);

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

}
