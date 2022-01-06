/**
 * 
 */
package com.emc.settlement.backend.scheduled;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.emc.rex.model.bc.am.common.RexInternalService;
import com.emc.rex.model.bc.am.common.RexInternalService_Service;
import com.oracle.xmlns.adf.svc.errors.ServiceException;

/**
 * @author DWTN1561
 *
 */
@Service
public class RexInternalServiceClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public void startREXRunByRunDate() {
		
   		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

   		try {
   			RexInternalService_Service service = new RexInternalService_Service();
   			RexInternalService rexInternalServicesSoapHttpPort = service.getRexInternalServiceSoapHttpPort();
		
			String pdfBytes = rexInternalServicesSoapHttpPort.startREXRunByRunDate(fmt.format(new Date()), "Schedule Run - No Action required", "S", "SYSTEM");
			System.out.println("Finished : "+pdfBytes);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
