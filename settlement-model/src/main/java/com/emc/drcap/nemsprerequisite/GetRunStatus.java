
package com.emc.drcap.nemsprerequisite;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for getRunStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getRunStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="controllerParam_TradingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="logPrefix" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="controllerParam_RunFrom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="controllerParam_RunType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="controllerParam_MainEveId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="controllerParam_RunEventId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="schemeId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getRunStatus", propOrder = {
    "controllerParamTradingDate",
    "logPrefix",
    "controllerParamRunFrom",
    "controllerParamRunType",
    "controllerParamMainEveId",
    "controllerParamRunEventId",
    "schemeId"
})
public class GetRunStatus  implements java.io.Serializable{

    @XmlElement(name = "controllerParam_TradingDate")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar controllerParamTradingDate;
    protected String logPrefix;
    @XmlElement(name = "controllerParam_RunFrom")
    protected String controllerParamRunFrom;
    @XmlElement(name = "controllerParam_RunType")
    protected String controllerParamRunType;
    @XmlElement(name = "controllerParam_MainEveId")
    protected String controllerParamMainEveId;
    @XmlElement(name = "controllerParam_RunEventId")
    protected String controllerParamRunEventId;
    protected String schemeId;

    /**
     * Gets the value of the controllerParamTradingDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getControllerParamTradingDate() {
        return controllerParamTradingDate;
    }

    /**
     * Sets the value of the controllerParamTradingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setControllerParamTradingDate(XMLGregorianCalendar value) {
        this.controllerParamTradingDate = value;
    }

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
     * Gets the value of the controllerParamRunFrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getControllerParamRunFrom() {
        return controllerParamRunFrom;
    }

    /**
     * Sets the value of the controllerParamRunFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setControllerParamRunFrom(String value) {
        this.controllerParamRunFrom = value;
    }

    /**
     * Gets the value of the controllerParamRunType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getControllerParamRunType() {
        return controllerParamRunType;
    }

    /**
     * Sets the value of the controllerParamRunType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setControllerParamRunType(String value) {
        this.controllerParamRunType = value;
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
     * Gets the value of the controllerParamRunEventId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getControllerParamRunEventId() {
        return controllerParamRunEventId;
    }

    /**
     * Sets the value of the controllerParamRunEventId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setControllerParamRunEventId(String value) {
        this.controllerParamRunEventId = value;
    }

    /**
     * Gets the value of the schemeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSchemeId() {
        return schemeId;
    }

    /**
     * Sets the value of the schemeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSchemeId(String value) {
        this.schemeId = value;
    }

}
