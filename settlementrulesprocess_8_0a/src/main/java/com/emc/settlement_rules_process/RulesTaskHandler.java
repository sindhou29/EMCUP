package com.emc.settlement_rules_process;

import javax.sql.DataSource;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.emc.sett.common.AbstractSettlementData;
import com.emc.sett.common.IRuleflowInterface;
import com.emc.sett.utils.TrackingAgendaEventListener;
import com.emc.sett.utils.UtilityFunctions;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;

public class RulesTaskHandler {

	public void executeWorkItem(org.kie.api.runtime.process.ProcessContext ctx) throws Exception {

        System.out.println("Inside rules task handler");
        
        IRuleflowInterface irule = (IRuleflowInterface)ctx.getVariable("irule");
        KieContainer icontainer = (KieContainer)ctx.getVariable("icontainer");
        TrackingAgendaEventListener itracker = (TrackingAgendaEventListener)ctx.getVariable("itracker");
        Integer currGroup = (Integer)ctx.getVariable("currGroup");
        AbstractSettlementData settData = (AbstractSettlementData)ctx.getVariable("settData");
        SettlementRunParams runParams = (SettlementRunParams)ctx.getVariable("runParams");
        DataSource nems = (DataSource)ctx.getVariable("nems");

        String grps[][] = irule.getRuleflowGroups();

		System.out.println("Executing " +grps[currGroup][0] + " rule flow group... :" + new java.util.Date());
		System.out.println( "kBase : " + grps[currGroup][1]);
        
        if (runParams.isRegressionMode() == false) {
        	UtilityFunctions.logJAMMessage(nems, runParams.runEveId, "I", "MarketRulesProcess.RulesTaskHandler", 
        			"Applying rules: " + grps[currGroup][0], "");
        }

        KieSession ks = icontainer.newKieSession(grps[currGroup][1]);
        
        ks.addEventListener(itracker);
        irule.populateFacts(ks, settData, grps[currGroup][0]);
        ks.getAgenda().getAgendaGroup( grps[currGroup][0]).setFocus();
        ks.fireAllRules();
        ks.dispose();

		System.out.println("Done " +grps[currGroup][0] + " rule flow group... :" + new java.util.Date());
		
		ctx.setVariable("currGroup", currGroup + 1);
	}
}
