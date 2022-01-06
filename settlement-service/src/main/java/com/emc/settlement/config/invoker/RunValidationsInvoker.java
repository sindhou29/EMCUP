package com.emc.settlement.config.invoker;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.emc.settlement.backend.runrelated.RunValidations;
import com.emc.settlement.model.backend.service.task.runrelated.RunValidationsTask;

@Component
public class RunValidationsInvoker implements BaseInvoker {

	@Autowired
	private RunValidations runValidations;

	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		if (operationPath.equalsIgnoreCase(RunValidationsTask.VALIDATESETTLEMENTRUN.getValue())) {
			runValidations.validateSettlementRun(variableMap);
		} else if (operationPath.equalsIgnoreCase(RunValidationsTask.NOTVALID.getValue())) {
			runValidations.notValid(variableMap);
		} else if (operationPath.equalsIgnoreCase(RunValidationsTask.FORTESTRUN.getValue())) {
			variableMap = runValidations.forTestRun(variableMap);
		} else if (operationPath.equalsIgnoreCase(RunValidationsTask.EXCEPTIONHANDLER.getValue())) {
			runValidations.exceptionHandler(variableMap);
		}
		return variableMap;
	}
}
