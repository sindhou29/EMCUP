//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.03.04 at 02:14:54 PM SGT 
//


package com.emc.sett.model;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TvcT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TvcT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="accountId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="periodId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="contractName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tvp" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="tvq" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
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
@XmlType(name = "TvcT", propOrder = {
    "accountId",
    "periodId",
    "contractName",
    "tvp",
    "tvq",
    "vcrp",
    "vcsc"
})
public class TvcT {

    @XmlElement(required = true)
    protected String accountId;
    @XmlElement(required = true)
    protected String periodId;
    @XmlElement(required = true)
    protected String contractName;
    @XmlElement(required = true)
    protected BigDecimal tvp;
    @XmlElement(required = true)
    protected BigDecimal tvq;
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
     * Gets the value of the tvp property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTvp() {
        return tvp;
    }

    /**
     * Sets the value of the tvp property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTvp(BigDecimal value) {
        this.tvp = value;
    }

    /**
     * Gets the value of the tvq property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTvq() {
        return tvq;
    }

    /**
     * Sets the value of the tvq property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTvq(BigDecimal value) {
        this.tvq = value;
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
