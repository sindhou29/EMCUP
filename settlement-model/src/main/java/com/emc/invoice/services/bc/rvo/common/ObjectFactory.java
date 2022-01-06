
package com.emc.invoice.services.bc.rvo.common;

import java.math.BigDecimal;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.emc.invoice.services.bc.rvo.common package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _NemPenaltyInvoicesRVOSDO_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "nemPenaltyInvoicesRVOSDO");
    private final static QName _InvoiceLabelValueTVOSDO_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "invoiceLabelValueTVOSDO");
    private final static QName _AuditBRFReportingRVOSDO_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "auditBRFReportingRVOSDO");
    private final static QName _AuditBRFReportingRVOSDOName_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "Name");
    private final static QName _AuditBRFReportingRVOSDOBrfAmount_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "BrfAmount");
    private final static QName _AuditBRFReportingRVOSDOStatus_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "Status");
    private final static QName _AuditBRFReportingRVOSDOFinalResult_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "FinalResult");
    private final static QName _AuditBRFReportingRVOSDOFilename_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "Filename");
    private final static QName _AuditBRFReportingRVOSDOTotalRecords_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "TotalRecords");
    private final static QName _AuditBRFReportingRVOSDOMisMatch_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "MisMatch");
    private final static QName _AuditBRFReportingRVOSDOUploadedDate_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "UploadedDate");
    private final static QName _InvoiceLabelValueTVOSDOLabel_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "Label");
    private final static QName _InvoiceLabelValueTVOSDOValue_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "Value");
    private final static QName _InvoiceLabelValueTVOSDOPtpId_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "PtpId");
    private final static QName _InvoiceLabelValueTVOSDOFieldType_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "FieldType");
    private final static QName _InvoiceLabelValueTVOSDOPtpName_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "PtpName");
    private final static QName _NemPenaltyInvoicesRVOSDOTradingDate_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "TradingDate");
    private final static QName _NemPenaltyInvoicesRVOSDORunId_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "RunId");
    private final static QName _NemPenaltyInvoicesRVOSDOValueDate_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "ValueDate");
    private final static QName _NemPenaltyInvoicesRVOSDOInvoiceFormat_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "InvoiceFormat");
    private final static QName _NemPenaltyInvoicesRVOSDOCompanyName_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "CompanyName");
    private final static QName _NemPenaltyInvoicesRVOSDOAddress1_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "Address1");
    private final static QName _NemPenaltyInvoicesRVOSDOAddress2_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "Address2");
    private final static QName _NemPenaltyInvoicesRVOSDOCity_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "City");
    private final static QName _NemPenaltyInvoicesRVOSDOPostalCode_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "PostalCode");
    private final static QName _NemPenaltyInvoicesRVOSDOAttentionTo_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "AttentionTo");
    private final static QName _NemPenaltyInvoicesRVOSDOFax_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "Fax");
    private final static QName _NemPenaltyInvoicesRVOSDOCompanyRegistrationNumber_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "CompanyRegistrationNumber");
    private final static QName _NemPenaltyInvoicesRVOSDOGstRegistrationNumber_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "GstRegistrationNumber");
    private final static QName _NemPenaltyInvoicesRVOSDOStringCreatedDate_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "StringCreatedDate");
    private final static QName _NemPenaltyInvoicesRVOSDOStringTradingDate_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "StringTradingDate");
    private final static QName _NemPenaltyInvoicesRVOSDOStringValueDate_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "StringValueDate");
    private final static QName _NemPenaltyInvoicesRVOSDOInvoiceNumber_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "InvoiceNumber");
    private final static QName _NemPenaltyInvoicesRVOSDODesc1_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "Desc1");
    private final static QName _NemPenaltyInvoicesRVOSDOAmount1_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "Amount1");
    private final static QName _NemPenaltyInvoicesRVOSDODesc2_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "Desc2");
    private final static QName _NemPenaltyInvoicesRVOSDOAmount2_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "Amount2");
    private final static QName _NemPenaltyInvoicesRVOSDOTotalAmount_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "TotalAmount");
    private final static QName _NemPenaltyInvoicesRVOSDODesc3_QNAME = new QName("/com/emc/invoice/services/bc/rvo/common/", "Desc3");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.emc.invoice.services.bc.rvo.common
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link NemPenaltyInvoicesRVOSDO }
     * 
     */
    public NemPenaltyInvoicesRVOSDO createNemPenaltyInvoicesRVOSDO() {
        return new NemPenaltyInvoicesRVOSDO();
    }

    /**
     * Create an instance of {@link InvoiceLabelValueTVOSDO }
     * 
     */
    public InvoiceLabelValueTVOSDO createInvoiceLabelValueTVOSDO() {
        return new InvoiceLabelValueTVOSDO();
    }

    /**
     * Create an instance of {@link AuditBRFReportingRVOSDO }
     * 
     */
    public AuditBRFReportingRVOSDO createAuditBRFReportingRVOSDO() {
        return new AuditBRFReportingRVOSDO();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NemPenaltyInvoicesRVOSDO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "nemPenaltyInvoicesRVOSDO")
    public JAXBElement<NemPenaltyInvoicesRVOSDO> createNemPenaltyInvoicesRVOSDO(NemPenaltyInvoicesRVOSDO value) {
        return new JAXBElement<NemPenaltyInvoicesRVOSDO>(_NemPenaltyInvoicesRVOSDO_QNAME, NemPenaltyInvoicesRVOSDO.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvoiceLabelValueTVOSDO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "invoiceLabelValueTVOSDO")
    public JAXBElement<InvoiceLabelValueTVOSDO> createInvoiceLabelValueTVOSDO(InvoiceLabelValueTVOSDO value) {
        return new JAXBElement<InvoiceLabelValueTVOSDO>(_InvoiceLabelValueTVOSDO_QNAME, InvoiceLabelValueTVOSDO.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuditBRFReportingRVOSDO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "auditBRFReportingRVOSDO")
    public JAXBElement<AuditBRFReportingRVOSDO> createAuditBRFReportingRVOSDO(AuditBRFReportingRVOSDO value) {
        return new JAXBElement<AuditBRFReportingRVOSDO>(_AuditBRFReportingRVOSDO_QNAME, AuditBRFReportingRVOSDO.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "Name", scope = AuditBRFReportingRVOSDO.class)
    public JAXBElement<String> createAuditBRFReportingRVOSDOName(String value) {
        return new JAXBElement<String>(_AuditBRFReportingRVOSDOName_QNAME, String.class, AuditBRFReportingRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "BrfAmount", scope = AuditBRFReportingRVOSDO.class)
    public JAXBElement<BigDecimal> createAuditBRFReportingRVOSDOBrfAmount(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_AuditBRFReportingRVOSDOBrfAmount_QNAME, BigDecimal.class, AuditBRFReportingRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "Status", scope = AuditBRFReportingRVOSDO.class)
    public JAXBElement<String> createAuditBRFReportingRVOSDOStatus(String value) {
        return new JAXBElement<String>(_AuditBRFReportingRVOSDOStatus_QNAME, String.class, AuditBRFReportingRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "FinalResult", scope = AuditBRFReportingRVOSDO.class)
    public JAXBElement<String> createAuditBRFReportingRVOSDOFinalResult(String value) {
        return new JAXBElement<String>(_AuditBRFReportingRVOSDOFinalResult_QNAME, String.class, AuditBRFReportingRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "Filename", scope = AuditBRFReportingRVOSDO.class)
    public JAXBElement<String> createAuditBRFReportingRVOSDOFilename(String value) {
        return new JAXBElement<String>(_AuditBRFReportingRVOSDOFilename_QNAME, String.class, AuditBRFReportingRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "TotalRecords", scope = AuditBRFReportingRVOSDO.class)
    public JAXBElement<BigDecimal> createAuditBRFReportingRVOSDOTotalRecords(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_AuditBRFReportingRVOSDOTotalRecords_QNAME, BigDecimal.class, AuditBRFReportingRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "MisMatch", scope = AuditBRFReportingRVOSDO.class)
    public JAXBElement<BigDecimal> createAuditBRFReportingRVOSDOMisMatch(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_AuditBRFReportingRVOSDOMisMatch_QNAME, BigDecimal.class, AuditBRFReportingRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "UploadedDate", scope = AuditBRFReportingRVOSDO.class)
    public JAXBElement<XMLGregorianCalendar> createAuditBRFReportingRVOSDOUploadedDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_AuditBRFReportingRVOSDOUploadedDate_QNAME, XMLGregorianCalendar.class, AuditBRFReportingRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "Label", scope = InvoiceLabelValueTVOSDO.class)
    public JAXBElement<String> createInvoiceLabelValueTVOSDOLabel(String value) {
        return new JAXBElement<String>(_InvoiceLabelValueTVOSDOLabel_QNAME, String.class, InvoiceLabelValueTVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "Value", scope = InvoiceLabelValueTVOSDO.class)
    public JAXBElement<String> createInvoiceLabelValueTVOSDOValue(String value) {
        return new JAXBElement<String>(_InvoiceLabelValueTVOSDOValue_QNAME, String.class, InvoiceLabelValueTVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "PtpId", scope = InvoiceLabelValueTVOSDO.class)
    public JAXBElement<String> createInvoiceLabelValueTVOSDOPtpId(String value) {
        return new JAXBElement<String>(_InvoiceLabelValueTVOSDOPtpId_QNAME, String.class, InvoiceLabelValueTVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "FieldType", scope = InvoiceLabelValueTVOSDO.class)
    public JAXBElement<String> createInvoiceLabelValueTVOSDOFieldType(String value) {
        return new JAXBElement<String>(_InvoiceLabelValueTVOSDOFieldType_QNAME, String.class, InvoiceLabelValueTVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "PtpName", scope = InvoiceLabelValueTVOSDO.class)
    public JAXBElement<String> createInvoiceLabelValueTVOSDOPtpName(String value) {
        return new JAXBElement<String>(_InvoiceLabelValueTVOSDOPtpName_QNAME, String.class, InvoiceLabelValueTVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "TradingDate", scope = NemPenaltyInvoicesRVOSDO.class)
    public JAXBElement<XMLGregorianCalendar> createNemPenaltyInvoicesRVOSDOTradingDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_NemPenaltyInvoicesRVOSDOTradingDate_QNAME, XMLGregorianCalendar.class, NemPenaltyInvoicesRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "RunId", scope = NemPenaltyInvoicesRVOSDO.class)
    public JAXBElement<String> createNemPenaltyInvoicesRVOSDORunId(String value) {
        return new JAXBElement<String>(_NemPenaltyInvoicesRVOSDORunId_QNAME, String.class, NemPenaltyInvoicesRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "ValueDate", scope = NemPenaltyInvoicesRVOSDO.class)
    public JAXBElement<XMLGregorianCalendar> createNemPenaltyInvoicesRVOSDOValueDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_NemPenaltyInvoicesRVOSDOValueDate_QNAME, XMLGregorianCalendar.class, NemPenaltyInvoicesRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "InvoiceFormat", scope = NemPenaltyInvoicesRVOSDO.class)
    public JAXBElement<String> createNemPenaltyInvoicesRVOSDOInvoiceFormat(String value) {
        return new JAXBElement<String>(_NemPenaltyInvoicesRVOSDOInvoiceFormat_QNAME, String.class, NemPenaltyInvoicesRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "CompanyName", scope = NemPenaltyInvoicesRVOSDO.class)
    public JAXBElement<String> createNemPenaltyInvoicesRVOSDOCompanyName(String value) {
        return new JAXBElement<String>(_NemPenaltyInvoicesRVOSDOCompanyName_QNAME, String.class, NemPenaltyInvoicesRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "Address1", scope = NemPenaltyInvoicesRVOSDO.class)
    public JAXBElement<String> createNemPenaltyInvoicesRVOSDOAddress1(String value) {
        return new JAXBElement<String>(_NemPenaltyInvoicesRVOSDOAddress1_QNAME, String.class, NemPenaltyInvoicesRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "Address2", scope = NemPenaltyInvoicesRVOSDO.class)
    public JAXBElement<String> createNemPenaltyInvoicesRVOSDOAddress2(String value) {
        return new JAXBElement<String>(_NemPenaltyInvoicesRVOSDOAddress2_QNAME, String.class, NemPenaltyInvoicesRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "City", scope = NemPenaltyInvoicesRVOSDO.class)
    public JAXBElement<String> createNemPenaltyInvoicesRVOSDOCity(String value) {
        return new JAXBElement<String>(_NemPenaltyInvoicesRVOSDOCity_QNAME, String.class, NemPenaltyInvoicesRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "PostalCode", scope = NemPenaltyInvoicesRVOSDO.class)
    public JAXBElement<String> createNemPenaltyInvoicesRVOSDOPostalCode(String value) {
        return new JAXBElement<String>(_NemPenaltyInvoicesRVOSDOPostalCode_QNAME, String.class, NemPenaltyInvoicesRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "AttentionTo", scope = NemPenaltyInvoicesRVOSDO.class)
    public JAXBElement<String> createNemPenaltyInvoicesRVOSDOAttentionTo(String value) {
        return new JAXBElement<String>(_NemPenaltyInvoicesRVOSDOAttentionTo_QNAME, String.class, NemPenaltyInvoicesRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "Fax", scope = NemPenaltyInvoicesRVOSDO.class)
    public JAXBElement<String> createNemPenaltyInvoicesRVOSDOFax(String value) {
        return new JAXBElement<String>(_NemPenaltyInvoicesRVOSDOFax_QNAME, String.class, NemPenaltyInvoicesRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "CompanyRegistrationNumber", scope = NemPenaltyInvoicesRVOSDO.class)
    public JAXBElement<String> createNemPenaltyInvoicesRVOSDOCompanyRegistrationNumber(String value) {
        return new JAXBElement<String>(_NemPenaltyInvoicesRVOSDOCompanyRegistrationNumber_QNAME, String.class, NemPenaltyInvoicesRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "GstRegistrationNumber", scope = NemPenaltyInvoicesRVOSDO.class)
    public JAXBElement<String> createNemPenaltyInvoicesRVOSDOGstRegistrationNumber(String value) {
        return new JAXBElement<String>(_NemPenaltyInvoicesRVOSDOGstRegistrationNumber_QNAME, String.class, NemPenaltyInvoicesRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "PtpId", scope = NemPenaltyInvoicesRVOSDO.class)
    public JAXBElement<String> createNemPenaltyInvoicesRVOSDOPtpId(String value) {
        return new JAXBElement<String>(_InvoiceLabelValueTVOSDOPtpId_QNAME, String.class, NemPenaltyInvoicesRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "StringCreatedDate", scope = NemPenaltyInvoicesRVOSDO.class)
    public JAXBElement<String> createNemPenaltyInvoicesRVOSDOStringCreatedDate(String value) {
        return new JAXBElement<String>(_NemPenaltyInvoicesRVOSDOStringCreatedDate_QNAME, String.class, NemPenaltyInvoicesRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "StringTradingDate", scope = NemPenaltyInvoicesRVOSDO.class)
    public JAXBElement<String> createNemPenaltyInvoicesRVOSDOStringTradingDate(String value) {
        return new JAXBElement<String>(_NemPenaltyInvoicesRVOSDOStringTradingDate_QNAME, String.class, NemPenaltyInvoicesRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "StringValueDate", scope = NemPenaltyInvoicesRVOSDO.class)
    public JAXBElement<String> createNemPenaltyInvoicesRVOSDOStringValueDate(String value) {
        return new JAXBElement<String>(_NemPenaltyInvoicesRVOSDOStringValueDate_QNAME, String.class, NemPenaltyInvoicesRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "InvoiceNumber", scope = NemPenaltyInvoicesRVOSDO.class)
    public JAXBElement<String> createNemPenaltyInvoicesRVOSDOInvoiceNumber(String value) {
        return new JAXBElement<String>(_NemPenaltyInvoicesRVOSDOInvoiceNumber_QNAME, String.class, NemPenaltyInvoicesRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "Desc1", scope = NemPenaltyInvoicesRVOSDO.class)
    public JAXBElement<String> createNemPenaltyInvoicesRVOSDODesc1(String value) {
        return new JAXBElement<String>(_NemPenaltyInvoicesRVOSDODesc1_QNAME, String.class, NemPenaltyInvoicesRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "Amount1", scope = NemPenaltyInvoicesRVOSDO.class)
    public JAXBElement<String> createNemPenaltyInvoicesRVOSDOAmount1(String value) {
        return new JAXBElement<String>(_NemPenaltyInvoicesRVOSDOAmount1_QNAME, String.class, NemPenaltyInvoicesRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "Desc2", scope = NemPenaltyInvoicesRVOSDO.class)
    public JAXBElement<String> createNemPenaltyInvoicesRVOSDODesc2(String value) {
        return new JAXBElement<String>(_NemPenaltyInvoicesRVOSDODesc2_QNAME, String.class, NemPenaltyInvoicesRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "Amount2", scope = NemPenaltyInvoicesRVOSDO.class)
    public JAXBElement<String> createNemPenaltyInvoicesRVOSDOAmount2(String value) {
        return new JAXBElement<String>(_NemPenaltyInvoicesRVOSDOAmount2_QNAME, String.class, NemPenaltyInvoicesRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "TotalAmount", scope = NemPenaltyInvoicesRVOSDO.class)
    public JAXBElement<String> createNemPenaltyInvoicesRVOSDOTotalAmount(String value) {
        return new JAXBElement<String>(_NemPenaltyInvoicesRVOSDOTotalAmount_QNAME, String.class, NemPenaltyInvoicesRVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/rvo/common/", name = "Desc3", scope = NemPenaltyInvoicesRVOSDO.class)
    public JAXBElement<String> createNemPenaltyInvoicesRVOSDODesc3(String value) {
        return new JAXBElement<String>(_NemPenaltyInvoicesRVOSDODesc3_QNAME, String.class, NemPenaltyInvoicesRVOSDO.class, value);
    }

}
