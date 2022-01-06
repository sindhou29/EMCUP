
package com.emc.drcap.nemsprerequisite;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for shareplexValidationDO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="shareplexValidationDO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="backUpMode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shareplexValidationDO", propOrder = {
    "backUpMode"
})
public class ShareplexValidationDO implements Serializable{

    protected String backUpMode;

    /**
     * Gets the value of the backUpMode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBackUpMode() {
        return backUpMode;
    }

    /**
     * Sets the value of the backUpMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBackUpMode(String value) {
        this.backUpMode = value;
    }

}
