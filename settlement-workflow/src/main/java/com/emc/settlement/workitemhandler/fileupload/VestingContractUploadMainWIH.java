package com.emc.settlement.workitemhandler.fileupload;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.backend.fileupload.VestingContractUploadMain;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.exceptions.EnergyBidPriceMinException;
import com.emc.settlement.model.backend.exceptions.VestingContractUploadException;
import com.emc.settlement.model.backend.pojo.CsvFileValidator;
import com.emc.settlement.model.backend.pojo.UploadFileInfo;
import com.emc.settlement.model.backend.service.task.fileupload.VestingContractUploadMainTask;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class VestingContractUploadMainWIH implements WorkItemHandler {
	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		String operation = (String) workItem.getParameter("Operation");
		String comments = (String) workItem.getParameter("comments");
		String compressed = (String) workItem.getParameter("compressed");
		CsvFileValidator csvFileValidator = (CsvFileValidator) workItem.getParameter("csvFileValidator");
		Boolean drDeployment = (Boolean) workItem.getParameter("drDeployment");
		Date drDeploymentDate = (Date) workItem.getParameter("drDeploymentDate");
		String ebtEventId = (String) workItem.getParameter("ebtEventId");
		String ebtEventsRowId = (String) workItem.getParameter("ebtEventsRowId");
		String egbEveId = (String) workItem.getParameter("egbEveId");
		String eveId = (String) workItem.getParameter("eveId");
		String fileContentName = (String) workItem.getParameter("fileContentName");
		UploadFileInfo fileInfo = (UploadFileInfo) workItem.getParameter("fileInfo");
		String filename = (String) workItem.getParameter("filename");
		Boolean realTimeCalc = (Boolean) workItem.getParameter("realTimeCalc");
		String strFirstSettDate = (String) workItem.getParameter("strFirstSettDate");
		String strLastSettDate = (String) workItem.getParameter("strLastSettDate");
		String uploadMethod = (String) workItem.getParameter("uploadMethod");
		String uploadTime = (String) workItem.getParameter("uploadTime");
		String uploadUser = (String) workItem.getParameter("uploadUser");
		String userId = (String) workItem.getParameter("userId");
		VestingContractUploadException vcException = (VestingContractUploadException) workItem.getParameter("vcException");
		EnergyBidPriceMinException egbException = (EnergyBidPriceMinException) workItem.getParameter("egbException");

		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("comments",comments);
		variableMap.put("compressed",compressed);
		variableMap.put("csvFileValidator",csvFileValidator);
		variableMap.put("drDeployment",drDeployment);
		variableMap.put("drDeploymentDate",drDeploymentDate);
		variableMap.put("ebtEventId",ebtEventId);
		variableMap.put("ebtEventsRowId",ebtEventsRowId);
		variableMap.put("egbEveId",egbEveId);
		variableMap.put("eveId",eveId);
		variableMap.put("fileContentName",fileContentName);
		variableMap.put("fileInfo",fileInfo);
		variableMap.put("filename",filename);
		variableMap.put("realTimeCalc",realTimeCalc);
		variableMap.put("strFirstSettDate",strFirstSettDate);
		variableMap.put("strLastSettDate",strLastSettDate);
		variableMap.put("uploadMethod",uploadMethod);
		variableMap.put("uploadTime",uploadTime);
		variableMap.put("uploadUser",uploadUser);
		variableMap.put("userId",userId);
		variableMap.put("vcException",vcException);
		variableMap.put("egbException",egbException);

		variableMap = ContextInitializer.getInstance().invokeVestingContractUploadMain(variableMap, operation);



		manager.completeWorkItem(workItem.getId(),variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
