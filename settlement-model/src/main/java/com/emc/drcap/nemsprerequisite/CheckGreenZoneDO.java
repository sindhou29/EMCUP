
package com.emc.drcap.nemsprerequisite;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for checkGreenZoneDO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="checkGreenZoneDO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="msgStep" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="resultGreenZone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "checkGreenZoneDO", propOrder = {
    "msgStep",
    "resultGreenZone"
})
public class CheckGreenZoneDO implements java.io.Serializable{

    protected String msgStep;
    protected String resultGreenZone;

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
     * Gets the value of the resultGreenZone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultGreenZone() {
        return resultGreenZone;
    }

    /**
     * Sets the value of the resultGreenZone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultGreenZone(String value) {
        this.resultGreenZone = value;
    }

}
