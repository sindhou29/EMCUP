//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.03.01 at 03:42:28 PM SGT 
//


package com.emc.sett.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MarketT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MarketT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="runId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="periodId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="reserves" type="{http://www.model.sett.emc.com}ReserveT" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="facilities" type="{http://www.model.sett.emc.com}FacilityT" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="wmepOutput" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="accountingNeaa" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="afp" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="egaWeq" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="emcAdm" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="feq" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="fsc" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="fsq" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="fsrp" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="fsrpfsq" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="fssc" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="fssck" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="heua" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="heuc" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="heur" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="hlcu" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="hpk" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="hq" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="lcsc" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="meuc" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="neaa" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="pcuCount" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="prq" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="psoAdm" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="roundedHeua" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="roundedHeuc" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="roundedHeur" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="roundedHlcu" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="rsa" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="rsc" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="srq" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="trq" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="tte" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="usep" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="vcrp" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="vcsc" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="wcq" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="wdq" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="weq" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="wmep" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="wmq" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="wsp" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MarketT", propOrder = {
    "runId",
    "periodId",
    "reserves",
    "facilities",
    "wmepOutput",
    "accountingNeaa",
    "afp",
    "egaWeq",
    "emcAdm",
    "feq",
    "fsc",
    "fsq",
    "fsrp",
    "fsrpfsq",
    "fssc",
    "fssck",
    "heua",
    "heuc",
    "heur",
    "hlcu",
    "hpk",
    "hq",
    "lcsc",
    "meuc",
    "neaa",
    "pcuCount",
    "prq",
    "psoAdm",
    "roundedHeua",
    "roundedHeuc",
    "roundedHeur",
    "roundedHlcu",
    "rsa",
    "rsc",
    "srq",
    "trq",
    "tte",
    "usep",
    "vcrp",
    "vcsc",
    "wcq",
    "wdq",
    "weq",
    "wmep",
    "wmq",
    "wsp"
})
public class MarketT {

    @XmlElement(required = true)
    protected String runId;
    @XmlElement(required = true)
    protected String periodId;
    protected List<ReserveT> reserves;
    protected List<FacilityT> facilities;
    @XmlElement(required = true)
    protected BigDecimal wmepOutput;
    @XmlElement(required = true)
    protected BigDecimal accountingNeaa;
    @XmlElement(required = true)
    protected BigDecimal afp;
    @XmlElement(required = true)
    protected BigDecimal egaWeq;
    @XmlElement(required = true)
    protected BigDecimal emcAdm;
    @XmlElement(required = true)
    protected BigDecimal feq;
    @XmlElement(required = true)
    protected BigDecimal fsc;
    @XmlElement(required = true)
    protected BigDecimal fsq;
    @XmlElement(required = true)
    protected BigDecimal fsrp;
    @XmlElement(required = true)
    protected BigDecimal fsrpfsq;
    @XmlElement(required = true)
    protected BigDecimal fssc;
    @XmlElement(required = true)
    protected BigDecimal fssck;
    @XmlElement(required = true)
    protected BigDecimal heua;
    @XmlElement(required = true)
    protected BigDecimal heuc;
    @XmlElement(required = true)
    protected BigDecimal heur;
    @XmlElement(required = true)
    protected BigDecimal hlcu;
    @XmlElement(required = true)
    protected BigDecimal hpk;
    @XmlElement(required = true)
    protected BigDecimal hq;
    @XmlElement(required = true)
    protected BigDecimal lcsc;
    @XmlElement(required = true)
    protected BigDecimal meuc;
    @XmlElement(required = true)
    protected BigDecimal neaa;
    @XmlElement(required = true)
    protected BigDecimal pcuCount;
    @XmlElement(required = true)
    protected BigDecimal prq;
    @XmlElement(required = true)
    protected BigDecimal psoAdm;
    @XmlElement(required = true)
    protected BigDecimal roundedHeua;
    @XmlElement(required = true)
    protected BigDecimal roundedHeuc;
    @XmlElement(required = true)
    protected BigDecimal roundedHeur;
    @XmlElement(required = true)
    protected BigDecimal roundedHlcu;
    @XmlElement(required = true)
    protected BigDecimal rsa;
    @XmlElement(required = true)
    protected BigDecimal rsc;
    @XmlElement(required = true)
    protected BigDecimal srq;
    @XmlElement(required = true)
    protected BigDecimal trq;
    @XmlElement(required = true)
    protected BigDecimal tte;
    @XmlElement(required = true)
    protected BigDecimal usep;
    @XmlElement(required = true)
    protected BigDecimal vcrp;
    @XmlElement(required = true)
    protected BigDecimal vcsc;
    @XmlElement(required = true)
    protected BigDecimal wcq;
    @XmlElement(required = true)
    protected BigDecimal wdq;
    @XmlElement(required = true)
    protected BigDecimal weq;
    @XmlElement(required = true)
    protected BigDecimal wmep;
    @XmlElement(required = true)
    protected BigDecimal wmq;
    @XmlElement(required = true)
    protected BigDecimal wsp;

