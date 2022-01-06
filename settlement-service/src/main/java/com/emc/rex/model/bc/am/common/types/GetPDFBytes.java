
package com.emc.rex.model.bc.am.common.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="runDate" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="ptpId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "runDate",
    "ptpId"
})
@XmlRootElement(name = "getPDFBytes")
public class GetPDFBytes {

    @XmlElement(required = true)
    protected String runDate;
    @XmlElement(required = true)
    protected String ptpId;

    /**
     * Gets the value of the runDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRunDate() {
        return runDate;
    }

    /**
     * Sets the value of the runDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRunDate(String value) {
        this.runDate = value;
    }

    /**
     * Gets the value of the ptpId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPtpId() {
        return ptpId;
    }

    /**
     * Sets the value of the ptpId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPtpId(String value) {
        this.ptpId = value;
    }

}
