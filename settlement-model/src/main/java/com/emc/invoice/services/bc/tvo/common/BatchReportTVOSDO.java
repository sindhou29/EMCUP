
package com.emc.invoice.services.bc.tvo.common;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for BatchReportTVOSDO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BatchReportTVOSDO"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CustomerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DocDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DueDate" type="{http://xmlns.oracle.com/adf/svc/types/}dateTime-Timestamp" minOccurs="0"/&gt;
 *         &lt;element name="Amt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TotalAmt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Gst" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CustomerId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="InvoiceNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DueDateString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TradingDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AuditDueDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BatchReportTVOSDO", propOrder = {
    "customerName",
    "docDesc",
    "dueDate",
    "amt",
    "totalAmt",
    "gst",
    "customerId",
    "invoiceNumber",
    "dueDateString",
    "tradingDate",
    "auditDueDate"
})
public class BatchReportTVOSDO {

    @XmlElementRef(name = "CustomerName", namespace = "/com/emc/invoice/services/bc/tvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> customerName;
    @XmlElementRef(name = "DocDesc", namespace = "/com/emc/invoice/services/bc/tvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> docDesc;
    @XmlElementRef(name = "DueDate", namespace = "/com/emc/invoice/services/bc/tvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dueDate;
    @XmlElementRef(name = "Amt", namespace = "/com/emc/invoice/services/bc/tvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> amt;
    @XmlElementRef(name = "TotalAmt", namespace = "/com/emc/invoice/services/bc/tvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> totalAmt;
    @XmlElementRef(name = "Gst", namespace = "/com/emc/invoice/services/bc/tvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> gst;
    @XmlElementRef(name = "CustomerId", namespace = "/com/emc/invoice/services/bc/tvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> customerId;
    @XmlElementRef(name = "InvoiceNumber", namespace = "/com/emc/invoice/services/bc/tvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> invoiceNumber;
    @XmlElementRef(name = "DueDateString", namespace = "/com/emc/invoice/services/bc/tvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> dueDateString;
    @XmlElementRef(name = "TradingDate", namespace = "/com/emc/invoice/services/bc/tvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> tradingDate;
    @XmlElementRef(name = "AuditDueDate", namespace = "/com/emc/invoice/services/bc/tvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> auditDueDate;

    /**
     * Gets the value of the customerName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCustomerName() {
        return customerName;
    }

    /**
     * Sets the value of the customerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCustomerName(JAXBElement<String> value) {
        this.customerName = value;
    }

    /**
     * Gets the value of the docDesc property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDocDesc() {
        return docDesc;
    }

    /**
     * Sets the value of the docDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDocDesc(JAXBElement<String> value) {
        this.docDesc = value;
    }

    /**
     * Gets the value of the dueDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDueDate() {
        return dueDate;
    }

    /**
     * Sets the value of the dueDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDueDate(JAXBElement<XMLGregorianCalendar> value) {
        this.dueDate = value;
    }

    /**
     * Gets the value of the amt property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAmt() {
        return amt;
    }

    /**
     * Sets the value of the amt property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAmt(JAXBElement<String> value) {
        this.amt = value;
    }

    /**
     * Gets the value of the totalAmt property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTotalAmt() {
        return totalAmt;
    }

    /**
     * Sets the value of the totalAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTotalAmt(JAXBElement<String> value) {
        this.totalAmt = value;
    }

    /**
     * Gets the value of the gst property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getGst() {
        return gst;
    }

    /**
     * Sets the value of the gst property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setGst(JAXBElement<String> value) {
        this.gst = value;
    }

    /**
     * Gets the value of the customerId property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCustomerId() {
        return customerId;
    }

    /**
     * Sets the value of the customerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCustomerId(JAXBElement<String> value) {
        this.customerId = value;
    }

    /**
     * Gets the value of the invoiceNumber property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * Sets the value of the invoiceNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setInvoiceNumber(JAXBElement<String> value) {
        this.invoiceNumber = value;
    }

    /**
     * Gets the value of the dueDateString property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDueDateString() {
        return dueDateString;
    }

    /**
     * Sets the value of the dueDateString property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDueDateString(JAXBElement<String> value) {
        this.dueDateString = value;
    }

    /**
     * Gets the value of the tradingDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTradingDate() {
        return tradingDate;
    }

    /**
     * Sets the value of the tradingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTradingDate(JAXBElement<String> value) {
        this.tradingDate = value;
    }

    /**
     * Gets the value of the auditDueDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAuditDueDate() {
        return auditDueDate;
    }

    /**
     * Sets the value of the auditDueDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAuditDueDate(JAXBElement<String> value) {
        this.auditDueDate = value;
    }

}
