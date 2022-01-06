package com.emc.drcap.penalty.model.bc.am.common;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 3.1.12.redhat-1
 * 2018-07-27T11:37:11.512+08:00
 * Generated source version: 3.1.12.redhat-1
 * 
 */
@WebService(targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/", name = "PenaltyService")
@XmlSeeAlso({com.oracle.xmlns.adf.svc.types.ObjectFactory.class, sdo.commonj.java.ObjectFactory.class, sdo.commonj.xml.ObjectFactory.class, com.emc.drcap.penalty.model.bc.am.common.types.ObjectFactory.class, sdo.commonj.ObjectFactory.class, com.oracle.xmlns.adf.svc.errors.ObjectFactory.class})
public interface PenaltyService {

    @WebMethod(action = "/com/emc/drcap/penalty/model/bc/am/common/execComplianceCheck")
    @RequestWrapper(localName = "execComplianceCheck", targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/types/", className = "com.emc.drcap.penalty.model.bc.am.common.types.ExecComplianceCheck")
    @ResponseWrapper(localName = "execComplianceCheckResponse", targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/types/", className = "com.emc.drcap.penalty.model.bc.am.common.types.ExecComplianceCheckResponse")
    @WebResult(name = "result", targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/types/")
    
    //@RequestWrapper(localName = "execRulesControllerRun", targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/types/", className = "com.emc.drcap.penalty.model.bc.am.common.types.ExecRulesControllerRun")
    //@ResponseWrapper(localName = "execRulesControllerRunResponse", targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/types/", className = "com.emc.drcap.penalty.model.bc.am.common.types.ExecRulesControllerRunResponse")
    //@WebResult(name = "result", targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/types/")

    public java.lang.String execComplianceCheck(
        @WebParam(name = "tradingDate", targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/types/")
        javax.xml.datatype.XMLGregorianCalendar tradingDate,
        @WebParam(name = "runDate", targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/types/")
        javax.xml.datatype.XMLGregorianCalendar runDate,
        @WebParam(name = "runId", targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/types/")
        java.lang.String runId,
        @WebParam(name = "runType", targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/types/")
        java.lang.String runType
    ) throws com.oracle.xmlns.adf.svc.errors.ServiceException;

    @WebMethod(action = "/com/emc/drcap/penalty/model/bc/am/common/execRulesControllerRun")
    @RequestWrapper(localName = "execRulesControllerRun", targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/types/", className = "com.emc.drcap.penalty.model.bc.am.common.types.ExecRulesControllerRun")
    @ResponseWrapper(localName = "execRulesControllerRunResponse", targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/types/", className = "com.emc.drcap.penalty.model.bc.am.common.types.ExecRulesControllerRunResponse")
    @WebResult(name = "result", targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/types/")
    public java.lang.String execRulesControllerRun(
        @WebParam(name = "tradingDate", targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/types/")
        javax.xml.datatype.XMLGregorianCalendar tradingDate,
        @WebParam(name = "runDate", targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/types/")
        javax.xml.datatype.XMLGregorianCalendar runDate,
        @WebParam(name = "runId", targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/types/")
        java.lang.String runId,
        @WebParam(name = "runType", targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/types/")
        java.lang.String runType
    ) throws com.oracle.xmlns.adf.svc.errors.ServiceException;

    @WebMethod(action = "/com/emc/drcap/penalty/model/bc/am/common/afpsifFileUpload")
    @RequestWrapper(localName = "afpsifFileUpload", targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/types/", className = "com.emc.drcap.penalty.model.bc.am.common.types.AfpsifFileUpload")
    @ResponseWrapper(localName = "afpsifFileUploadResponse", targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/types/", className = "com.emc.drcap.penalty.model.bc.am.common.types.AfpsifFileUploadResponse")
    @WebResult(name = "result", targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/types/")
    public java.lang.String afpsifFileUpload(
        @WebParam(name = "uploadEventId", targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/types/")
        java.lang.String uploadEventId
    ) throws com.oracle.xmlns.adf.svc.errors.ServiceException;

    @WebMethod(action = "/com/emc/drcap/penalty/model/bc/am/common/approveOrRejectAfpsifFile")
    @RequestWrapper(localName = "approveOrRejectAfpsifFile", targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/types/", className = "com.emc.drcap.penalty.model.bc.am.common.types.ApproveOrRejectAfpsifFile")
    @ResponseWrapper(localName = "approveOrRejectAfpsifFileResponse", targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/types/", className = "com.emc.drcap.penalty.model.bc.am.common.types.ApproveOrRejectAfpsifFileResponse")
    @WebResult(name = "result", targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/types/")
    public java.lang.String approveOrRejectAfpsifFile(
        @WebParam(name = "uploadEventId", targetNamespace = "/com/emc/drcap/penalty/model/bc/am/common/types/")
        java.lang.String uploadEventId
    ) throws com.oracle.xmlns.adf.svc.errors.ServiceException;
}