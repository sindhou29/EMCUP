package com.emc.settlement.config.invoker.fileupload;

import java.util.Map;

import com.emc.settlement.backend.fileupload.BilateralContractValidateAndUploadData;
import com.emc.settlement.config.invoker.BaseInvoker;
import com.emc.settlement.model.backend.service.task.fileupload.BilateralContractValidateAndUploadDataTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BilateralContractValidateAndUploadDataInvoker implements BaseInvoker {

	@Autowired
	private BilateralContractValidateAndUploadData bilateralContractValidateAndUploadData;

	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		if(operationPath.equals(BilateralContractValidateAndUploadDataTask.VALIDATEBILATERALCONTRACTS.getValue())) {
			variableMap = bilateralContractValidateAndUploadData.validateBilateralContracts(variableMap);
		} else if(operationPath.equals(BilateralContractValidateAndUploadDataTask.UPLOADBILATERALCONTRACTS.getValue())) {
			bilateralContractValidateAndUploadData.uploadBilateralContracts(variableMap);
		} else if(operationPath.equals(BilateralContractValidateAndUploadDataTask.HANDLEEXCEPTION.getValue())) {
			variableMap = bilateralContractValidateAndUploadData.handleException(variableMap);
		}
		return variableMap;
	}
}
