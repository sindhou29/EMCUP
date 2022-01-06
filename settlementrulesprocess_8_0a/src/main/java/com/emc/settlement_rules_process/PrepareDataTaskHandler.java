package com.emc.settlement_rules_process;

import javax.sql.DataSource;

import com.emc.sett.common.IRuleflowInterface;
import com.emc.sett.utils.UtilityFunctions;
import com.emc.settlement.model.backend.pojo.SettRunPkg;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;

public class PrepareDataTaskHandler {


	public void executeWorkItem(org.kie.api.runtime.process.ProcessContext ctx) throws Exception {

        System.out.println("Preparing settlement data... " + new java.util.Date());
        
        IRuleflowInterface irule = (IRuleflowInterface)ctx.getVariable("irule");
        SettRunPkg runPackage = (SettRunPkg)ctx.getVariable("runPackage");
        SettlementRunParams runParams = (SettlementRunParams)ctx.getVariable("runParams");
        DataSource nems = (DataSource)ctx.getVariable("nems");
        
        if (runParams.isRegressionMode() == false) {
        	UtilityFunctions.logJAMMessage(nems, runParams.runEveId, "I", "MarketRules.getVersionString", 
        			"Market Rule version used: " + irule.getVersionString(), "");
        }

        com.emc.sett.impl.SettlementData settData = (com.emc.sett.impl.SettlementData)(irule.prepareData("", runPackage, runParams, nems));
        ctx.setVariable("settData", settData);

        System.out.println("settData count = " + settData.getRecordsCount());

        System.out.println("Finished settlement data... :" + new java.util.Date());
	}
}
