package it.smartcommunitylab.csengine.model;

import it.smartcommunitylab.csengine.cv.CVRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Transient;

public class CV extends BaseObject {
	private String studentId;
	private Map<String, List<String>> studentExperienceIdMap = new HashMap<String, List<String>>();
	@Transient
	private List<CVRegistration> registrations = new ArrayList<CVRegistration>();
	@Transient
	private Map<String, StudentExperience> experienceMap = new HashMap<String, StudentExperience>();
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
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public Map<String, List<String>> getStudentExperienceIdMap() {
		return studentExperienceIdMap;
	}
	public void setStudentExperienceIdMap(Map<String, List<String>> studentExperienceIdMap) {
		this.studentExperienceIdMap = studentExperienceIdMap;
	}
	public List<CVRegistration> getRegistrations() {
		return registrations;
	}
	public void setRegistrations(List<CVRegistration> registrations) {
		this.registrations = registrations;
	}
	public Map<String, StudentExperience> getExperienceMap() {
		return experienceMap;
	}
	public void setExperienceMap(Map<String, StudentExperience> experienceMap) {
		this.experienceMap = experienceMap;
	}
}
