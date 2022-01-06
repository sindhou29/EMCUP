package com.emc.settlement.model.backend.pojo.fileupload;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UploadBilateralContract  implements Serializable{

	public UploadBilateralContract() {
		// TODO Auto-generated constructor stub
	}

	public String contractName;
	public String sellerAccount;
	public String buyerAccount;
	public String type;
	public String reserveGroup;
	public String startDate;
	public String endDate;
	public int period;
	public Double quantity;
	public String message;
	public List<String> validContractType;
	public String buyerSacId;
	public String sellerSacId;
	public String sacVersion;
	public String nodeId;
	public String frontendUserId;
	

	
	public String getContractName() {
		return contractName;
	}
	public void setContractName(String contractName) {
		this.contractName = contractName;
	}
	public String getSellerAccount() {
		return sellerAccount;
	}
	public void setSellerAccount(String sellerAccount) {
		this.sellerAccount = sellerAccount;
	}
	public String getBuyerAccount() {
		return buyerAccount;
	}
	public void setBuyerAccount(String buyerAccount) {
		this.buyerAccount = buyerAccount;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getReserveGroup() {
		return reserveGroup;
	}
	public void setReserveGroup(String reserveGroup) {
		this.reserveGroup = reserveGroup;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	public Double getQuantity() {
		return quantity;
	}
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<String> getValidContractType() {
		return validContractType;
	}
	public void setValidContractType(List<String> validContractType) {
		this.validContractType = validContractType;
	}
	public String getBuyerSacId() {
		return buyerSacId;
	}
	public void setBuyerSacId(String buyerSacId) {
		this.buyerSacId = buyerSacId;
	}
	public String getSellerSacId() {
		return sellerSacId;
	}
	public void setSellerSacId(String sellerSacId) {
		this.sellerSacId = sellerSacId;
	}
	public String getSacVersion() {
		return sacVersion;
	}
	public void setSacVersion(String sacVersion) {
		this.sacVersion = sacVersion;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getFrontendUserId() {
		return frontendUserId;
	}
	public void setFrontendUserId(String frontendUserId) {
		this.frontendUserId = frontendUserId;
	}
	
	
	public void initializeDbItem()
	{
		contractName = null;
		buyerAccount = null;
		sellerAccount = null;
		type = null;
		reserveGroup = null;
		startDate = null;
		endDate = null;
		period = 0;
		quantity = null;
		validContractType = new ArrayList<String>();
		validContractType.add("Energy");
		validContractType.add("Load");
		validContractType.add("Reserve");
		validContractType.add("Regulation");
		validContractType.add("Injection");
	}
}
