//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.03.01 at 03:42:28 PM SGT 
//


package com.emc.sett.model;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VestingT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VestingT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="accountId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="periodId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="contractName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="hp" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="hq" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="vcrp" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="vcsc" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VestingT", propOrder = {
    "accountId",
    "periodId",
    "contractName",
    "hp",
    "hq",
    "vcrp",
    "vcsc"
})
public class VestingT {

    @XmlElement(required = true)
    protected String accountId;
    @XmlElement(required = true)
    protected String periodId;
    @XmlElement(required = true)
    protected String contractName;
    @XmlElement(required = true)
    protected BigDecimal hp;
    @XmlElement(required = true)
    protected BigDecimal hq;
    @XmlElement(required = true)
    protected BigDecimal vcrp;
    @XmlElement(required = true)
    protected BigDecimal vcsc;

    /**
     * Gets the value of the accountId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * Sets the value of the accountId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountId(String value) {
        this.accountId = value;
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
     * Gets the value of the contractName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContractName() {
        return contractName;
    }

    /**
     * Sets the value of the contractName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContractName(String value) {
        this.contractName = value;
    }

    /**
     * Gets the value of the hp property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getHp() {
        return hp;
    }

    /**
     * Sets the value of the hp property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setHp(BigDecimal value) {
        this.hp = value;
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

}
