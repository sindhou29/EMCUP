package com.emc.settlement.config.invoker;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.emc.settlement.backend.runrelated.ForceDenySettlementRun;
import com.emc.settlement.model.backend.service.task.runrelated.ForceDenySettlementRunTask;

@Component
public class ForceDenySettlementRunInvoker implements BaseInvoker {

	@Autowired
	private ForceDenySettlementRun forceDenySettlementRun;

	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		if (operationPath.equalsIgnoreCase(ForceDenySettlementRunTask.FORCEDENYSETTRUN.getValue())) {
			forceDenySettlementRun.forceDenySettRun(variableMap);
		} else if (operationPath.equalsIgnoreCase(ForceDenySettlementRunTask.EXCEPTIONHANDLER.getValue())) {
			forceDenySettlementRun.exceptionHandler(variableMap);
		}
		return variableMap;
	}
}
