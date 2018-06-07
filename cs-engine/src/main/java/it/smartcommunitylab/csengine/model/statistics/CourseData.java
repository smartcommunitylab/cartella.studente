package it.smartcommunitylab.csengine.model.statistics;

import java.util.Date;

public class CourseData {

	private String id;
	
	private String course;
	private Date dateFrom;
	private Date dateTo;
	private String miurCode;

	private String instituteId;
	private String teachingUnitId;
	
	private String schoolYear;

	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String name) {
		this.course = name;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date from) {
		this.dateFrom = from;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date to) {
		this.dateTo = to;
	}

	public String getMiurCode() {
		return miurCode;
	}

	public void setMiurCode(String miurCode) {
		this.miurCode = miurCode;
	}

	public String getInstituteId() {
		return instituteId;
	}

	public void setInstituteId(String instituteId) {
		this.instituteId = instituteId;
	}

	public String getTeachingUnitId() {
		return teachingUnitId;
	}

	public void setTeachingUnitId(String teachingUnit) {
		this.teachingUnitId = teachingUnit;
	}

	public String getSchoolYear() {
		return schoolYear;
	}

	public void setSchoolYear(String schoolYear) {
		this.schoolYear = schoolYear;
	}

}
