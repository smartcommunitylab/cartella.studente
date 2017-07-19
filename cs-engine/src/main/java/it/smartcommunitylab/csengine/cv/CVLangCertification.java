package it.smartcommunitylab.csengine.cv;

public class CVLangCertification {
	private String dateFrom;
	private String dateTo;
	private String lang;
	private String level;
	private String mappedLevel;
	private String name;
	
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getMappedLevel() {
		return mappedLevel;
	}
	public void setMappedLevel(String mappedLevel) {
		this.mappedLevel = mappedLevel;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
}
