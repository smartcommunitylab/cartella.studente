package it.smartcommunitylab.csengine.model;

public class CourseMetaInfo extends BaseObject {
	private String course;
	private String codMiur;
	private Integer years;

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public String getCodMiur() {
		return codMiur;
	}

	public void setCodMiur(String codMiur) {
		this.codMiur = codMiur;
	}

	public Integer getYears() {
		return years;
	}

	public void setYears(Integer years) {
		this.years = years;
	}

}