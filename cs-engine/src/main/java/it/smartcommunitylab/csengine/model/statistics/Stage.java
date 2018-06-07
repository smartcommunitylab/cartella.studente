package it.smartcommunitylab.csengine.model.statistics;

public class Stage {

	private String title;
	private Long dateFrom;
	private int duration;
	
	private String schoolYear;
	
	private Boolean certified;
	private Boolean institutional;
	
	private String location;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Long dateFrom) {
		this.dateFrom = dateFrom;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getSchoolYear() {
		return schoolYear;
	}

	public void setSchoolYear(String schoolYear) {
		this.schoolYear = schoolYear;
	}

	public Boolean getCertified() {
		return certified;
	}

	public void setCertified(Boolean certified) {
		this.certified = certified;
	}

	public Boolean getInstitutional() {
		return institutional;
	}

	public void setInstitutional(Boolean institutional) {
		this.institutional = institutional;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	
	
}
