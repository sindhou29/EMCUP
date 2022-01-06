package com.emc.sett.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.emc.sett.common.AbstractSettlementData;

public class SettlementData extends AbstractSettlementData {

	private Global global = new Global();
	private Map<String, Account> AccountMap = new HashMap<String, Account>();
	private Map<String, Adjustment> AdjustmentMap = new HashMap<String, Adjustment>();
	private Map<String, Bilateral> BilateralMap = new HashMap<String, Bilateral>();
	private Map<String, Brq> BrqMap = new HashMap<String, Brq>();
	private Map<String, List<Brq>> BrqBuyMap = new HashMap<String, List<Brq>>();
	private Map<String, List<Brq>> BrqSellMap = new HashMap<String, List<Brq>>();
	private Map<String, Cnmea> CnmeaMap = new HashMap<String, Cnmea>();
	private Map<String, Facility> FacilityMap = new HashMap<String, Facility>();
	private Map<String, Fsc> FscMap = new HashMap<String, Fsc>();
	private Map<String, Ftr> FtrMap = new HashMap<String, Ftr>();
	private Map<String, Market> MarketMap = new HashMap<String, Market>();
	private Map<String, Mnmea> MnmeaMap = new HashMap<String, Mnmea>();
	private Map<String, MnmeaSub> MnmeaSubMap = new HashMap<String, MnmeaSub>();
	private Map<String, CnmeaSettRe> CnmeaSettReMap = new HashMap<String, CnmeaSettRe>();
	private Map<String, Participant> ParticipantMap = new HashMap<String, Participant>();
	private Map<String, Period> PeriodMap = new HashMap<String, Period>();
	private Map<String, Rerun> RerunMap = new HashMap<String, Rerun>();
	private Map<String, Reserve> ReserveMap = new HashMap<String, Reserve>();
	private Map<String, List<Reserve>> NodeReserveMap = new HashMap<String, List<Reserve>>();
	private Map<String, RsvClass> RsvClassMap = new HashMap<String, RsvClass>();
	private Map<String, Tvc> TvcMap = new HashMap<String, Tvc>();
	private Map<String, Vesting> VestingMap = new HashMap<String, Vesting>();
	
	public int getRecordsCount() {
		int cnt = 1;
		
		cnt += AccountMap.size();
		cnt += AdjustmentMap.size();
		cnt += BilateralMap.size();
		cnt += BrqMap.size();
		cnt += CnmeaMap.size();
		cnt += FacilityMap.size();
		cnt += FscMap.size();
		cnt += FtrMap.size();
		cnt += MarketMap.size();
		cnt += MnmeaMap.size();
		cnt += MnmeaSubMap.size();
		cnt += CnmeaSettReMap.size();
		cnt += ParticipantMap.size();
		cnt += PeriodMap.size();
		cnt += RerunMap.size();
		cnt += ReserveMap.size();
		cnt += RsvClassMap.size();
		cnt += TvcMap.size();
		cnt += VestingMap.size();
		
		return cnt;
	}
	
	public Global getGlobal() {
		return global;
	}
	
	public Map<String, Account> getAccount() {
		return AccountMap;
	}
	
	public Map<String, Adjustment> getAdjustment() {
		return AdjustmentMap;
	}
	
	public Map<String, Bilateral> getBilateral() {
		return BilateralMap;
	}
	
	public Map<String, Brq> getBrq() {
		return BrqMap;
	}
	
	public Map<String, List<Brq>> getBrqBuyContract() {
		return BrqBuyMap;
	}
	
	public Map<String, List<Brq>> getBrqSellerContract() {
		return BrqSellMap;
	}
	
	public Map<String, Cnmea> getCnmea() {
		return CnmeaMap;
	}
	
	public Map<String, Facility> getFacility() {
		return FacilityMap;
	}
	
	public Map<String, Fsc> getFsc() {
		return FscMap;
	}
	
	public Map<String, Ftr> getFtr() {
		return FtrMap;
	}
	
	public Map<String, Market> getMarket() {
		return MarketMap;
	}
	
	public Map<String, Mnmea> getMnmea() {
		return MnmeaMap;
	}
	
	public Map<String, MnmeaSub> getMnmeaSub() {
		return MnmeaSubMap;
	}
	
	public Map<String, CnmeaSettRe> getCnmeaSettRe() {
		return CnmeaSettReMap;
	}
	
	public Map<String, Participant> getParticipant() {
		return ParticipantMap;
	}
	
	public Map<String, Period> getPeriod() {
		return PeriodMap;
	}
	
	public Map<String, Rerun> getRerun() {
		return RerunMap;
	}
	
	public Map<String, Reserve> getReserve() {
		return ReserveMap;
	}
	
	public Map<String, List<Reserve>> getNodeReserve() {
		return NodeReserveMap;
	}
	
	public Map<String, RsvClass> getRsvClass() {
		return RsvClassMap;
	}
	
	public Map<String, Tvc> getTVC() {
		return TvcMap;
	}
	
	public Map<String, Vesting> getVesting() {
		return VestingMap;
	}
}
