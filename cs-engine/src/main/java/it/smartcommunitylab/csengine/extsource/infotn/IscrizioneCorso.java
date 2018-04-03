package it.smartcommunitylab.csengine.extsource.infotn;

public class IscrizioneCorso {
	private String origin;
	private String extId;
	private CourseRef courseRef;
	private StudentRef student;

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

	public CourseRef getCourseRef() {
		return courseRef;
	}

	public void setCourseRef(CourseRef courseRef) {
		this.courseRef = courseRef;
	}

	public StudentRef getStudent() {
		return student;
	}

	public void setStudent(StudentRef student) {
		this.student = student;
	}

}

class CourseRef {
	private String extId;
	private String origin;

	public String getExtId() {
		return extId;
	}

	public void setExtId(String extId) {
		this.extId = extId;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

}

class StudentRef {
	private String extId;
	private String origin;
	private String classRoom;
	private String dateFrom;
	private String dateTo;

	public String getExtId() {
		return extId;
	}

	public void setExtId(String extId) {
		this.extId = extId;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getClassRoom() {
		return classRoom;
	}

	public void setClassRoom(String classRoom) {
		this.classRoom = classRoom;
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
