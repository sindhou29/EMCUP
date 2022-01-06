
package com.emc.drcap.nemsprerequisite;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for compareMCRDataDO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="compareMCRDataDO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="clwqExists" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="mcrChange" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="mcrIds" type="{http://nemsprerequisite.drcap.emc.com/}stringListList" minOccurs="0"/>
 *         &lt;element name="mcrStringNew" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mcrStringOld" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "compareMCRDataDO", propOrder = {
    "clwqExists",
    "mcrChange",
    "mcrIds",
    "mcrStringNew",
    "mcrStringOld",
    "msgStep"
})
public class CompareMCRDataDO implements java.io.Serializable{

    protected boolean clwqExists;
    protected boolean mcrChange;
    protected StringListList mcrIds;
    protected String mcrStringNew;
    protected String mcrStringOld;
    protected String msgStep;

    /**
     * Gets the value of the clwqExists property.
     * 
     */
    public boolean isClwqExists() {
        return clwqExists;
    }

    /**
     * Sets the value of the clwqExists property.
     * 
     */
    public void setClwqExists(boolean value) {
        this.clwqExists = value;
    }

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
     * Gets the value of the mcrIds property.
     * 
     * @return
     *     possible object is
     *     {@link StringListList }
     *     
     */
    public StringListList getMcrIds() {
        return mcrIds;
    }

    /**
     * Sets the value of the mcrIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link StringListList }
     *     
     */
    public void setMcrIds(StringListList value) {
        this.mcrIds = value;
    }

    /**
     * Gets the value of the mcrStringNew property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMcrStringNew() {
        return mcrStringNew;
    }

    /**
     * Sets the value of the mcrStringNew property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMcrStringNew(String value) {
        this.mcrStringNew = value;
    }

    /**
     * Gets the value of the mcrStringOld property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMcrStringOld() {
        return mcrStringOld;
    }

    /**
     * Sets the value of the mcrStringOld property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMcrStringOld(String value) {
        this.mcrStringOld = value;
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
