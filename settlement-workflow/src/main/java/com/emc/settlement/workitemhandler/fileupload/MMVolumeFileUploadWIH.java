package com.emc.settlement.workitemhandler.fileupload;

import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.backend.fileupload.MMVolumeFileUpload;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.exceptions.MMVolumeUploadException;
import com.emc.settlement.model.backend.pojo.CsvFileValidator;
import com.emc.settlement.model.backend.pojo.UploadFileInfo;
import com.emc.settlement.model.backend.service.task.fileupload.MMVolumeFileUploadTask;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class MMVolumeFileUploadWIH implements WorkItemHandler {

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		String operationPath = (String) workItem.getParameter("Operation");
		String comments = (String) workItem.getParameter("comments");
		CsvFileValidator csvFileValidator = (CsvFileValidator) workItem.getParameter("csvFileValidator");
		String ebtEventId = (String) workItem.getParameter("ebtEventId");
		String ebtEventsRowId = (String) workItem.getParameter("ebtEventsRowId");
		String eveId = (String) workItem.getParameter("eveId");
		String fileContentName = (String) workItem.getParameter("fileContentName");
		UploadFileInfo fileInfo = (UploadFileInfo) workItem.getParameter("fileInfo");
		String fileName = (String) workItem.getParameter("fileName");
		String uploadMethod = (String) workItem.getParameter("uploadMethod");
		String uploadUser = (String) workItem.getParameter("uploadUser");
		String uploadTime = (String) workItem.getParameter("uploadTime");
		String userId = (String) workItem.getParameter("userId");
		RuntimeException runtimeException = (RuntimeException) workItem.getParameter("runtimeException");
		MMVolumeUploadException mmVolumeException = (MMVolumeUploadException) workItem.getParameter("mmVolumeException");


		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("comments", comments);
		variableMap.put("csvFileValidator", csvFileValidator);
		variableMap.put("ebtEventId", ebtEventId);
		variableMap.put("ebtEventsRowId", ebtEventsRowId);
		variableMap.put("eveId", eveId);
		variableMap.put("fileContentName", fileContentName);
		variableMap.put("fileInfo", fileInfo);
		variableMap.put("fileName", fileName);
		variableMap.put("uploadMethod", uploadMethod);
		variableMap.put("uploadUser", uploadUser);
		variableMap.put("uploadTime", uploadTime);
		variableMap.put("userId", userId);
		variableMap.put("runtimeException", runtimeException);
		variableMap.put("mmVolumeException", mmVolumeException);

		/*MMVolumeFileUpload mmVolumeFileUpload = ContextInitializer.getInstance().getContext().getBean(MMVolumeFileUpload.class);
		if(operationPath.equals(MMVolumeFileUploadTask.CAPTUREMETADATA.getValue())) {
			variableMap = mmVolumeFileUpload.captureMetaData(variableMap);
		} else if(operationPath.equals(MMVolumeFileUploadTask.VALIDATEMMFILE.getValue())) {
			mmVolumeFileUpload.validateMMFile(variableMap);
		} else if(operationPath.equals(MMVolumeFileUploadTask.UPDATEEBTEVENT.getValue())) {
			mmVolumeFileUpload.updateEBTEvent(variableMap);
		} else if(operationPath.equals(MMVolumeFileUploadTask.MMFILEUPLOADEXCEPTION.getValue())) {
			mmVolumeFileUpload.mmFileUploadException(variableMap);
		} else if(operationPath.equals(MMVolumeFileUploadTask.SENDEXCEPTIONNOTIFICATION.getValue())) {
			mmVolumeFileUpload.sendExceptionNotification(variableMap);
		}*/

		variableMap = ContextInitializer.getInstance().invokeMMVolumeFileUpload(variableMap, operationPath);


		manager.completeWorkItem(workItem.getId(), variableMap);
	}
}
