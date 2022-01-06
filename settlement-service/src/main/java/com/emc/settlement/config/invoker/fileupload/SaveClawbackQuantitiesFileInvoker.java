package com.emc.settlement.config.invoker.fileupload;

import java.util.Map;

import com.emc.settlement.backend.fileupload.SaveClawbackQuantitiesFile;
import com.emc.settlement.config.invoker.BaseInvoker;
import com.emc.settlement.model.backend.service.task.fileupload.SaveClawbackQuantitiesFileTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SaveClawbackQuantitiesFileInvoker implements BaseInvoker {

	@Autowired
	private SaveClawbackQuantitiesFile saveClawbackQuantitiesFile;

	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		if(operationPath.equals(SaveClawbackQuantitiesFileTask.CAPTUREMETADATA.getValue())) {
			variableMap = saveClawbackQuantitiesFile.captureMetaData(variableMap);
		} else if(operationPath.equals(SaveClawbackQuantitiesFileTask.STORECLAWBACKFILE.getValue())) {
			variableMap = saveClawbackQuantitiesFile.storeClawbackFile(variableMap);
		} else if(operationPath.equals(SaveClawbackQuantitiesFileTask.UPDATEEVENT.getValue())) {
			saveClawbackQuantitiesFile.updateEvent(variableMap);
		} else if(operationPath.equals(SaveClawbackQuantitiesFileTask.ALERTNOTIFY.getValue())) {
			saveClawbackQuantitiesFile.alertNotify(variableMap);
		}
		return variableMap;
	}
}
