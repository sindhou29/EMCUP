package com.emc.sett.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.emc.sett.common.AbstractSettlementData;

public class SettlementData extends AbstractSettlementData {

	private Global global = new Global();
	private Map<String, Account> accountMap = new HashMap<String, Account>();
	private Map<String, Adjustment> adjustmentMap = new HashMap<String, Adjustment>();
	private Map<String, Bilateral> bilateralMap = new HashMap<String, Bilateral>();
	private Map<String, Brq> brqMap = new HashMap<String, Brq>();
	private Map<String, List<Brq>> brqBuyMap = new HashMap<String, List<Brq>>();
	private Map<String, List<Brq>> brqSellMap = new HashMap<String, List<Brq>>();
	private Map<String, Cnmea> cnmeaMap = new HashMap<String, Cnmea>();
	private Map<String, Facility> facilityMap = new HashMap<String, Facility>();
	private Map<String, Fsc> fscMap = new HashMap<String, Fsc>();
	private Map<String, Ftr> ftrMap = new HashMap<String, Ftr>();
	private Map<String, Market> marketMap = new HashMap<String, Market>();
	private Map<String, Mnmea> mnmeaMap = new HashMap<String, Mnmea>();
	private Map<String, MnmeaSub> mnmeaSubMap = new HashMap<String, MnmeaSub>();
	private Map<String, CnmeaSettRe> cnmeaSettReMap = new HashMap<String, CnmeaSettRe>();
	private Map<String, Participant> participantMap = new HashMap<String, Participant>();
	private Map<String, Period> periodMap = new HashMap<String, Period>();
	private Map<String, Rerun> rerunMap = new HashMap<String, Rerun>();
	private Map<String, Reserve> reserveMap = new HashMap<String, Reserve>();
	private Map<String, List<Reserve>> nodeReserveMap = new HashMap<String, List<Reserve>>();
	private Map<String, RsvClass> rsvClassMap = new HashMap<String, RsvClass>();
	private Map<String, Tvc> tvcMap = new HashMap<String, Tvc>();
	private Map<String, Vesting> vestingMap = new HashMap<String, Vesting>();
	
	public int getRecordsCount() {
		int cnt = 1;
		
		cnt += accountMap.size();
		cnt += adjustmentMap.size();
		cnt += bilateralMap.size();
		cnt += brqMap.size();
		cnt += cnmeaMap.size();
		cnt += facilityMap.size();
		cnt += fscMap.size();
		cnt += ftrMap.size();
		cnt += marketMap.size();
		cnt += mnmeaMap.size();
		cnt += mnmeaSubMap.size();
		cnt += cnmeaSettReMap.size();
		cnt += participantMap.size();
		cnt += periodMap.size();
		cnt += rerunMap.size();
		cnt += reserveMap.size();
		cnt += rsvClassMap.size();
		cnt += tvcMap.size();
		cnt += vestingMap.size();
		
		return cnt;
	}
	
	public Global getGlobal() {
		return global;
	}
	
	public Map<String, Account> getAccount() {
		return accountMap;
	}
	
	public Map<String, Adjustment> getAdjustment() {
		return adjustmentMap;
	}
	
	public Map<String, Bilateral> getBilateral() {
		return bilateralMap;
	}
	
	public Map<String, Brq> getBrq() {
		return brqMap;
	}
	
	public Map<String, List<Brq>> getBrqBuyContract() {
		return brqBuyMap;
	}
	
	public Map<String, List<Brq>> getBrqSellerContract() {
		return brqSellMap;
	}
	
	public Map<String, Cnmea> getCnmea() {
		return cnmeaMap;
	}
	
	public Map<String, Facility> getFacility() {
		return facilityMap;
	}
	
	public Map<String, Fsc> getFsc() {
		return fscMap;
	}
	
	public Map<String, Ftr> getFtr() {
		return ftrMap;
	}
	
	public Map<String, Market> getMarket() {
		return marketMap;
	}
	
	public Map<String, Mnmea> getMnmea() {
		return mnmeaMap;
	}
	
	public Map<String, MnmeaSub> getMnmeaSub() {
		return mnmeaSubMap;
	}
	
	public Map<String, CnmeaSettRe> getCnmeaSettRe() {
		return cnmeaSettReMap;
	}
	
	public Map<String, Participant> getParticipant() {
		return participantMap;
	}
	
	public Map<String, Period> getPeriod() {
		return periodMap;
	}
	
	public Map<String, Rerun> getRerun() {
		return rerunMap;
	}
	
	public Map<String, Reserve> getReserve() {
		return reserveMap;
	}
	
	public Map<String, List<Reserve>> getNodeReserve() {
		return nodeReserveMap;
	}
	
	public Map<String, RsvClass> getRsvClass() {
		return rsvClassMap;
	}
	
	public Map<String, Tvc> getTVC() {
		return tvcMap;
	}
	
	public Map<String, Vesting> getVesting() {
		return vestingMap;
	}
}
