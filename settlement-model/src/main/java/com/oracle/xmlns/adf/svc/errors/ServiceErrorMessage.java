
package com.oracle.xmlns.adf.svc.errors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ServiceErrorMessage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServiceErrorMessage"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://xmlns.oracle.com/adf/svc/errors/}ServiceMessage"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sdoObject" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/&gt;
 *         &lt;element name="exceptionClassName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceErrorMessage", propOrder = {
    "sdoObject",
    "exceptionClassName"
})
@XmlSeeAlso({
    ServiceAttrValErrorMessage.class,
    ServiceRowValErrorMessage.class,
    ServiceDMLErrorMessage.class
})
public class ServiceErrorMessage
    extends ServiceMessage
{

    protected Object sdoObject;
    protected String exceptionClassName;

    /**
     * Gets the value of the sdoObject property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getSdoObject() {
        return sdoObject;
    }

    /**
     * Sets the value of the sdoObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setSdoObject(Object value) {
        this.sdoObject = value;
    }

    /**
     * Gets the value of the exceptionClassName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExceptionClassName() {
        return exceptionClassName;
    }

    /**
     * Sets the value of the exceptionClassName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExceptionClassName(String value) {
        this.exceptionClassName = value;
    }

}
