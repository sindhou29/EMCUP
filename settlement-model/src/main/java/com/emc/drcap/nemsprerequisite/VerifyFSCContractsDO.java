
package com.emc.drcap.nemsprerequisite;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for verifyFSCContractsDO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="verifyFSCContractsDO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="breachAccMsg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="msgStep" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "verifyFSCContractsDO", propOrder = {
    "breachAccMsg",
    "msgStep",
    "valid"
})
public class VerifyFSCContractsDO  implements java.io.Serializable{

    protected String breachAccMsg;
    protected String msgStep;
    protected Boolean valid;

    /**
     * Gets the value of the breachAccMsg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBreachAccMsg() {
        return breachAccMsg;
    }

    /**
     * Sets the value of the breachAccMsg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBreachAccMsg(String value) {
        this.breachAccMsg = value;
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
