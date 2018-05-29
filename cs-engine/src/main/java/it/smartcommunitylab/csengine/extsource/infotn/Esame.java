package it.smartcommunitylab.csengine.extsource.infotn;

public class Esame {
	private String extId;
	private String origin;
	private String dateFrom;
	private String dateTo;
	private Institute instituteRef;
	private String qualification;
	private String schoolyear;
	private String type;

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
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

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

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

}
