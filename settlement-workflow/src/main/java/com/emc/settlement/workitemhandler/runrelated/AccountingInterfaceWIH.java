package com.emc.settlement.workitemhandler.runrelated;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.emc.settlement.backend.runrelated.AccountInterfaceMain;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.service.task.runrelated.AccountInterfaceMainTask;

//@Component
public class AccountingInterfaceWIH implements WorkItemHandler {


	/*@Autowired
	private SaveMSSLMeteringFile saveMSSLMeteringFile;*/

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		Map<String, Object> variableMap = new HashMap<String, Object>();

		String operationPath = (String) workItem.getParameter("Operation");
		String eveId = (String) workItem.getParameter("eveId");
		String runFrom = (String) workItem.getParameter("runFrom");
		Date settlementDate = (Date) workItem.getParameter("settlementDate");

		variableMap.put("eveId", eveId);
		variableMap.put("runFrom", runFrom);
		variableMap.put("settlementDate", settlementDate);

		variableMap = ContextInitializer.getInstance().invokeAccountingInterface(variableMap, operationPath);

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

}
