
package com.emc.invoice.services.bc.am.common.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


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
 *         &lt;element name="tradingDate" type="{http://xmlns.oracle.com/adf/svc/types/}dateTime-Timestamp"/&gt;
 *         &lt;element name="runId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="schemeType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="isTestRun" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
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
    "tradingDate",
    "runId",
    "schemeType",
    "isTestRun"
})
@XmlRootElement(name = "exposeEFTGeneration")
public class ExposeEFTGeneration {

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar tradingDate;
    @XmlElement(required = true)
    protected String runId;
    @XmlElement(required = true)
    protected String schemeType;
    protected boolean isTestRun;

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
     * Gets the value of the schemeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSchemeType() {
        return schemeType;
    }

    /**
     * Sets the value of the schemeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSchemeType(String value) {
        this.schemeType = value;
    }

    /**
     * Gets the value of the isTestRun property.
     * 
     */
    public boolean isIsTestRun() {
        return isTestRun;
    }

    /**
     * Sets the value of the isTestRun property.
     * 
     */
    public void setIsTestRun(boolean value) {
        this.isTestRun = value;
    }

}
