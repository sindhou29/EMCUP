
package com.emc.drcap.nemsprerequisite;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for registerEventRunDO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="registerEventRunDO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="controllerParam_runEveId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="msgStep" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="srInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "registerEventRunDO", propOrder = {
    "controllerParamRunEveId",
    "msgStep",
    "srInfo"
})
public class RegisterEventRunDO implements java.io.Serializable{

    @XmlElement(name = "controllerParam_runEveId")
    protected String controllerParamRunEveId;
    protected String msgStep;
    protected String srInfo;

    /**
     * Gets the value of the controllerParamRunEveId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getControllerParamRunEveId() {
        return controllerParamRunEveId;
    }

    /**
     * Sets the value of the controllerParamRunEveId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setControllerParamRunEveId(String value) {
        this.controllerParamRunEveId = value;
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
     * Gets the value of the srInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSrInfo() {
        return srInfo;
    }

    /**
     * Sets the value of the srInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSrInfo(String value) {
        this.srInfo = value;
    }

}
