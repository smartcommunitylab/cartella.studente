package it.smartcommunitylab.csengine.extsource.infotn;

public class Corso {
	private String origin;
	private String extId;
	private String course;
	private String dateFrom;
	private String dateTo;
	private Institute instituteRef;
	private String schoolYear;
	private TeachingUnit teachingUnitRef;

	public String getExtId() {
		return extId;
	}

	public void setExtId(String extId) {
		this.extId = extId;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public Institute getInstituteRef() {
		return instituteRef;
	}

	public void setInstituteRef(Institute instituteRef) {
		this.instituteRef = instituteRef;
	}

	public TeachingUnit getTeachingUnitRef() {
		return teachingUnitRef;
	}

	public void setTeachingUnitRef(TeachingUnit teachingUnitRef) {
		this.teachingUnitRef = teachingUnitRef;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getSchoolYear() {
		return schoolYear;
	}

	public void setSchoolYear(String schoolYear) {
		this.schoolYear = schoolYear;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}
}
