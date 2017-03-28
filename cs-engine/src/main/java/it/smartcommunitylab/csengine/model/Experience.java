package it.smartcommunitylab.csengine.model;

import java.util.HashMap;
import java.util.Map;

public class Experience extends BaseObject {
	private String studentId;
	private String type;
	private Map<String, Object> attributes = new HashMap<String, Object>();
	private Certificate certificate;
	
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Certificate getCertificate() {
		return certificate;
	}
	public void setCertificate(Certificate certificate) {
		this.certificate = certificate;
	}
	
}
