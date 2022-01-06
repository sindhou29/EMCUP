package com.emc.settlement.config.invoker.fileupload;

import java.util.Map;

import com.emc.settlement.backend.fileupload.VerifyAndLoadMSSLMeteringData;
import com.emc.settlement.config.invoker.BaseInvoker;
import com.emc.settlement.model.backend.service.task.fileupload.VerifyAndLoadMSSLMeteringDataTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VerifyAndLoadMSSLMeteringDataInvoker implements BaseInvoker {

	@Autowired
	private VerifyAndLoadMSSLMeteringData verifyAndLoadMSSLMeteringData;

	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {

		if(operationPath.equalsIgnoreCase(VerifyAndLoadMSSLMeteringDataTask.UPDATEPROCESSEDSTATUS.getValue())) {
			variableMap = verifyAndLoadMSSLMeteringData.updateProcessedStatus(variableMap);
		} else if(operationPath.equalsIgnoreCase(VerifyAndLoadMSSLMeteringDataTask.VALIDATEANDLOADDAILYMETERINGDATA.getValue())) {
			variableMap = verifyAndLoadMSSLMeteringData.validateAndLoadDailyMeteringData(variableMap);
		} else if(operationPath.equalsIgnoreCase(VerifyAndLoadMSSLMeteringDataTask.VALIDATEANDLOADCMFDATA.getValue())) {
			variableMap = verifyAndLoadMSSLMeteringData.validateAndLoadCMFData(variableMap);
		} else if(operationPath.equalsIgnoreCase(VerifyAndLoadMSSLMeteringDataTask.UPDATEEVENT.getValue())) {
			variableMap = verifyAndLoadMSSLMeteringData.updateEvent(variableMap);
		} else if(operationPath.equalsIgnoreCase(VerifyAndLoadMSSLMeteringDataTask.UPDATEFAILEVENT.getValue())) {
			variableMap = verifyAndLoadMSSLMeteringData.updateFailEvent(variableMap);
		} else if(operationPath.equalsIgnoreCase(VerifyAndLoadMSSLMeteringDataTask.ALERTNOTIFY.getValue())) {
			variableMap = verifyAndLoadMSSLMeteringData.alertNotify(variableMap);
		}
		return variableMap;
	}
}
