package com.emc.settlement.config.invoker.fileupload;

import java.util.Map;

import com.emc.settlement.backend.fileupload.VestingContractUploadMain;
import com.emc.settlement.config.invoker.BaseInvoker;
import com.emc.settlement.model.backend.service.task.fileupload.VestingContractUploadMainTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VestingContractUploadMainInvoker implements BaseInvoker {

	@Autowired
	private VestingContractUploadMain vestingContractUploadMain;

	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		if(operationPath.equals(VestingContractUploadMainTask.CAPTUREMETADATA.getValue())) {
			variableMap = vestingContractUploadMain.captureMetaData(variableMap);
		} else if(operationPath.equals(VestingContractUploadMainTask.SENDACKTOMSSL.getValue())) {
			vestingContractUploadMain.sendACKToMSSL(variableMap);
		} else if(operationPath.equals(VestingContractUploadMainTask.VALIDATEVESTINGFILE.getValue())) {
			variableMap = vestingContractUploadMain.validateVestingFile(variableMap);
		} else if(operationPath.equals(VestingContractUploadMainTask.UPDATEEBTEVENT.getValue())) {
			vestingContractUploadMain.updateEBTEvent(variableMap);
		} else if(operationPath.equals(VestingContractUploadMainTask.CHECKCUTOFF.getValue())) {
			variableMap = vestingContractUploadMain.checkCutoff(variableMap);
		} else if(operationPath.equals(VestingContractUploadMainTask.CALCULATEENERGYBIDPRICEMIN.getValue())) {
			vestingContractUploadMain.calculateEnergyBidPriceMin(variableMap);
		} else if(operationPath.equals(VestingContractUploadMainTask.HANDLEEXCEPTION.getValue())) {
			vestingContractUploadMain.handleException(variableMap);
		} else if(operationPath.equals(VestingContractUploadMainTask.SENDEXCEPTIONNOTIFICATION.getValue())) {
			vestingContractUploadMain.sendExceptionNotification(variableMap);
		} else if(operationPath.equals(VestingContractUploadMainTask.ENERGYBIDPRICEMINEXCEPTION.getValue())) {
			vestingContractUploadMain.energyBidPriceMinException(variableMap);
		} else if(operationPath.equals(VestingContractUploadMainTask.SENDWARNINGNOTIFICATION.getValue())) {
			vestingContractUploadMain.sendWarningNotification(variableMap);
		}
		return variableMap;
	}
}
