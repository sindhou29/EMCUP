package com.emc.settlement.model.backend.pojo;

import java.io.File;
import java.io.Serializable;

public class FileHolder  implements Serializable{

	public String file_filename;
	public String file_content_type;
	public String comments;
	public String current_version;
	public String change_type;
	public String filedir;
	public String uploaduser;
	public String effective_date;

	public File file;
	
	public int major_change;
	public int  minor_change;
	
	public String getFile_filename() {
		return file_filename;
	}
	public void setFile_filename(String file_filename) {
		this.file_filename = file_filename;
	}
	public String getFile_content_type() {
		return file_content_type;
	}
	public void setFile_content_type(String file_content_type) {
		this.file_content_type = file_content_type;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getCurrent_version() {
		return current_version;
	}
	public void setCurrent_version(String current_version) {
		this.current_version = current_version;
	}
	public String getChange_type() {
		return change_type;
	}
	public void setChange_type(String change_type) {
		this.change_type = change_type;
	}
	public String getFiledir() {
		return filedir;
	}
	public void setFiledir(String filedir) {
		this.filedir = filedir;
	}
	public String getUploaduser() {
		return uploaduser;
	}
	public void setUploaduser(String uploaduser) {
		this.uploaduser = uploaduser;
	}
	public String getEffective_date() {
		return effective_date;
	}
	public void setEffective_date(String effective_date) {
		this.effective_date = effective_date;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public int getMajor_change() {
		return major_change;
	}
	public void setMajor_change(int major_change) {
		this.major_change = major_change;
	}
	public int getMinor_change() {
		return minor_change;
	}
	public void setMinor_change(int minor_change) {
		this.minor_change = minor_change;
	}
}
