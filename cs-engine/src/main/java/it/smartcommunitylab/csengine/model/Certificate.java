package it.smartcommunitylab.csengine.model;

import java.util.HashMap;
import java.util.Map;

public class Certificate {
	private String storageId;
	private String studentId;
	private String experienceId;
	private Map<String, Object> attributes = new HashMap<String, Object>();
	private Boolean documentPresent = Boolean.FALSE;
	private String documentUri;
	private String contentType;
	
	public String getExperienceId() {
		return experienceId;
	}
	public void setExperienceId(String experienceId) {
		this.experienceId = experienceId;
	}
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	public String getDocumentUri() {
		return documentUri;
	}
	public void setDocumentUri(String documentUri) {
		this.documentUri = documentUri;
	}
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public String getStorageId() {
		return storageId;
	}
	public void setStorageId(String storageId) {
		this.storageId = storageId;
	}
	public Boolean getDocumentPresent() {
		return documentPresent;
	}
	public void setDocumentPresent(Boolean documentPresent) {
		this.documentPresent = documentPresent;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
}
