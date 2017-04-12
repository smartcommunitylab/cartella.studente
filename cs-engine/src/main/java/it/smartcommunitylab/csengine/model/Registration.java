package it.smartcommunitylab.csengine.model;

import java.util.Date;

public class Registration extends BaseObject {
	private String studentId;
	private String instituteId;
	private String schoolYear;
	private Date dateFrom;
	private Date dateTo;
	private String course;
	private String teachingUnit;
	private String classroom;
	private String courseOrigin;
	private String courseExtId; 
	private Institute institute;
	private Student student; 
	
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public String getInstituteId() {
		return instituteId;
	}
	public void setInstituteId(String instituteId) {
		this.instituteId = instituteId;
	}
	public String getSchoolYear() {
		return schoolYear;
	}
	public void setSchoolYear(String schoolYear) {
		this.schoolYear = schoolYear;
	}
	public Date getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}
	public Date getDateTo() {
		return dateTo;
	}
	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}
	public String getCourse() {
		return course;
	}
	public void setCourse(String course) {
		this.course = course;
	}
	public String getClassroom() {
		return classroom;
	}
	public void setClassroom(String classroom) {
		this.classroom = classroom;
	}
	public Institute getInstitute() {
		return institute;
	}
	public void setInstitute(Institute institute) {
		this.institute = institute;
	}
	public String getCourseOrigin() {
		return courseOrigin;
	}
	public void setCourseOrigin(String courseOrigin) {
		this.courseOrigin = courseOrigin;
	}
	public String getCourseExtId() {
		return courseExtId;
	}
	public void setCourseExtId(String courseExtId) {
		this.courseExtId = courseExtId;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public String getTeachingUnit() {
		return teachingUnit;
	}
	public void setTeachingUnit(String teachingUnit) {
		this.teachingUnit = teachingUnit;
	}
}
