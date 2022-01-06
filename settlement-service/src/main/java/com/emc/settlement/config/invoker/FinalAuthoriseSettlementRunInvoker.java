package com.emc.settlement.config.invoker;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.emc.settlement.backend.runrelated.FinalAuthoriseSettlementRun;
import com.emc.settlement.model.backend.service.task.runrelated.FinalAuthoriseSettlementRunTask;

@Component
public class FinalAuthoriseSettlementRunInvoker implements BaseInvoker {

	@Autowired
	private FinalAuthoriseSettlementRun finalAuthoriseSettlementRun;

	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		if (operationPath.equalsIgnoreCase(FinalAuthoriseSettlementRunTask.AUTHORISATIONSETTLEMENTRUN.getValue())) {
			variableMap = finalAuthoriseSettlementRun.authorisationSettlementRun(variableMap);
		}else if (operationPath.equalsIgnoreCase(FinalAuthoriseSettlementRunTask.CREATEUSAPFILEFROMMSSL.getValue())) {
			finalAuthoriseSettlementRun.createUsapFileForMSSL(variableMap);
		}else if (operationPath.equalsIgnoreCase(FinalAuthoriseSettlementRunTask.GENERATEACCOUNTFILES.getValue())) {
			finalAuthoriseSettlementRun.generateAccountFiles(variableMap);
		}else if (operationPath.equalsIgnoreCase(FinalAuthoriseSettlementRunTask.STARTDAILYMANUALPROCESS.getValue())) {
			finalAuthoriseSettlementRun.startDailyManualProcess(variableMap);
		}else if (operationPath.equalsIgnoreCase(FinalAuthoriseSettlementRunTask.STARTMMBRAP.getValue())) {
			finalAuthoriseSettlementRun.startMMBRAP(variableMap);
		}else if (operationPath.equalsIgnoreCase(FinalAuthoriseSettlementRunTask.STARTMMBRAR.getValue())) {
			finalAuthoriseSettlementRun.startMMBRAR(variableMap);
		}else if (operationPath.equalsIgnoreCase(FinalAuthoriseSettlementRunTask.EXCEPTIONHANDLER.getValue())) {
			finalAuthoriseSettlementRun.exceptionHandler(variableMap);
		}
		return variableMap;
	}
}