    /**
     * Gets the value of the runId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRunId() {
        return runId;
    }

    /**
     * Sets the value of the runId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRunId(String value) {
        this.runId = value;
    }

    /**
     * Gets the value of the periodId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPeriodId() {
        return periodId;
    }

    /**
     * Sets the value of the periodId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPeriodId(String value) {
        this.periodId = value;
    }

    /**
     * Gets the value of the reserves property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reserves property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReserves().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReserveT }
     * 
     * 
     */
    public List<ReserveT> getReserves() {
        if (reserves == null) {
            reserves = new ArrayList<ReserveT>();
        }
        return this.reserves;
    }

    /**
     * Gets the value of the facilities property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the facilities property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFacilities().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FacilityT }
     * 
     * 
     */
    public List<FacilityT> getFacilities() {
        if (facilities == null) {
            facilities = new ArrayList<FacilityT>();
        }
        return this.facilities;
    }

    /**
     * Gets the value of the wmepOutput property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getWmepOutput() {
        return wmepOutput;
    }

    /**
     * Sets the value of the wmepOutput property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setWmepOutput(BigDecimal value) {
        this.wmepOutput = value;
    }

    /**
     * Gets the value of the accountingNeaa property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAccountingNeaa() {
        return accountingNeaa;
    }

    /**
     * Sets the value of the accountingNeaa property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAccountingNeaa(BigDecimal value) {
        this.accountingNeaa = value;
    }

    /**
     * Gets the value of the afp property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAfp() {
        return afp;
    }

    /**
     * Sets the value of the afp property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAfp(BigDecimal value) {
        this.afp = value;
    }

    /**
     * Gets the value of the egaWeq property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getEgaWeq() {
        return egaWeq;
    }

    /**
     * Sets the value of the egaWeq property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEgaWeq(BigDecimal value) {
        this.egaWeq = value;
    }

    /**
     * Gets the value of the emcAdm property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getEmcAdm() {
        return emcAdm;
    }

    /**
     * Sets the value of the emcAdm property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEmcAdm(BigDecimal value) {
        this.emcAdm = value;
    }

    /**
     * Gets the value of the feq property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getFeq() {
        return feq;
    }

    /**
     * Sets the value of the feq property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setFeq(BigDecimal value) {
        this.feq = value;
    }

    /**
     * Gets the value of the fsc property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getFsc() {
        return fsc;
    }

    /**
     * Sets the value of the fsc property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setFsc(BigDecimal value) {
        this.fsc = value;
    }

    /**
     * Gets the value of the fsq property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getFsq() {
        return fsq;
    }

    /**
     * Sets the value of the fsq property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setFsq(BigDecimal value) {
        this.fsq = value;
    }

    /**
     * Gets the value of the fsrp property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getFsrp() {
        return fsrp;
    }

    /**
     * Sets the value of the fsrp property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setFsrp(BigDecimal value) {
        this.fsrp = value;
    }

    /**
     * Gets the value of the fsrpfsq property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getFsrpfsq() {
        return fsrpfsq;
    }

    /**
     * Sets the value of the fsrpfsq property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setFsrpfsq(BigDecimal value) {
        this.fsrpfsq = value;
    }

    /**
     * Gets the value of the fssc property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getFssc() {
        return fssc;
    }

    /**
     * Sets the value of the fssc property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setFssc(BigDecimal value) {
        this.fssc = value;
    }

    /**
     * Gets the value of the fssck property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getFssck() {
        return fssck;
    }

    /**
     * Sets the value of the fssck property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setFssck(BigDecimal value) {
        this.fssck = value;
    }

    /**
     * Gets the value of the heua property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getHeua() {
        return heua;
    }

    /**
     * Sets the value of the heua property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setHeua(BigDecimal value) {
        this.heua = value;
    }

    /**
     * Gets the value of the heuc property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getHeuc() {
        return heuc;
    }

    /**
     * Sets the value of the heuc property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setHeuc(BigDecimal value) {
        this.heuc = value;
    }

    /**
     * Gets the value of the heur property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getHeur() {
        return heur;
    }

    /**
     * Sets the value of the heur property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setHeur(BigDecimal value) {
        this.heur = value;
    }

    /**
     * Gets the value of the hlcu property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getHlcu() {
        return hlcu;
    }

    /**
     * Sets the value of the hlcu property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setHlcu(BigDecimal value) {
        this.hlcu = value;
    }

    /**
     * Gets the value of the hpk property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getHpk() {
        return hpk;
    }

    /**
     * Sets the value of the hpk property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setHpk(BigDecimal value) {
        this.hpk = value;
    }

    /**
     * Gets the value of the hq property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getHq() {
        return hq;
    }

    /**
     * Sets the value of the hq property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setHq(BigDecimal value) {
        this.hq = value;
    }

    /**
     * Gets the value of the lcsc property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLcsc() {
        return lcsc;
    }

    /**
     * Sets the value of the lcsc property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLcsc(BigDecimal value) {
        this.lcsc = value;
    }

    /**
     * Gets the value of the meuc property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getMeuc() {
        return meuc;
    }

    /**
     * Sets the value of the meuc property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setMeuc(BigDecimal value) {
        this.meuc = value;
    }

    /**
     * Gets the value of the neaa property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getNeaa() {
        return neaa;
    }

    /**
     * Sets the value of the neaa property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setNeaa(BigDecimal value) {
        this.neaa = value;
    }

    /**
     * Gets the value of the pcuCount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPcuCount() {
        return pcuCount;
    }

    /**
     * Sets the value of the pcuCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPcuCount(BigDecimal value) {
        this.pcuCount = value;
    }

    /**
     * Gets the value of the prq property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPrq() {
        return prq;
    }

    /**
     * Sets the value of the prq property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPrq(BigDecimal value) {
        this.prq = value;
    }

    /**
     * Gets the value of the psoAdm property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPsoAdm() {
        return psoAdm;
    }

    /**
     * Sets the value of the psoAdm property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPsoAdm(BigDecimal value) {
        this.psoAdm = value;
    }

    /**
     * Gets the value of the roundedHeua property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getRoundedHeua() {
        return roundedHeua;
    }

    /**
     * Sets the value of the roundedHeua property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setRoundedHeua(BigDecimal value) {
        this.roundedHeua = value;
    }

    /**
     * Gets the value of the roundedHeuc property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getRoundedHeuc() {
        return roundedHeuc;
    }

    /**
     * Sets the value of the roundedHeuc property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setRoundedHeuc(BigDecimal value) {
        this.roundedHeuc = value;
    }

    /**
     * Gets the value of the roundedHeur property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getRoundedHeur() {
        return roundedHeur;
    }

    /**
     * Sets the value of the roundedHeur property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setRoundedHeur(BigDecimal value) {
        this.roundedHeur = value;
    }

    /**
     * Gets the value of the roundedHlcu property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getRoundedHlcu() {
        return roundedHlcu;
    }

    /**
     * Sets the value of the roundedHlcu property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setRoundedHlcu(BigDecimal value) {
        this.roundedHlcu = value;
    }

    /**
     * Gets the value of the rsa property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getRsa() {
        return rsa;
    }

    /**
     * Sets the value of the rsa property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setRsa(BigDecimal value) {
        this.rsa = value;
    }

    /**
     * Gets the value of the rsc property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getRsc() {
        return rsc;
    }

    /**
     * Sets the value of the rsc property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setRsc(BigDecimal value) {
        this.rsc = value;
    }

    /**
     * Gets the value of the srq property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSrq() {
        return srq;
    }

    /**
     * Sets the value of the srq property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSrq(BigDecimal value) {
        this.srq = value;
    }

    /**
     * Gets the value of the trq property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTrq() {
        return trq;
    }

    /**
     * Sets the value of the trq property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTrq(BigDecimal value) {
        this.trq = value;
    }

    /**
     * Gets the value of the tte property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTte() {
        return tte;
    }

    /**
     * Sets the value of the tte property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTte(BigDecimal value) {
        this.tte = value;
    }

    /**
     * Gets the value of the usep property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getUsep() {
        return usep;
    }

    /**
     * Sets the value of the usep property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setUsep(BigDecimal value) {
        this.usep = value;
    }

    /**
     * Gets the value of the vcrp property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getVcrp() {
        return vcrp;
    }

    /**
     * Sets the value of the vcrp property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setVcrp(BigDecimal value) {
        this.vcrp = value;
    }

    /**
     * Gets the value of the vcsc property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getVcsc() {
        return vcsc;
    }

    /**
     * Sets the value of the vcsc property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setVcsc(BigDecimal value) {
        this.vcsc = value;
    }

    /**
     * Gets the value of the wcq property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getWcq() {
        return wcq;
    }

    /**
     * Sets the value of the wcq property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setWcq(BigDecimal value) {
        this.wcq = value;
    }

    /**
     * Gets the value of the wdq property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getWdq() {
        return wdq;
    }

    /**
     * Sets the value of the wdq property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setWdq(BigDecimal value) {
        this.wdq = value;
    }

    /**
     * Gets the value of the weq property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getWeq() {
        return weq;
    }

    /**
     * Sets the value of the weq property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setWeq(BigDecimal value) {
        this.weq = value;
    }

    /**
     * Gets the value of the wmep property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getWmep() {
        return wmep;
    }

    /**
     * Sets the value of the wmep property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setWmep(BigDecimal value) {
        this.wmep = value;
    }

    /**
     * Gets the value of the wmq property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getWmq() {
        return wmq;
    }

    /**
     * Sets the value of the wmq property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setWmq(BigDecimal value) {
        this.wmq = value;
    }

    /**
     * Gets the value of the wsp property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getWsp() {
        return wsp;
    }

    /**
     * Sets the value of the wsp property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setWsp(BigDecimal value) {
        this.wsp = value;
    }

}
