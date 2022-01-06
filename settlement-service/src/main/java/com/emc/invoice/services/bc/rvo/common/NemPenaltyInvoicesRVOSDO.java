
package com.emc.invoice.services.bc.rvo.common;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for NemPenaltyInvoicesRVOSDO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NemPenaltyInvoicesRVOSDO"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TradingDate" type="{http://xmlns.oracle.com/adf/svc/types/}date-Date" minOccurs="0"/&gt;
 *         &lt;element name="RunId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ValueDate" type="{http://xmlns.oracle.com/adf/svc/types/}date-Date" minOccurs="0"/&gt;
 *         &lt;element name="InvoiceFormat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CompanyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Address1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Address2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="City" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="PostalCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AttentionTo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Fax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CompanyRegistrationNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="GstRegistrationNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="PtpId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="StringCreatedDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="StringTradingDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="StringValueDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="InvoiceNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Desc1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Amount1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Desc2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Amount2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TotalAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Desc3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NemPenaltyInvoicesRVOSDO", propOrder = {
    "tradingDate",
    "runId",
    "valueDate",
    "invoiceFormat",
    "companyName",
    "address1",
    "address2",
    "city",
    "postalCode",
    "attentionTo",
    "fax",
    "companyRegistrationNumber",
    "gstRegistrationNumber",
    "ptpId",
    "stringCreatedDate",
    "stringTradingDate",
    "stringValueDate",
    "invoiceNumber",
    "desc1",
    "amount1",
    "desc2",
    "amount2",
    "totalAmount",
    "desc3"
})
public class NemPenaltyInvoicesRVOSDO {

