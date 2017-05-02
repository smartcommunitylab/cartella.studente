package it.smartcommunitylab.csengine.model;

public class Consent extends BaseObject {
	private String studentId;
	private String subject;
	private Boolean authorized = Boolean.FALSE;
	
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Boolean getAuthorized() {
		return authorized;
	}
	public void setAuthorized(Boolean authorized) {
		this.authorized = authorized;
	}

}
