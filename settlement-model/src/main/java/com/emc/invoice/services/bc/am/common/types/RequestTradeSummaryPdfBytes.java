
package com.emc.invoice.services.bc.am.common.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="runId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="tradingDate" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="ptpId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="stlAccountName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "runId",
    "tradingDate",
    "ptpId",
    "stlAccountName"
})
@XmlRootElement(name = "requestTradeSummaryPdfBytes")
public class RequestTradeSummaryPdfBytes {

    @XmlElement(required = true)
    protected String runId;
    @XmlElement(required = true)
    protected String tradingDate;
    @XmlElement(required = true)
    protected String ptpId;
    @XmlElement(required = true)
    protected String stlAccountName;

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
     * Gets the value of the tradingDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTradingDate() {
        return tradingDate;
    }

    /**
     * Sets the value of the tradingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTradingDate(String value) {
        this.tradingDate = value;
    }

    /**
     * Gets the value of the ptpId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPtpId() {
        return ptpId;
    }

    /**
     * Sets the value of the ptpId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPtpId(String value) {
        this.ptpId = value;
    }

    /**
     * Gets the value of the stlAccountName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStlAccountName() {
        return stlAccountName;
    }

    /**
     * Sets the value of the stlAccountName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStlAccountName(String value) {
        this.stlAccountName = value;
    }

}
