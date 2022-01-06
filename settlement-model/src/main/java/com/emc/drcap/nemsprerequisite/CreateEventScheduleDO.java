
package com.emc.drcap.nemsprerequisite;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for createEventScheduleDO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="createEventScheduleDO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mainEventId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="msgStep" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pd_avg3" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="pd_sum" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="pd_sum2" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="pd_total" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="schEventId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createEventScheduleDO", propOrder = {
    "mainEventId",
    "msgStep",
    "pdAvg3",
    "pdSum",
    "pdSum2",
    "pdTotal",
    "schEventId"
})
public class CreateEventScheduleDO  implements java.io.Serializable{

    protected String mainEventId;
    protected String msgStep;
    @XmlElement(name = "pd_avg3")
    protected int pdAvg3;
    @XmlElement(name = "pd_sum")
    protected int pdSum;
    @XmlElement(name = "pd_sum2")
    protected int pdSum2;
    @XmlElement(name = "pd_total")
    protected int pdTotal;
    protected String schEventId;

    /**
     * Gets the value of the mainEventId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMainEventId() {
        return mainEventId;
    }

    /**
     * Sets the value of the mainEventId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMainEventId(String value) {
        this.mainEventId = value;
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
     * Gets the value of the pdAvg3 property.
     * 
     */
    public int getPdAvg3() {
        return pdAvg3;
    }

    /**
     * Sets the value of the pdAvg3 property.
     * 
     */
    public void setPdAvg3(int value) {
        this.pdAvg3 = value;
    }

    /**
     * Gets the value of the pdSum property.
     * 
     */
    public int getPdSum() {
        return pdSum;
    }

    /**
     * Sets the value of the pdSum property.
     * 
     */
    public void setPdSum(int value) {
        this.pdSum = value;
    }

    /**
     * Gets the value of the pdSum2 property.
     * 
     */
    public int getPdSum2() {
        return pdSum2;
    }

    /**
     * Sets the value of the pdSum2 property.
     * 
     */
    public void setPdSum2(int value) {
        this.pdSum2 = value;
    }

    /**
     * Gets the value of the pdTotal property.
     * 
     */
    public int getPdTotal() {
        return pdTotal;
    }

    /**
     * Sets the value of the pdTotal property.
     * 
     */
    public void setPdTotal(int value) {
        this.pdTotal = value;
    }

    /**
     * Gets the value of the schEventId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSchEventId() {
        return schEventId;
    }

    /**
     * Sets the value of the schEventId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSchEventId(String value) {
        this.schEventId = value;
    }

}
