package com.emc.settlement.config.invoker.fileupload;

import java.util.Map;

import com.emc.settlement.backend.fileupload.EMCPSOBudgetUpload;
import com.emc.settlement.config.invoker.BaseInvoker;
import com.emc.settlement.model.backend.service.task.fileupload.EMCPSOBudgetUploadTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EMCPSOBudgetUploadInvoker implements BaseInvoker {

	@Autowired
	private EMCPSOBudgetUpload emcpsoBudgetUpload;

	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		if(operationPath.equals(EMCPSOBudgetUploadTask.CAPTUREFILEUPLOADPARAMETERS.getValue())) {
			variableMap = emcpsoBudgetUpload.captureFileUploadParameters(variableMap);
		} else if(operationPath.equals(EMCPSOBudgetUploadTask.VALIDATEFILE.getValue())) {
			variableMap = emcpsoBudgetUpload.validateFile(variableMap);
		} else if(operationPath.equals(EMCPSOBudgetUploadTask.GETBUDGETDATA.getValue())) {
			variableMap = emcpsoBudgetUpload.getBudgetData(variableMap);
		} else if(operationPath.equals(EMCPSOBudgetUploadTask.VALIDATEBUDGETDATA.getValue())) {
			variableMap = emcpsoBudgetUpload.validateBudgetData(variableMap);
		} else if(operationPath.equals(EMCPSOBudgetUploadTask.UPLOADBUDGETDATA.getValue())) {
			variableMap = emcpsoBudgetUpload.uploadBudgetData(variableMap);
		} else if(operationPath.equals(EMCPSOBudgetUploadTask.UPDATEEBTEVENT.getValue())) {
			emcpsoBudgetUpload.updateEBTEvent(variableMap);
		} else if(operationPath.equals(EMCPSOBudgetUploadTask.HANDLEEXCEPTION.getValue())) {
			emcpsoBudgetUpload.handleException(variableMap);
		} else if(operationPath.equals(EMCPSOBudgetUploadTask.SENDEXCEPTIONNOTIFICATION.getValue())) {
			emcpsoBudgetUpload.sendExceptionNotification(variableMap);
		}
		return variableMap;
	}
}
