package com.emc.settlement.config.invoker.runrelated;

import java.util.Map;

import com.emc.settlement.backend.common.UtilityService;
import com.emc.settlement.backend.runrelated.IPDataVerfications;
import com.emc.settlement.config.invoker.BaseInvoker;
import com.emc.settlement.model.backend.service.task.runrelated.IPDataVerificationsTask;
import com.emc.settlement.model.backend.service.task.runrelated.SettlementRunProcessTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IPDataVerificationsValidationsInvoker implements BaseInvoker {

	@Autowired
	private UtilityService utilityService;
	
	@Autowired
	private IPDataVerfications ipDataVerfications;

	/*@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		utilityService.logJAMMessage(variableMap);
		return variableMap;
	}*/
	
	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		if (operationPath.equalsIgnoreCase(IPDataVerificationsTask.EXCEPTIONHANDLER.getValue())) {
			ipDataVerfications.exceptionHandler(variableMap);
		} else if (operationPath.equalsIgnoreCase(IPDataVerificationsTask.OTHEREXCEPTIONHANDLER.getValue())) {
			ipDataVerfications.otherExceptionHandler(variableMap);
		}
		return variableMap;
	}
}