    @XmlElementRef(name = "TradingDate", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> tradingDate;
    @XmlElementRef(name = "RunId", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> runId;
    @XmlElementRef(name = "ValueDate", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> valueDate;
    @XmlElementRef(name = "InvoiceFormat", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> invoiceFormat;
    @XmlElementRef(name = "CompanyName", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> companyName;
    @XmlElementRef(name = "Address1", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> address1;
    @XmlElementRef(name = "Address2", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> address2;
    @XmlElementRef(name = "City", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> city;
    @XmlElementRef(name = "PostalCode", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> postalCode;
    @XmlElementRef(name = "AttentionTo", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> attentionTo;
    @XmlElementRef(name = "Fax", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> fax;
    @XmlElementRef(name = "CompanyRegistrationNumber", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> companyRegistrationNumber;
    @XmlElementRef(name = "GstRegistrationNumber", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> gstRegistrationNumber;
    @XmlElementRef(name = "PtpId", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> ptpId;
    @XmlElementRef(name = "StringCreatedDate", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> stringCreatedDate;
    @XmlElementRef(name = "StringTradingDate", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> stringTradingDate;
    @XmlElementRef(name = "StringValueDate", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> stringValueDate;
    @XmlElementRef(name = "InvoiceNumber", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> invoiceNumber;
    @XmlElementRef(name = "Desc1", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> desc1;
    @XmlElementRef(name = "Amount1", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> amount1;
    @XmlElementRef(name = "Desc2", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> desc2;
    @XmlElementRef(name = "Amount2", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> amount2;
    @XmlElementRef(name = "TotalAmount", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> totalAmount;
    @XmlElementRef(name = "Desc3", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> desc3;

    /**
     * Gets the value of the tradingDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getTradingDate() {
        return tradingDate;
    }

    /**
     * Sets the value of the tradingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setTradingDate(JAXBElement<XMLGregorianCalendar> value) {
        this.tradingDate = value;
    }

    /**
     * Gets the value of the runId property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRunId() {
        return runId;
    }

    /**
     * Sets the value of the runId property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRunId(JAXBElement<String> value) {
        this.runId = value;
    }

    /**
     * Gets the value of the valueDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getValueDate() {
        return valueDate;
    }

    /**
     * Sets the value of the valueDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setValueDate(JAXBElement<XMLGregorianCalendar> value) {
        this.valueDate = value;
    }

    /**
     * Gets the value of the invoiceFormat property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getInvoiceFormat() {
        return invoiceFormat;
    }

    /**
     * Sets the value of the invoiceFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setInvoiceFormat(JAXBElement<String> value) {
        this.invoiceFormat = value;
    }

    /**
     * Gets the value of the companyName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCompanyName() {
        return companyName;
    }

    /**
     * Sets the value of the companyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCompanyName(JAXBElement<String> value) {
        this.companyName = value;
    }

    /**
     * Gets the value of the address1 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAddress1() {
        return address1;
    }

    /**
     * Sets the value of the address1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAddress1(JAXBElement<String> value) {
        this.address1 = value;
    }

    /**
     * Gets the value of the address2 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAddress2() {
        return address2;
    }

    /**
     * Sets the value of the address2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAddress2(JAXBElement<String> value) {
        this.address2 = value;
    }

    /**
     * Gets the value of the city property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCity() {
        return city;
    }

    /**
     * Sets the value of the city property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCity(JAXBElement<String> value) {
        this.city = value;
    }

    /**
     * Gets the value of the postalCode property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the value of the postalCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPostalCode(JAXBElement<String> value) {
        this.postalCode = value;
    }

    /**
     * Gets the value of the attentionTo property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAttentionTo() {
        return attentionTo;
    }

    /**
     * Sets the value of the attentionTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAttentionTo(JAXBElement<String> value) {
        this.attentionTo = value;
    }

    /**
     * Gets the value of the fax property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getFax() {
        return fax;
    }

    /**
     * Sets the value of the fax property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setFax(JAXBElement<String> value) {
        this.fax = value;
    }

    /**
     * Gets the value of the companyRegistrationNumber property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCompanyRegistrationNumber() {
        return companyRegistrationNumber;
    }

    /**
     * Sets the value of the companyRegistrationNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCompanyRegistrationNumber(JAXBElement<String> value) {
        this.companyRegistrationNumber = value;
    }

    /**
     * Gets the value of the gstRegistrationNumber property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getGstRegistrationNumber() {
        return gstRegistrationNumber;
    }

    /**
     * Sets the value of the gstRegistrationNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setGstRegistrationNumber(JAXBElement<String> value) {
        this.gstRegistrationNumber = value;
    }

    /**
     * Gets the value of the ptpId property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPtpId() {
        return ptpId;
    }

    /**
     * Sets the value of the ptpId property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPtpId(JAXBElement<String> value) {
        this.ptpId = value;
    }

    /**
     * Gets the value of the stringCreatedDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStringCreatedDate() {
        return stringCreatedDate;
    }

    /**
     * Sets the value of the stringCreatedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStringCreatedDate(JAXBElement<String> value) {
        this.stringCreatedDate = value;
    }

    /**
     * Gets the value of the stringTradingDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStringTradingDate() {
        return stringTradingDate;
    }

    /**
     * Sets the value of the stringTradingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStringTradingDate(JAXBElement<String> value) {
        this.stringTradingDate = value;
    }

    /**
     * Gets the value of the stringValueDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStringValueDate() {
        return stringValueDate;
    }

    /**
     * Sets the value of the stringValueDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStringValueDate(JAXBElement<String> value) {
        this.stringValueDate = value;
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
     * Gets the value of the desc1 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDesc1() {
        return desc1;
    }

    /**
     * Sets the value of the desc1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDesc1(JAXBElement<String> value) {
        this.desc1 = value;
    }

    /**
     * Gets the value of the amount1 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAmount1() {
        return amount1;
    }

    /**
     * Sets the value of the amount1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAmount1(JAXBElement<String> value) {
        this.amount1 = value;
    }

    /**
     * Gets the value of the desc2 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDesc2() {
        return desc2;
    }

    /**
     * Sets the value of the desc2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDesc2(JAXBElement<String> value) {
        this.desc2 = value;
    }

    /**
     * Gets the value of the amount2 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAmount2() {
        return amount2;
    }

    /**
     * Sets the value of the amount2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAmount2(JAXBElement<String> value) {
        this.amount2 = value;
    }

    /**
     * Gets the value of the totalAmount property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTotalAmount() {
        return totalAmount;
    }

    /**
     * Sets the value of the totalAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTotalAmount(JAXBElement<String> value) {
        this.totalAmount = value;
    }

    /**
     * Gets the value of the desc3 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDesc3() {
        return desc3;
    }

    /**
     * Sets the value of the desc3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDesc3(JAXBElement<String> value) {
        this.desc3 = value;
    }

}
