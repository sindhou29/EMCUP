package com.emc.settlement.config.invoker.fileupload;

import java.util.Map;

import com.emc.settlement.backend.fileupload.BilateralContractUploadMain;
import com.emc.settlement.config.invoker.BaseInvoker;
import com.emc.settlement.model.backend.service.task.fileupload.BilateralContractUploadMainTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BilateralContractUploadMainInvoker implements BaseInvoker {

	@Autowired
	private BilateralContractUploadMain bilateralContractUploadMain;

	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		if(operationPath.equals(BilateralContractUploadMainTask.CAPTUREFILEUPLOADPARAMETERS.getValue())) {
			variableMap = bilateralContractUploadMain.captureFileUploadParameters(variableMap);
		} else if(operationPath.equals(BilateralContractUploadMainTask.STORECSVFILEINTODATABASE.getValue())) {
			bilateralContractUploadMain.storeCSVFileIntoDatabase(variableMap);
		} else if(operationPath.equals(BilateralContractUploadMainTask.VALIDATEFILE.getValue())) {
			variableMap = bilateralContractUploadMain.validateFile(variableMap);
		} else if(operationPath.equals(BilateralContractUploadMainTask.UPDATEEBTEVENT.getValue())) {
			bilateralContractUploadMain.updateEBTEvent(variableMap);
		} else if(operationPath.equals(BilateralContractUploadMainTask.HANDLEEXCEPTION.getValue())) {
			bilateralContractUploadMain.handleException(variableMap);
		} else if(operationPath.equals(BilateralContractUploadMainTask.SENDEXCEPTIONNOTIFICATION.getValue())) {
			bilateralContractUploadMain.sendExceptionNotification(variableMap);
		}
		return variableMap;
	}
}
