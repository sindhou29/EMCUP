
package com.emc.drcap.penalty.model.bc.am.common.types;

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
 *         &lt;element name="uploadEventId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
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
    "uploadEventId"
})
@XmlRootElement(name = "approveOrRejectAfpsifFile")
public class ApproveOrRejectAfpsifFile {

    @XmlElement(required = true)
    protected String uploadEventId;

    /**
     * Gets the value of the uploadEventId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUploadEventId() {
        return uploadEventId;
    }

    /**
     * Sets the value of the uploadEventId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUploadEventId(String value) {
        this.uploadEventId = value;
    }

}
