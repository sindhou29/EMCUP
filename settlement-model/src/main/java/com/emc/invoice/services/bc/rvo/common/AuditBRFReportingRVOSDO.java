
package com.emc.invoice.services.bc.rvo.common;

import java.math.BigDecimal;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for AuditBRFReportingRVOSDO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AuditBRFReportingRVOSDO"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ValueDate" type="{http://xmlns.oracle.com/adf/svc/types/}date-Date" minOccurs="0"/&gt;
 *         &lt;element name="EftReference" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="InvoicesAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="PrepaymentAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="EftAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="BrfAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="CloseAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="Status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="FinalResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="RunId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TradingDate" type="{http://xmlns.oracle.com/adf/svc/types/}date-Date" minOccurs="0"/&gt;
 *         &lt;element name="Filename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TotalRecords" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="MisMatch" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="UploadedDate" type="{http://xmlns.oracle.com/adf/svc/types/}date-Date" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuditBRFReportingRVOSDO", propOrder = {
    "valueDate",
    "eftReference",
    "name",
    "invoicesAmount",
    "prepaymentAmount",
    "eftAmount",
    "brfAmount",
    "closeAmount",
    "status",
    "finalResult",
    "runId",
    "tradingDate",
    "filename",
    "totalRecords",
    "misMatch",
    "uploadedDate"
})
public class AuditBRFReportingRVOSDO {

    @XmlElement(name = "ValueDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar valueDate;
    @XmlElement(name = "EftReference")
    protected String eftReference;
    @XmlElementRef(name = "Name", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> name;
    @XmlElement(name = "InvoicesAmount")
    protected BigDecimal invoicesAmount;
    @XmlElement(name = "PrepaymentAmount")
    protected BigDecimal prepaymentAmount;
    @XmlElement(name = "EftAmount")
    protected BigDecimal eftAmount;
    @XmlElementRef(name = "BrfAmount", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<BigDecimal> brfAmount;
    @XmlElement(name = "CloseAmount")
    protected BigDecimal closeAmount;
    @XmlElementRef(name = "Status", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> status;
    @XmlElementRef(name = "FinalResult", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> finalResult;
    @XmlElement(name = "RunId")
    protected String runId;
    @XmlElement(name = "TradingDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar tradingDate;
    @XmlElementRef(name = "Filename", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> filename;
    @XmlElementRef(name = "TotalRecords", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<BigDecimal> totalRecords;
    @XmlElementRef(name = "MisMatch", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<BigDecimal> misMatch;
    @XmlElementRef(name = "UploadedDate", namespace = "/com/emc/invoice/services/bc/rvo/common/", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> uploadedDate;

    /**
     * Gets the value of the valueDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getValueDate() {
        return valueDate;
    }

    /**
     * Sets the value of the valueDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setValueDate(XMLGregorianCalendar value) {
        this.valueDate = value;
    }

    /**
     * Gets the value of the eftReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEftReference() {
        return eftReference;
    }

    /**
     * Sets the value of the eftReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEftReference(String value) {
        this.eftReference = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setName(JAXBElement<String> value) {
        this.name = value;
    }

    /**
     * Gets the value of the invoicesAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getInvoicesAmount() {
        return invoicesAmount;
    }

    /**
     * Sets the value of the invoicesAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setInvoicesAmount(BigDecimal value) {
        this.invoicesAmount = value;
    }

    /**
     * Gets the value of the prepaymentAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPrepaymentAmount() {
        return prepaymentAmount;
    }

    /**
     * Sets the value of the prepaymentAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPrepaymentAmount(BigDecimal value) {
        this.prepaymentAmount = value;
    }

    /**
     * Gets the value of the eftAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getEftAmount() {
        return eftAmount;
    }

    /**
     * Sets the value of the eftAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEftAmount(BigDecimal value) {
        this.eftAmount = value;
    }

    /**
     * Gets the value of the brfAmount property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     *     
     */
    public JAXBElement<BigDecimal> getBrfAmount() {
        return brfAmount;
    }

    /**
     * Sets the value of the brfAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     *     
     */
    public void setBrfAmount(JAXBElement<BigDecimal> value) {
        this.brfAmount = value;
    }

    /**
     * Gets the value of the closeAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getCloseAmount() {
        return closeAmount;
    }

    /**
     * Sets the value of the closeAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setCloseAmount(BigDecimal value) {
        this.closeAmount = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStatus(JAXBElement<String> value) {
        this.status = value;
    }

    /**
     * Gets the value of the finalResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getFinalResult() {
        return finalResult;
    }

    /**
     * Sets the value of the finalResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setFinalResult(JAXBElement<String> value) {
        this.finalResult = value;
    }

    /**
     * Gets the value of the runId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRunId() {
        return runId;
    }

    /**
     * Sets the value of the runId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRunId(String value) {
        this.runId = value;
    }

    /**
     * Gets the value of the tradingDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTradingDate() {
        return tradingDate;
    }

    /**
     * Sets the value of the tradingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTradingDate(XMLGregorianCalendar value) {
        this.tradingDate = value;
    }

    /**
     * Gets the value of the filename property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getFilename() {
        return filename;
    }

    /**
     * Sets the value of the filename property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setFilename(JAXBElement<String> value) {
        this.filename = value;
    }

    /**
     * Gets the value of the totalRecords property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     *     
     */
    public JAXBElement<BigDecimal> getTotalRecords() {
        return totalRecords;
    }

    /**
     * Sets the value of the totalRecords property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     *     
     */
    public void setTotalRecords(JAXBElement<BigDecimal> value) {
        this.totalRecords = value;
    }

    /**
     * Gets the value of the misMatch property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     *     
     */
    public JAXBElement<BigDecimal> getMisMatch() {
        return misMatch;
    }

    /**
     * Sets the value of the misMatch property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     *     
     */
    public void setMisMatch(JAXBElement<BigDecimal> value) {
        this.misMatch = value;
    }

    /**
     * Gets the value of the uploadedDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getUploadedDate() {
        return uploadedDate;
    }

    /**
     * Sets the value of the uploadedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setUploadedDate(JAXBElement<XMLGregorianCalendar> value) {
        this.uploadedDate = value;
    }

}
