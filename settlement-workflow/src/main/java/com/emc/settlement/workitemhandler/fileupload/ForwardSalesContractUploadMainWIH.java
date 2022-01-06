package com.emc.settlement.workitemhandler.fileupload;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.backend.fileupload.ForwardSalesContractUploadMain;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.exceptions.FSCUploadException;
import com.emc.settlement.model.backend.pojo.CsvFileValidator;
import com.emc.settlement.model.backend.pojo.UploadFileInfo;
import com.emc.settlement.model.backend.service.task.fileupload.ForwardSalesContractUploadMainTask;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class ForwardSalesContractUploadMainWIH implements WorkItemHandler {
	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		Map<String, Object> variableMap = new HashMap<String, Object>();
		String operationPath = (String) workItem.getParameter("Operation");
		String comments = (String) workItem.getParameter("comments");
		String compressed = (String) workItem.getParameter("compressed");
		CsvFileValidator csvFileValidator = (CsvFileValidator) workItem.getParameter("csvFileValidator");
		String ebtEventId = (String) workItem.getParameter("ebtEventId");
		String ebtEventsRowId = (String) workItem.getParameter("ebtEventsRowId");
		String eveId = (String) workItem.getParameter("eveId");
		String fileContentName = (String) workItem.getParameter("fileContentName");
		UploadFileInfo fileInfo = (UploadFileInfo) workItem.getParameter("fileInfo");
		String filename = (String) workItem.getParameter("filename");
		String strFirstSettDate = (String) workItem.getParameter("strFirstSettDate");
		String strLastSettDate = (String) workItem.getParameter("strLastSettDate");
		String uploadMethod = (String) workItem.getParameter("uploadMethod");
		Date uploadTime = (Date) workItem.getParameter("uploadTime");
		String uploadUser = (String) workItem.getParameter("uploadUser");
		String userId = (String) workItem.getParameter("userId");
		FSCUploadException fscException = (FSCUploadException) workItem.getParameter("fscException");

		variableMap.put("comments",comments);
		variableMap.put("compressed",compressed);
		variableMap.put("csvFileValidator",csvFileValidator);
		variableMap.put("ebtEventId",ebtEventId);
		variableMap.put("ebtEventsRowId",ebtEventsRowId);
		variableMap.put("eveId",eveId);
		variableMap.put("fileContentName",fileContentName);
		variableMap.put("fileInfo",fileInfo);
		variableMap.put("filename",filename);
		variableMap.put("strFirstSettDate",strFirstSettDate);
		variableMap.put("strLastSettDate",strLastSettDate);
		variableMap.put("uploadMethod",uploadMethod);
		variableMap.put("uploadTime",uploadTime);
		variableMap.put("uploadUser",uploadUser);
		variableMap.put("userId",userId);
		variableMap.put("fscException",fscException);

		/*ForwardSalesContractUploadMain forwardSalesContractUploadMain = ContextInitializer.getInstance().getContext().getBean(ForwardSalesContractUploadMain.class);
		if(operationPath.equals(ForwardSalesContractUploadMainTask.CAPTUREMETADATA.getValue())) {
			variableMap = forwardSalesContractUploadMain.captureMetaData(variableMap);
		} else if(operationPath.equals(ForwardSalesContractUploadMainTask.SENDACKTOMSSL.getValue())) {
			forwardSalesContractUploadMain.sendACKToMSSL(variableMap);
		} else if(operationPath.equals(ForwardSalesContractUploadMainTask.VALIDATEFSCFILE.getValue())) {
			variableMap = forwardSalesContractUploadMain.validateFSCFile(variableMap);
		} else if(operationPath.equals(ForwardSalesContractUploadMainTask.UPDATEEBTEVENT.getValue())) {
			forwardSalesContractUploadMain.updateEBTEvent(variableMap);
		} else if(operationPath.equals(ForwardSalesContractUploadMainTask.FSCUPLOADEXCEPTION.getValue())) {
			forwardSalesContractUploadMain.fscUploadException(variableMap);
		} else if(operationPath.equals(ForwardSalesContractUploadMainTask.SENDEXCEPTIONNOTIFICATION.getValue())) {
			forwardSalesContractUploadMain.sendExceptionNotification(variableMap);
		}*/

		variableMap = ContextInitializer.getInstance().invokeForwardSalesContractUploadMain(variableMap, operationPath);


		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
