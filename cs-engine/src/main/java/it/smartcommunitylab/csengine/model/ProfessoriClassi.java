package it.smartcommunitylab.csengine.model;

import it.smartcommunitylab.csengine.extsource.infotn.Corso;

public class ProfessoriClassi extends BaseObject {

	private String classroom;
	private Corso course;
	private String datefrom;
	private String dateto;
	private String schoolyear;
	private Teacher teacher;

	public String getClassroom() {
		return classroom;
	}

	public void setClassroom(String classroom) {
		this.classroom = classroom;
	}

	public Corso getCourse() {
		return course;
	}

	public void setCourse(Corso course) {
		this.course = course;
	}

	public String getDatefrom() {
		return datefrom;
	}

	public void setDatefrom(String datefrom) {
		this.datefrom = datefrom;
	}

	public String getDateto() {
		return dateto;
	}

	public void setDateto(String dateto) {
		this.dateto = dateto;
	}

	public String getSchoolyear() {
		return schoolyear;
	}

	public void setSchoolyear(String schoolyear) {
		this.schoolyear = schoolyear;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}
}
