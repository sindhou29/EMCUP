package com.emc.settlement.config.invoker.fileupload;

import java.util.Map;

import com.emc.settlement.backend.fileupload.SaveMSSLMeteringFile;
import com.emc.settlement.config.invoker.BaseInvoker;
import com.emc.settlement.model.backend.service.task.fileupload.SaveMSSLMeteringFileTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SaveMSSLMeteringFileInvoker implements BaseInvoker {

	@Autowired
	private SaveMSSLMeteringFile saveMSSLMeteringFile;

	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		if(operationPath.equalsIgnoreCase(SaveMSSLMeteringFileTask.CAPTUREMETADATA.getValue())) {
			variableMap = saveMSSLMeteringFile.captureMetaData(variableMap);
		} else if(operationPath.equalsIgnoreCase(SaveMSSLMeteringFileTask.SENDACKTOMSSL.getValue())) {
			variableMap = saveMSSLMeteringFile.sendACKToMSSL(variableMap);
		} else if(operationPath.equalsIgnoreCase(SaveMSSLMeteringFileTask.STOREMETERINGFILE.getValue())) {
			variableMap = saveMSSLMeteringFile.storeMeteringFile(variableMap);
		} else if(operationPath.equalsIgnoreCase(SaveMSSLMeteringFileTask.UPDATEEVENT.getValue())) {
			saveMSSLMeteringFile.updateEvent(variableMap);
		} else if(operationPath.equalsIgnoreCase(SaveMSSLMeteringFileTask.ALERTNOTIFY.getValue())) {
			variableMap = saveMSSLMeteringFile.alertNotify(variableMap);
		}
		return variableMap;
	}
}
