package com.emc.settlement.config.invoker.fileupload;

import java.util.Map;

import com.emc.settlement.backend.fileupload.VerifyAndLoadClawbackQuantitiesData;
import com.emc.settlement.config.invoker.BaseInvoker;
import com.emc.settlement.model.backend.service.task.fileupload.VerifyAndLoadClawbackQuantitiesDataTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VerifyAndLoadClawbackQuantitiesDataInvoker implements BaseInvoker {

	@Autowired
	private VerifyAndLoadClawbackQuantitiesData verifyAndLoadClawbackQuantitiesData;

	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		if(operationPath.equals(VerifyAndLoadClawbackQuantitiesDataTask.VERIFYANDLOADCLAWBACKDATA.getValue())) {
			variableMap = verifyAndLoadClawbackQuantitiesData.verifyAndLoadClawbackData(variableMap);
		} else if(operationPath.equals(VerifyAndLoadClawbackQuantitiesDataTask.UPDATEEVENT.getValue())) {
			verifyAndLoadClawbackQuantitiesData.updateEvent(variableMap);
		} else if(operationPath.equals(VerifyAndLoadClawbackQuantitiesDataTask.UPDATEFAILEVENT.getValue())) {
			verifyAndLoadClawbackQuantitiesData.updateFailEvent(variableMap);
		} else if(operationPath.equals(VerifyAndLoadClawbackQuantitiesDataTask.ALERTNOTIFY.getValue())) {
			verifyAndLoadClawbackQuantitiesData.alertNotify(variableMap);
		}
		return variableMap;
	}
}
