
package com.emc.drcap.nemsprerequisite;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for tradingSingleDateDO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tradingSingleDateDO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="controllerParam_FromTradingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="controllerParam_ToTradingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="controllerParam_TradingdDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="count" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="treadingDays" type="{http://www.w3.org/2001/XMLSchema}dateTime" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tradingSingleDateDO", propOrder = {
    "controllerParamFromTradingDate",
    "controllerParamToTradingDate",
    "controllerParamTradingdDate",
    "count",
    "treadingDays"
})
public class TradingSingleDateDO  implements java.io.Serializable{

    @XmlElement(name = "controllerParam_FromTradingDate")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar controllerParamFromTradingDate;
    @XmlElement(name = "controllerParam_ToTradingDate")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar controllerParamToTradingDate;
    @XmlElement(name = "controllerParam_TradingdDate")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar controllerParamTradingdDate;
    protected int count;
    @XmlElement(nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected List<XMLGregorianCalendar> treadingDays;

    /**
     * Gets the value of the controllerParamFromTradingDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getControllerParamFromTradingDate() {
        return controllerParamFromTradingDate;
    }

    /**
     * Sets the value of the controllerParamFromTradingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setControllerParamFromTradingDate(XMLGregorianCalendar value) {
        this.controllerParamFromTradingDate = value;
    }

    /**
     * Gets the value of the controllerParamToTradingDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getControllerParamToTradingDate() {
        return controllerParamToTradingDate;
    }

    /**
     * Sets the value of the controllerParamToTradingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setControllerParamToTradingDate(XMLGregorianCalendar value) {
        this.controllerParamToTradingDate = value;
    }

    /**
     * Gets the value of the controllerParamTradingdDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getControllerParamTradingdDate() {
        return controllerParamTradingdDate;
    }

    /**
     * Sets the value of the controllerParamTradingdDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setControllerParamTradingdDate(XMLGregorianCalendar value) {
        this.controllerParamTradingdDate = value;
    }

    /**
     * Gets the value of the count property.
     * 
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets the value of the count property.
     * 
     */
    public void setCount(int value) {
        this.count = value;
    }

    /**
     * Gets the value of the treadingDays property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the treadingDays property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTreadingDays().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XMLGregorianCalendar }
     * 
     * 
     */
    public List<XMLGregorianCalendar> getTreadingDays() {
        if (treadingDays == null) {
            treadingDays = new ArrayList<XMLGregorianCalendar>();
        }
        return this.treadingDays;
    }

    public void setTreadingDays(List<XMLGregorianCalendar> treadingDays) {
        this.treadingDays = treadingDays;
    }

}
