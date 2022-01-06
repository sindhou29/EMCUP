package com.emc.settlement.config.invoker;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.emc.settlement.backend.runrelated.LoadPendingCMFFiles;
import com.emc.settlement.model.backend.service.task.runrelated.LoadPendingCMFFilesTask;

@Component
public class LoadPendingCMFFilesInvoker implements BaseInvoker {

	@Autowired
	private LoadPendingCMFFiles loadPendingCMFFiles;

	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		if (operationPath.equalsIgnoreCase(LoadPendingCMFFilesTask.LOADPENDINGCMFFILES.getValue())) {
			loadPendingCMFFiles.loadPendingCMFFiles(variableMap);
		}
		return variableMap;
	}
}
