package com.emc.settlement.workitemhandler.runrelated;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.emc.settlement.backend.runrelated.SetPackageAuthorization;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.service.task.runrelated.SetPackageAuthorizationTask;

//@Component
public class SetPackageAuthorizationWIH implements WorkItemHandler {


	/*@Autowired
	private SaveMSSLMeteringFile saveMSSLMeteringFile;*/

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		Map<String, Object> variableMap = new HashMap<String, Object>();

		String operationPath = (String) workItem.getParameter("Operation");
		String packageId = (String) workItem.getParameter("packageId");
		String runStatus = (String) workItem.getParameter("runStatus");
		String username = (String) workItem.getParameter("username");

		variableMap.put("packageId", packageId);
		variableMap.put("runStatus", runStatus);
		variableMap.put("username", username);

		variableMap = ContextInitializer.getInstance().invokeSetPackageAuthorization(variableMap, operationPath);

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

}
