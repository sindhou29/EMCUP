
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
 *         &lt;element name="RunDate" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="comments" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="runType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="userId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
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
    "comments",
    "runType",
    "userId"
})
@XmlRootElement(name = "startREXRunByRunDate")
public class StartREXRunByRunDate {

    @XmlElement(name = "RunDate", required = true)
    protected String runDate;
    @XmlElement(required = true)
    protected String comments;
    @XmlElement(required = true)
    protected String runType;
    @XmlElement(required = true)
    protected String userId;

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
     * Gets the value of the comments property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComments() {
        return comments;
    }

    /**
     * Sets the value of the comments property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComments(String value) {
        this.comments = value;
    }

    /**
     * Gets the value of the runType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRunType() {
        return runType;
    }

    /**
     * Sets the value of the runType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRunType(String value) {
        this.runType = value;
    }

    /**
     * Gets the value of the userId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the value of the userId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserId(String value) {
        this.userId = value;
    }

}
