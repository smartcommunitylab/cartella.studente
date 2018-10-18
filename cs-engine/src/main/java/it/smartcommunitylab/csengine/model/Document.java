package it.smartcommunitylab.csengine.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.smartcommunitylab.csengine.common.Utils;

public class Document {
	private String storageId;
	private String studentId;
	private String experienceId;
	private Map<String, Object> attributes = new HashMap<String, Object>();
	private Boolean documentPresent = Boolean.FALSE;
	private String contentType;
	private String filename;
	private String url;
	
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
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public boolean equals(Object o) {
		boolean result = false; 
		if(o instanceof Document) {
			Document object = (Document) o;
			if(Utils.isNotEmpty(object.getStorageId())) {
				if(object.getStorageId().equals(storageId)) {
					result = true;
				}
			}
		}
		return result;
	}
	
	@Override
  public int hashCode() {
      return Objects.hash(storageId);
  }

	
}
