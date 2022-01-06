package com.emc.settlement.config.invoker.fileupload;

import java.util.Map;

import com.emc.settlement.backend.fileupload.ForwardSalesContractUploadMain;
import com.emc.settlement.config.invoker.BaseInvoker;
import com.emc.settlement.model.backend.service.task.fileupload.ForwardSalesContractUploadMainTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ForwardSalesContractUploadMainInvoker implements BaseInvoker {

	@Autowired
	private ForwardSalesContractUploadMain forwardSalesContractUploadMain;


	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		if(operationPath.equals(ForwardSalesContractUploadMainTask.CAPTUREMETADATA.getValue())) {
			variableMap = forwardSalesContractUploadMain.captureMetaData(variableMap);
		} else if(operationPath.equals(ForwardSalesContractUploadMainTask.SENDACKTOMSSL.getValue())) {
			forwardSalesContractUploadMain.sendACKToMSSL(variableMap);
		} else if(operationPath.equals(ForwardSalesContractUploadMainTask.VALIDATEFSCFILE.getValue())) {
			variableMap = forwardSalesContractUploadMain.validateFSCFile(variableMap);
		} else if(operationPath.equals(ForwardSalesContractUploadMainTask.UPDATEEBTEVENT.getValue())) {
			forwardSalesContractUploadMain.updateEBTEvent(variableMap);
		} else if(operationPath.equals(ForwardSalesContractUploadMainTask.FSCUPLOADEXCEPTION.getValue())) {
			forwardSalesContractUploadMain.fscUploadException(variableMap);
		} else if(operationPath.equals(ForwardSalesContractUploadMainTask.SENDEXCEPTIONNOTIFICATION.getValue())) {
			forwardSalesContractUploadMain.sendExceptionNotification(variableMap);
		}
		return variableMap;
	}
}
