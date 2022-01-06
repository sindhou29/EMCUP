package com.emc.settlement.workitemhandler.fileupload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.emc.settlement.backend.fileupload.BilateralContractValidateAndUploadData;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.exceptions.BilateralContractUploadException;
import com.emc.settlement.model.backend.pojo.CsvFileValidator;
import com.emc.settlement.model.backend.service.task.fileupload.BilateralContractValidateAndUploadDataTask;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class BilateralContractValidateAndUploadDataWIH implements WorkItemHandler {
	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		Map<String, Object> variableMap = new HashMap<String, Object>();
		String operationPath = (String) workItem.getParameter("Operation");
		String acgId = (String) workItem.getParameter("acgId");
		String contractName = (String) workItem.getParameter("contractName");
		String contractType = (String) workItem.getParameter("contractType");
		CsvFileValidator csvFileValidator = (CsvFileValidator) workItem.getParameter("csvFileValidator");
		String ebtEventId = (String) workItem.getParameter("ebtEventId");
		String eveId = (String) workItem.getParameter("eveId");
		String externalId = (String) workItem.getParameter("externalId");
		List<Map<Integer, Object>> first_row_array = (List<Map<Integer, Object>>) workItem.getParameter("first_row_array");
		Boolean fromSEW = (Boolean) workItem.getParameter("fromSEW");
		String inputPTPID = (String) workItem.getParameter("inputPTPID");
		Integer rowNum = (Integer) workItem.getParameter("rowNum");
		String sacPurchasedBy = (String) workItem.getParameter("sacPurchasedBy");
		String sacSoldBy = (String) workItem.getParameter("sacSoldBy");
		String userId = (String) workItem.getParameter("userId");
		Integer versionNum = (Integer) workItem.getParameter("versionNum");
		BilateralContractUploadException bilateralContractUploadException = (BilateralContractUploadException) workItem.getParameter("bilateralContractUploadException");

		variableMap.put("acgId",acgId);
		variableMap.put("contractName",contractName);
		variableMap.put("contractType",contractType);
		variableMap.put("csvFileValidator",csvFileValidator);
		variableMap.put("ebtEventId",ebtEventId);
		variableMap.put("eveId",eveId);
		variableMap.put("externalId",externalId);
		variableMap.put("first_row_array",first_row_array);
		variableMap.put("fromSEW",fromSEW);
		variableMap.put("inputPTPID",inputPTPID);
		variableMap.put("rowNum",rowNum);
		variableMap.put("sacPurchasedBy",sacPurchasedBy);
		variableMap.put("sacSoldBy",sacSoldBy);
		variableMap.put("userId",userId);
		variableMap.put("versionNum",versionNum);
		variableMap.put("bilateralContractUploadException", bilateralContractUploadException);

		variableMap = ContextInitializer.getInstance().invokeBilateralContractValidateAndUploadData(variableMap, operationPath);

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
