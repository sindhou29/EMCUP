package com.emc.settlement.config.invoker;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.emc.settlement.backend.runrelated.AccountInterfaceMain;
import com.emc.settlement.model.backend.service.task.runrelated.AccountInterfaceMainTask;

@Component
public class AccountingInterfaceInvoker implements BaseInvoker {

	@Autowired
	private AccountInterfaceMain accountInterfaceMain;

	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		if (operationPath.equalsIgnoreCase(AccountInterfaceMainTask.DOACCOUNTINGINTERFACE.getValue())) {
			accountInterfaceMain.doAccountingInterface(variableMap);
		} else if (operationPath.equalsIgnoreCase(AccountInterfaceMainTask.EXCEPTIONHANDLER.getValue())) {
			accountInterfaceMain.exceptionHandler(variableMap);
		}
		return variableMap;
	}
}
