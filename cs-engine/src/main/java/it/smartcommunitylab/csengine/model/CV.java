package it.smartcommunitylab.csengine.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Transient;

public class CV extends BaseObject {
	private String studentId;
	private List<String> studentExperienceIds = new ArrayList<String>();
	@Transient
	private List<StudentExperience> experiences = new ArrayList<StudentExperience>();
	@Transient
	private Student student;
	private String drivingLicence;
	private String managementSkills;
	
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public List<StudentExperience> getExperiences() {
		return experiences;
	}
	public void setExperiences(List<StudentExperience> experiences) {
		this.experiences = experiences;
	}
	public String getDrivingLicence() {
		return drivingLicence;
	}
	public void setDrivingLicence(String drivingLicence) {
		this.drivingLicence = drivingLicence;
	}
	public String getManagementSkills() {
		return managementSkills;
	}
	public void setManagementSkills(String managementSkills) {
		this.managementSkills = managementSkills;
	}
	public List<String> getStudentExperienceIds() {
		return studentExperienceIds;
	}
	public void setStudentExperienceIds(List<String> studentExperienceIds) {
		this.studentExperienceIds = studentExperienceIds;
	}
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
}
