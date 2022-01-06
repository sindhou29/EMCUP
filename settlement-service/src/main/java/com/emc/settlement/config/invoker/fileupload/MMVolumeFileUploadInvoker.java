package com.emc.settlement.config.invoker.fileupload;

import java.util.Map;

import com.emc.settlement.backend.fileupload.MMVolumeFileUpload;
import com.emc.settlement.config.invoker.BaseInvoker;
import com.emc.settlement.model.backend.service.task.fileupload.MMVolumeFileUploadTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MMVolumeFileUploadInvoker implements BaseInvoker {

	@Autowired
	private MMVolumeFileUpload mmVolumeFileUpload;

	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
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
		}
		return variableMap;
	}
}
