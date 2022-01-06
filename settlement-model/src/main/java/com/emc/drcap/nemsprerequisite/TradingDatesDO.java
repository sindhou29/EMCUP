
package com.emc.drcap.nemsprerequisite;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tradingDatesDO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tradingDatesDO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FTradingDateRange" type="{http://nemsprerequisite.drcap.emc.com/}tradingSingleDateDO" minOccurs="0"/>
 *         &lt;element name="msgStep" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PTradingDateRange" type="{http://nemsprerequisite.drcap.emc.com/}tradingSingleDateDO" minOccurs="0"/>
 *         &lt;element name="RTradingDateRange" type="{http://nemsprerequisite.drcap.emc.com/}tradingSingleDateDO" minOccurs="0"/>
 *         &lt;element name="STradingDateRange" type="{http://nemsprerequisite.drcap.emc.com/}tradingSingleDateDO" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tradingDatesDO", propOrder = {
    "fTradingDateRange",
    "msgStep",
    "pTradingDateRange",
    "rTradingDateRange",
    "sTradingDateRange"
})
public class TradingDatesDO  implements java.io.Serializable{

    @XmlElement(name = "FTradingDateRange")
    protected TradingSingleDateDO fTradingDateRange;
    protected String msgStep;
    @XmlElement(name = "PTradingDateRange")
    protected TradingSingleDateDO pTradingDateRange;
    @XmlElement(name = "RTradingDateRange")
    protected TradingSingleDateDO rTradingDateRange;
    @XmlElement(name = "STradingDateRange")
    protected TradingSingleDateDO sTradingDateRange;

    /**
     * Gets the value of the fTradingDateRange property.
     * 
     * @return
     *     possible object is
     *     {@link TradingSingleDateDO }
     *     
     */
    public TradingSingleDateDO getFTradingDateRange() {
        return fTradingDateRange;
    }

    /**
     * Sets the value of the fTradingDateRange property.
     * 
     * @param value
     *     allowed object is
     *     {@link TradingSingleDateDO }
     *     
     */
    public void setFTradingDateRange(TradingSingleDateDO value) {
        this.fTradingDateRange = value;
    }

    /**
     * Gets the value of the msgStep property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsgStep() {
        return msgStep;
    }

    /**
     * Sets the value of the msgStep property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsgStep(String value) {
        this.msgStep = value;
    }

    /**
     * Gets the value of the pTradingDateRange property.
     * 
     * @return
     *     possible object is
     *     {@link TradingSingleDateDO }
     *     
     */
    public TradingSingleDateDO getPTradingDateRange() {
        return pTradingDateRange;
    }

    /**
     * Sets the value of the pTradingDateRange property.
     * 
     * @param value
     *     allowed object is
     *     {@link TradingSingleDateDO }
     *     
     */
    public void setPTradingDateRange(TradingSingleDateDO value) {
        this.pTradingDateRange = value;
    }

    /**
     * Gets the value of the rTradingDateRange property.
     * 
     * @return
     *     possible object is
     *     {@link TradingSingleDateDO }
     *     
     */
    public TradingSingleDateDO getRTradingDateRange() {
        return rTradingDateRange;
    }

    /**
     * Sets the value of the rTradingDateRange property.
     * 
     * @param value
     *     allowed object is
     *     {@link TradingSingleDateDO }
     *     
     */
    public void setRTradingDateRange(TradingSingleDateDO value) {
        this.rTradingDateRange = value;
    }

    /**
     * Gets the value of the sTradingDateRange property.
     * 
     * @return
     *     possible object is
     *     {@link TradingSingleDateDO }
     *     
     */
    public TradingSingleDateDO getSTradingDateRange() {
        return sTradingDateRange;
    }

    /**
     * Sets the value of the sTradingDateRange property.
     * 
     * @param value
     *     allowed object is
     *     {@link TradingSingleDateDO }
     *     
     */
    public void setSTradingDateRange(TradingSingleDateDO value) {
        this.sTradingDateRange = value;
    }

}
