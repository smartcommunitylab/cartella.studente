package it.smartcommunitylab.csengine.extsource.infotn;

public class Stage {
	private String extId;
	private String origin;
	private CompanyRef companyRef;
	private Corso courseRef;
	private String dateFrom;
	private String dateTo;
	private String duration;
	private String location;
	private String type;

	// private String extid_institute;
	// private String origin_company;
	// private String extid_company;
	private String schoolyear;

	private String tutor;

	private String title;

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getExtId() {
		return extId;
	}

	public void setExtId(String extId) {
		this.extId = extId;
	}

	public Corso getCourseRef() {
		return courseRef;
	}

	public void setCourseRef(Corso courseRef) {
		this.courseRef = courseRef;
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

	public String getSchoolyear() {
		return schoolyear;
	}

	public void setSchoolyear(String schoolyear) {
		this.schoolyear = schoolyear;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTutor() {
		return tutor;
	}

	public void setTutor(String tutor) {
		this.tutor = tutor;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public CompanyRef getCompanyRef() {
		return companyRef;
	}

	public void setCompanyRef(CompanyRef companyRef) {
		this.companyRef = companyRef;
	}

}
