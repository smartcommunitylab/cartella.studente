package it.smartcommunitylab.csengine.model;


public class StudentExperience extends BaseObject {
	private String studentId;
	private String experienceId;
	private Experience experience;
	private Student student; 
	private Certificate certificate;
	
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
	public Experience getExperience() {
		return experience;
	}
	public void setExperience(Experience experience) {
		this.experience = experience;
	}
	public Certificate getCertificate() {
		return certificate;
	}
	public void setCertificate(Certificate certificate) {
		this.certificate = certificate;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	
}
