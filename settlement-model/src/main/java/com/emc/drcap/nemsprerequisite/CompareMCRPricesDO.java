
package com.emc.drcap.nemsprerequisite;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for compareMCRPricesDO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="compareMCRPricesDO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mcrChange" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="msgStep" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "compareMCRPricesDO", propOrder = {
    "mcrChange",
    "msgStep"
})
public class CompareMCRPricesDO implements java.io.Serializable{

    protected boolean mcrChange;
    protected String msgStep;

    /**
     * Gets the value of the mcrChange property.
     * 
     */
    public boolean isMcrChange() {
        return mcrChange;
    }

    /**
     * Sets the value of the mcrChange property.
     * 
     */
    public void setMcrChange(boolean value) {
        this.mcrChange = value;
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

}
