package com.emc.settlement.config.invoker.fileupload;

import java.util.Map;

import com.emc.settlement.backend.fileupload.JMSListenerMain;
import com.emc.settlement.config.invoker.BaseInvoker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JMSListenerMainInvoker implements BaseInvoker {

	@Autowired
	private JMSListenerMain jmsListenerMain;

	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		variableMap = jmsListenerMain.jmsReceiver(variableMap);
		return variableMap;
	}
}
