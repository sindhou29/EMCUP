
package com.emc.drcap.nemsprerequisite;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for checkBusinessDayDO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="checkBusinessDayDO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="bizDayResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
@XmlType(name = "checkBusinessDayDO", propOrder = {
    "bizDayResult",
    "msgStep"
})
public class CheckBusinessDayDO implements java.io.Serializable{

    protected boolean bizDayResult;
    protected String msgStep;

    /**
     * Gets the value of the bizDayResult property.
     * 
     */
    public boolean isBizDayResult() {
        return bizDayResult;
    }

    /**
     * Sets the value of the bizDayResult property.
     * 
     */
    public void setBizDayResult(boolean value) {
        this.bizDayResult = value;
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
