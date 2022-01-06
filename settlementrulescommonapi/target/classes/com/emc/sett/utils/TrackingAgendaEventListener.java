package com.emc.sett.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.core.event.DefaultAgendaEventListener;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.runtime.rule.Match;

public class TrackingAgendaEventListener extends DefaultAgendaEventListener  {

    private List<Match> matchList = new ArrayList<Match>();
    private Set<String> coverageList = new HashSet<String>();

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        Rule rule = event.getMatch().getRule();

        String ruleName = rule.getName();
        Map<String, Object> ruleMetaDataMap = rule.getMetaData();

        matchList.add(event.getMatch());
        coverageList.add(ruleName);
        StringBuilder sb = new StringBuilder("Rule fired: " + ruleName);

        if (ruleMetaDataMap.size() > 0) {
            sb.append("\r\n  With [" + ruleMetaDataMap.size() + "] meta-data:");
            for (String key : ruleMetaDataMap.keySet()) {
                sb.append("\r\n    key=" + key + ", value="
                        + ruleMetaDataMap.get(key));
            }
        }
    }

    public boolean isRuleFired(String ruleName) {
        for (Match a : matchList) {
            if (a.getRule().getName().equals(ruleName)) {
                return true;
            }
        }
        return false;
    }

    public void reset() {
        matchList.clear();
        coverageList.clear();
    }

    public final List<Match> getMatchList() {
        return matchList;
    }

    public final Set<String> getCoverageList() {
        return coverageList;
    }

    public String matchsToString() {
        if (matchList.size() == 0) {
            return "No matchs occurred.";
        } else {
            StringBuilder sb = new StringBuilder("Matchs: ");
            for (Match match : matchList) {
                sb.append("\r\n  rule: ").append(match.getRule().getName());
            }
            return sb.toString();
        }
    }

    public String coverageToString() {
        if (coverageList.size() == 0) {
            return "No coverages occurred.";
        } else {
            StringBuilder sb = new StringBuilder("Rules coverage: ");
            List<String> sortedList = new ArrayList<String>(coverageList);
            Collections.sort(sortedList);
            for (String rule : sortedList) {
                sb.append("\r\n  rule: ").append(rule);
            }
            return sb.toString();
        }
    }

    public String getLastTenMatchs() {
    	int cnt = matchList.size();
        if (cnt == 0) {
            return "No matchs occurred.";
        } else {
            StringBuilder sb = new StringBuilder("Last 10 matchs: \r\n  rule: ...");
            for (int i=cnt-10; i<cnt; i++) {
                sb.append("\r\n  rule: ").append(matchList.get(i).getRule().getName());
            }
            return sb.toString();
        }
    }
}
