package com.emc.settlement.workitemhandler.fileupload;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.emc.settlement.backend.fileupload.EMCPSOBudgetUpload;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.exceptions.BudgetException;
import com.emc.settlement.model.backend.service.task.fileupload.EMCPSOBudgetUploadTask;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class EMCPSOBudgetUploadWIH implements WorkItemHandler {
	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		Map<String, Object> variableMap = new HashMap<String, Object>();
		String operationPath = (String) workItem.getParameter("Operation");
		List<Double> amount = (List<Double>) workItem.getParameter("amount");
		List<Double> cellAmount = (List<Double>) workItem.getParameter("cellAmount");
		List<Double> cellSerialNo = (List<Double>) workItem.getParameter("cellSerialNo");
		String comments = (String) workItem.getParameter("comments");
		String createdDateStr = (String) workItem.getParameter("createdDateStr");
		List<Date> cellMonth = (List<Date>) workItem.getParameter("cellMonth");
		String ebtEventId = (String) workItem.getParameter("ebtEventId");
		String ebtEventsRowId = (String) workItem.getParameter("ebtEventsRowId");
		String eveId = (String) workItem.getParameter("eveId");
		String fileContent = (String) workItem.getParameter("fileContent");
		String fileContentName = (String) workItem.getParameter("fileContentName");
		String filename = (String) workItem.getParameter("filename");
		Boolean foundEMC = (Boolean) workItem.getParameter("foundEMC");
		Boolean foundPSO = (Boolean) workItem.getParameter("foundPSO");
		Boolean insertedDateRange = (Boolean) workItem.getParameter("insertedDateRange");
		List<Date> month = (List<Date>) workItem.getParameter("month");
		Integer nextVersionNum = (Integer) workItem.getParameter("nextVersionNum");
		String uploadUser = (String) workItem.getParameter("uploadUser");
		String userId = (String) workItem.getParameter("userId");
		BudgetException budgetException = (BudgetException) workItem.getParameter("budgetException");

		variableMap.put("amount",amount);
		variableMap.put("cellAmount",cellAmount);
		variableMap.put("cellSerialNo",cellSerialNo);
		variableMap.put("comments",comments);
		variableMap.put("createdDateStr",createdDateStr);
		variableMap.put("cellMonth",cellMonth);
		variableMap.put("ebtEventId",ebtEventId);
		variableMap.put("ebtEventsRowId",ebtEventsRowId);
		variableMap.put("eveId",eveId);
		variableMap.put("fileContent",fileContent);
		variableMap.put("fileContentName",fileContentName);
		variableMap.put("filename",filename);
		variableMap.put("foundEMC",foundEMC);
		variableMap.put("foundPSO",foundPSO);
		variableMap.put("insertedDateRange",insertedDateRange);
		variableMap.put("month",month);
		variableMap.put("nextVersionNum",nextVersionNum);
		variableMap.put("uploadUser",uploadUser);
		variableMap.put("userId",userId);
		variableMap.put("budgetException",budgetException);


		variableMap = ContextInitializer.getInstance().invokeEMCPSOBudgetUpload(variableMap, operationPath);


		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
