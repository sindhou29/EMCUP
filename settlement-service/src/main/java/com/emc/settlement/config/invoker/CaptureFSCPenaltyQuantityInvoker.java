package com.emc.settlement.config.invoker;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.emc.settlement.backend.runrelated.CaptureFSCPenaltyQuantity;
import com.emc.settlement.model.backend.service.task.runrelated.CaptureFSCPenaltyQuantityTask;

@Component
public class CaptureFSCPenaltyQuantityInvoker implements BaseInvoker {

	@Autowired
	private CaptureFSCPenaltyQuantity captureFSCPenaltyQuantity;

	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		if (operationPath.equalsIgnoreCase(CaptureFSCPenaltyQuantityTask.CAPTUREFSCPENALTYQTY.getValue())) {
			captureFSCPenaltyQuantity.captureFSCPenaltyQty(variableMap);
		} else if (operationPath.equalsIgnoreCase(CaptureFSCPenaltyQuantityTask.ISFSCEFFECTIVE.getValue())) {
			variableMap = captureFSCPenaltyQuantity.isFSCEffective(variableMap);
		} else if (operationPath.equalsIgnoreCase(CaptureFSCPenaltyQuantityTask.UPDATEEVENT.getValue())) {
			captureFSCPenaltyQuantity.updateEvent(variableMap);
		} else if (operationPath.equalsIgnoreCase(CaptureFSCPenaltyQuantityTask.EXCEPTIONHANDLER.getValue())) {
			captureFSCPenaltyQuantity.exceptionHandler(variableMap);
		}
		return variableMap;
	}
}
