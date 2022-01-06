package com.emc.settlement.workitemhandler.runrelated;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.emc.settlement.backend.runrelated.CashFlowReconciliation;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.exceptions.SettlementRunException;
import com.emc.settlement.model.backend.pojo.CashFlow;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import com.emc.settlement.model.backend.service.task.runrelated.CashFlowReconciliationTask;

public class CashFlowReconciliationWIH implements WorkItemHandler {

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		Map<String, Object> variableMap = new HashMap<String, Object>();

		String operationPath = (String) workItem.getParameter("Operation");
		String cashflow = (String) workItem.getParameter("cashflow");
		Date drEffectiveDate = (Date) workItem.getParameter("drEffectiveDate");
		String eveId = (String) workItem.getParameter("eveId");
		List<CashFlow> negCashFlowArray = (ArrayList<CashFlow>) workItem.getParameter("negCashFlowArray");
		List<CashFlow> posCashFlowArray = (ArrayList<CashFlow>) workItem.getParameter("posCashFlowArray");
		Boolean ntEffective = (Boolean) workItem.getParameter("ntEffective");
		Boolean runSuccess = (Boolean) workItem.getParameter("runSuccess");
		Date settlementDate = (Date) workItem.getParameter("settlementDate");
		String settlementRunId = (String) workItem.getParameter("settlementRunId");
		String standingVersion = (String) workItem.getParameter("standingVersion");
		SettlementRunParams settlementParam = (SettlementRunParams) workItem.getParameter("settlementParam");
		SettlementRunException exception = (SettlementRunException) workItem.getParameter("exception");

		variableMap.put("cashflow", cashflow);
		variableMap.put("drEffectiveDate", drEffectiveDate);
		variableMap.put("eveId", eveId);
		variableMap.put("negCashFlowArray", negCashFlowArray);
		variableMap.put("posCashFlowArray", posCashFlowArray);
		variableMap.put("ntEffective", ntEffective);
		variableMap.put("runSuccess", runSuccess);
		variableMap.put("settlementDate", settlementDate);
		variableMap.put("settlementRunId", settlementRunId);
		variableMap.put("standingVersion", standingVersion);
		variableMap.put("settlementParam", settlementParam);
		variableMap.put("exception", exception);

		variableMap = ContextInitializer.getInstance().invokeCashFlowReconciliation(variableMap, operationPath);

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

}
