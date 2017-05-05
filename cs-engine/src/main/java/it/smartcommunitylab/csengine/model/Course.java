package it.smartcommunitylab.csengine.model;

import java.util.Date;

public class Course extends BaseObject {
	private String instituteId;
	private String teachingUnitId;
	private String schoolYear;
	private Date dateFrom;
	private Date dateTo;
	private String course;
	private String teachingUnit;
	
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
	public String getTeachingUnit() {
		return teachingUnit;
	}
	public void setTeachingUnit(String teachingUnit) {
		this.teachingUnit = teachingUnit;
	}
	public String getTeachingUnitId() {
		return teachingUnitId;
	}
	public void setTeachingUnitId(String teachingUnitId) {
		this.teachingUnitId = teachingUnitId;
	}
	public String getInstituteId() {
		return instituteId;
	}
	public void setInstituteId(String instituteId) {
		this.instituteId = instituteId;
	}
	

}
