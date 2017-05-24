package it.smartcommunitylab.csengine.ui;

import java.util.ArrayList;
import java.util.List;

public class Profile {
	private String subject;
	private String studentId;
	private String personInChargeId;
	private List<String> studentIds = new ArrayList<String>();
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public String getPersonInChargeId() {
		return personInChargeId;
	}
	public void setPersonInChargeId(String personInChargeId) {
		this.personInChargeId = personInChargeId;
	}
	public List<String> getStudentIds() {
		return studentIds;
	}
	public void setStudentIds(List<String> studentIds) {
		this.studentIds = studentIds;
	}
	
}
