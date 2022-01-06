package com.emc.settlement.config.invoker;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.emc.settlement.backend.runrelated.CashFlowReconciliation;
import com.emc.settlement.model.backend.service.task.runrelated.CashFlowReconciliationTask;

@Component
public class CashFlowReconciliationInvoker implements BaseInvoker {

	@Autowired
	private CashFlowReconciliation cashFlowReconciliation;

	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		if (operationPath.equalsIgnoreCase(CashFlowReconciliationTask.CALCULATECASHFLOW.getValue())) {
			cashFlowReconciliation.calculateCashFlow(variableMap);
		} else if (operationPath.equalsIgnoreCase(CashFlowReconciliationTask.CHECKRUNSTATUS.getValue())) {
			variableMap = cashFlowReconciliation.checkRunStatus(variableMap);
		} else if (operationPath.equalsIgnoreCase(CashFlowReconciliationTask.GENERATEEXCELSHEET.getValue())) {
			variableMap = cashFlowReconciliation.generateExcelsheet(variableMap);
		}
		return variableMap;
	}
}
