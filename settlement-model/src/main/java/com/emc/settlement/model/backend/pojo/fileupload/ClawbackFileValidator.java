/**
 * 
 */
package com.emc.settlement.model.backend.pojo.fileupload;



import java.io.Serializable;

import org.apache.log4j.Logger;


/**
 * @author DWTN1561
 *
 */
public class ClawbackFileValidator  implements Serializable{

	protected static final Logger logger = Logger.getLogger(ClawbackFileValidator.class);
	/**
	 * 
	 */
	public ClawbackFileValidator() {
		// TODO Auto-generated constructor stub
	}
	
	public int clawbackMinDataColumnSize=5; 
	
	public int getClawbackMinDataColumnSize() {
		return clawbackMinDataColumnSize;
	}

	public void setClawbackMinDataColumnSize(int clawbackMinDataColumnSize) {
		this.clawbackMinDataColumnSize = clawbackMinDataColumnSize;
	}

	


}
