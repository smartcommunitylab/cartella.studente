package it.smartcommunitylab.csengine.model;

import it.smartcommunitylab.aac.authorization.beans.AuthorizationDTO;

public class StudentAuth extends BaseObject {
	private String studentId;
	private AuthorizationDTO auth;
	
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public AuthorizationDTO getAuth() {
		return auth;
	}
	public void setAuth(AuthorizationDTO auth) {
		this.auth = auth;
	}	

}
