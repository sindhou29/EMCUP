package com.emc.settlement.model.backend.pojo;

import java.io.File;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

public class FileHelper  implements Serializable{

	protected static final Logger logger = Logger.getLogger(FileHelper.class);
	
	public void deletedir(String filepath)
	{
		try {
		    File dir = new File(filepath);
		    boolean result;

		    if (dir.exists()) {
		        File[] files = dir.listFiles();

		        if (files.length > 0) {
		            for (int i = 0; i <= files.length - 1; i++) {
		                // logMessage "delete file[" + files[i] + "]from dir[" + dir +"]" using severity = WARNING
		                if (files[i].isDirectory() == true) {
		                    this.deletedir(files[i].getAbsolutePath());
		                }
		                else {
		                    File tempFile = files[i];
		                    tempFile.delete();
		                }
		            }
		        }
		    }
		    else {
		        logger.log(Priority.INFO,"[EMC] Input dir" + dir + "is not a directory");
		    }

		    logger.log(Priority.INFO,"delete the root directory: " + dir);

		    dir.delete();
		}
		catch (Exception e) {
		    logger.log(Priority.INFO,"[EMC] Exception in deletingFiles " + e.getMessage());
		}
		
	}
}
