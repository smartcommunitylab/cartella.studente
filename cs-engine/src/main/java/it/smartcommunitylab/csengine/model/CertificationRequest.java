package it.smartcommunitylab.csengine.model;

import java.util.Date;

public class CertificationRequest {
	private String id;
	private String studentId;
	private String experienceId;
	private String certifierId;
	private Date timestamp;
	private Experience experience;
	
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public String getExperienceId() {
		return experienceId;
	}
	public void setExperienceId(String experienceId) {
		this.experienceId = experienceId;
	}
	public String getCertifierId() {
		return certifierId;
	}
	public void setCertifierId(String certifierId) {
		this.certifierId = certifierId;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public Experience getExperience() {
		return experience;
	}
	public void setExperience(Experience experience) {
		this.experience = experience;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
