package com.emc.settlement.config.invoker;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.emc.settlement.backend.runrelated.ApproveSettlementRun;
import com.emc.settlement.model.backend.service.task.runrelated.ApproveSettlementRunTask;

@Component
public class ApproveSettlementRunInvoker implements BaseInvoker {

	@Autowired
	private ApproveSettlementRun approveSettlementRun;

	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		if (operationPath.equalsIgnoreCase(ApproveSettlementRunTask.FIRSTTIERAUTHSETTLEMENTRUN.getValue())) {
			approveSettlementRun.firstTierAuthSettlementRun(variableMap);
		} else if (operationPath.equalsIgnoreCase(ApproveSettlementRunTask.EXCEPTIONHANDLER.getValue())) {
			approveSettlementRun.exceptionHandler(variableMap);
		}
		return variableMap;
	}
}
