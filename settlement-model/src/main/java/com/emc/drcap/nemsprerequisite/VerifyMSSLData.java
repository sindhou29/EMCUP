
package com.emc.drcap.nemsprerequisite;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for verifyMSSLData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="verifyMSSLData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="logPrefix" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="controllerParam_mainEveId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="standingVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pd_Total" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="pd_Sum" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="pd_Sum2" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="pd_Avg3" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="tradingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="msslQuantityVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="valid" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "verifyMSSLData", propOrder = {
    "logPrefix",
    "controllerParamMainEveId",
    "standingVersion",
    "pdTotal",
    "pdSum",
    "pdSum2",
    "pdAvg3",
    "tradingDate",
    "msslQuantityVersion",
    "valid"
})
public class VerifyMSSLData  implements java.io.Serializable{

    protected String logPrefix;
    @XmlElement(name = "controllerParam_mainEveId")
    protected String controllerParamMainEveId;
    protected String standingVersion;
    @XmlElement(name = "pd_Total")
    protected int pdTotal;
    @XmlElement(name = "pd_Sum")
    protected int pdSum;
    @XmlElement(name = "pd_Sum2")
    protected int pdSum2;
    @XmlElement(name = "pd_Avg3")
    protected int pdAvg3;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar tradingDate;
    protected String msslQuantityVersion;
    protected Boolean valid;

    /**
     * Gets the value of the logPrefix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLogPrefix() {
        return logPrefix;
    }

    /**
     * Sets the value of the logPrefix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLogPrefix(String value) {
        this.logPrefix = value;
    }

    /**
     * Gets the value of the controllerParamMainEveId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getControllerParamMainEveId() {
        return controllerParamMainEveId;
    }

    /**
     * Sets the value of the controllerParamMainEveId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setControllerParamMainEveId(String value) {
        this.controllerParamMainEveId = value;
    }

    /**
     * Gets the value of the standingVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStandingVersion() {
        return standingVersion;
    }

    /**
     * Sets the value of the standingVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStandingVersion(String value) {
        this.standingVersion = value;
    }

    /**
     * Gets the value of the pdTotal property.
     * 
     */
    public int getPdTotal() {
        return pdTotal;
    }

    /**
     * Sets the value of the pdTotal property.
     * 
     */
    public void setPdTotal(int value) {
        this.pdTotal = value;
    }

    /**
     * Gets the value of the pdSum property.
     * 
     */
    public int getPdSum() {
        return pdSum;
    }

    /**
     * Sets the value of the pdSum property.
     * 
     */
    public void setPdSum(int value) {
        this.pdSum = value;
    }

    /**
     * Gets the value of the pdSum2 property.
     * 
     */
    public int getPdSum2() {
        return pdSum2;
    }

    /**
     * Sets the value of the pdSum2 property.
     * 
     */
    public void setPdSum2(int value) {
        this.pdSum2 = value;
    }

    /**
     * Gets the value of the pdAvg3 property.
     * 
     */
    public int getPdAvg3() {
        return pdAvg3;
    }

    /**
     * Sets the value of the pdAvg3 property.
     * 
     */
    public void setPdAvg3(int value) {
        this.pdAvg3 = value;
    }

    /**
     * Gets the value of the tradingDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTradingDate() {
        return tradingDate;
    }

    /**
     * Sets the value of the tradingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTradingDate(XMLGregorianCalendar value) {
        this.tradingDate = value;
    }

    /**
     * Gets the value of the msslQuantityVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsslQuantityVersion() {
        return msslQuantityVersion;
    }

    /**
     * Sets the value of the msslQuantityVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsslQuantityVersion(String value) {
        this.msslQuantityVersion = value;
    }

    /**
     * Gets the value of the valid property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isValid() {
        return valid;
    }

    /**
     * Sets the value of the valid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setValid(Boolean value) {
        this.valid = value;
    }

}
