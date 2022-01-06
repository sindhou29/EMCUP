
package com.emc.invoice.services.bc.tvo.common;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.emc.invoice.services.bc.tvo.common package. 
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

    private final static QName _BatchReportTVOSDO_QNAME = new QName("/com/emc/invoice/services/bc/tvo/common/", "batchReportTVOSDO");
    private final static QName _BatchReportTVOSDOCustomerName_QNAME = new QName("/com/emc/invoice/services/bc/tvo/common/", "CustomerName");
    private final static QName _BatchReportTVOSDODocDesc_QNAME = new QName("/com/emc/invoice/services/bc/tvo/common/", "DocDesc");
    private final static QName _BatchReportTVOSDODueDate_QNAME = new QName("/com/emc/invoice/services/bc/tvo/common/", "DueDate");
    private final static QName _BatchReportTVOSDOAmt_QNAME = new QName("/com/emc/invoice/services/bc/tvo/common/", "Amt");
    private final static QName _BatchReportTVOSDOTotalAmt_QNAME = new QName("/com/emc/invoice/services/bc/tvo/common/", "TotalAmt");
    private final static QName _BatchReportTVOSDOGst_QNAME = new QName("/com/emc/invoice/services/bc/tvo/common/", "Gst");
    private final static QName _BatchReportTVOSDOCustomerId_QNAME = new QName("/com/emc/invoice/services/bc/tvo/common/", "CustomerId");
    private final static QName _BatchReportTVOSDOInvoiceNumber_QNAME = new QName("/com/emc/invoice/services/bc/tvo/common/", "InvoiceNumber");
    private final static QName _BatchReportTVOSDODueDateString_QNAME = new QName("/com/emc/invoice/services/bc/tvo/common/", "DueDateString");
    private final static QName _BatchReportTVOSDOTradingDate_QNAME = new QName("/com/emc/invoice/services/bc/tvo/common/", "TradingDate");
    private final static QName _BatchReportTVOSDOAuditDueDate_QNAME = new QName("/com/emc/invoice/services/bc/tvo/common/", "AuditDueDate");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.emc.invoice.services.bc.tvo.common
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link BatchReportTVOSDO }
     * 
     */
    public BatchReportTVOSDO createBatchReportTVOSDO() {
        return new BatchReportTVOSDO();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BatchReportTVOSDO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/tvo/common/", name = "batchReportTVOSDO")
    public JAXBElement<BatchReportTVOSDO> createBatchReportTVOSDO(BatchReportTVOSDO value) {
        return new JAXBElement<BatchReportTVOSDO>(_BatchReportTVOSDO_QNAME, BatchReportTVOSDO.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/tvo/common/", name = "CustomerName", scope = BatchReportTVOSDO.class)
    public JAXBElement<String> createBatchReportTVOSDOCustomerName(String value) {
        return new JAXBElement<String>(_BatchReportTVOSDOCustomerName_QNAME, String.class, BatchReportTVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/tvo/common/", name = "DocDesc", scope = BatchReportTVOSDO.class)
    public JAXBElement<String> createBatchReportTVOSDODocDesc(String value) {
        return new JAXBElement<String>(_BatchReportTVOSDODocDesc_QNAME, String.class, BatchReportTVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/tvo/common/", name = "DueDate", scope = BatchReportTVOSDO.class)
    public JAXBElement<XMLGregorianCalendar> createBatchReportTVOSDODueDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_BatchReportTVOSDODueDate_QNAME, XMLGregorianCalendar.class, BatchReportTVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/tvo/common/", name = "Amt", scope = BatchReportTVOSDO.class)
    public JAXBElement<String> createBatchReportTVOSDOAmt(String value) {
        return new JAXBElement<String>(_BatchReportTVOSDOAmt_QNAME, String.class, BatchReportTVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/tvo/common/", name = "TotalAmt", scope = BatchReportTVOSDO.class)
    public JAXBElement<String> createBatchReportTVOSDOTotalAmt(String value) {
        return new JAXBElement<String>(_BatchReportTVOSDOTotalAmt_QNAME, String.class, BatchReportTVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/tvo/common/", name = "Gst", scope = BatchReportTVOSDO.class)
    public JAXBElement<String> createBatchReportTVOSDOGst(String value) {
        return new JAXBElement<String>(_BatchReportTVOSDOGst_QNAME, String.class, BatchReportTVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/tvo/common/", name = "CustomerId", scope = BatchReportTVOSDO.class)
    public JAXBElement<String> createBatchReportTVOSDOCustomerId(String value) {
        return new JAXBElement<String>(_BatchReportTVOSDOCustomerId_QNAME, String.class, BatchReportTVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/tvo/common/", name = "InvoiceNumber", scope = BatchReportTVOSDO.class)
    public JAXBElement<String> createBatchReportTVOSDOInvoiceNumber(String value) {
        return new JAXBElement<String>(_BatchReportTVOSDOInvoiceNumber_QNAME, String.class, BatchReportTVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/tvo/common/", name = "DueDateString", scope = BatchReportTVOSDO.class)
    public JAXBElement<String> createBatchReportTVOSDODueDateString(String value) {
        return new JAXBElement<String>(_BatchReportTVOSDODueDateString_QNAME, String.class, BatchReportTVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/tvo/common/", name = "TradingDate", scope = BatchReportTVOSDO.class)
    public JAXBElement<String> createBatchReportTVOSDOTradingDate(String value) {
        return new JAXBElement<String>(_BatchReportTVOSDOTradingDate_QNAME, String.class, BatchReportTVOSDO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "/com/emc/invoice/services/bc/tvo/common/", name = "AuditDueDate", scope = BatchReportTVOSDO.class)
    public JAXBElement<String> createBatchReportTVOSDOAuditDueDate(String value) {
        return new JAXBElement<String>(_BatchReportTVOSDOAuditDueDate_QNAME, String.class, BatchReportTVOSDO.class, value);
    }

}
