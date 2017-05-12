package it.smartcommunitylab.csengine.ui;

import it.smartcommunitylab.csengine.model.Registration;
import it.smartcommunitylab.csengine.model.TeachingUnit;

import java.util.List;

public class StudentRegistration {
	private TeachingUnit teachingUnit;
	private List<Registration> registrations;
	
	public List<Registration> getRegistrations() {
		return registrations;
	}
	public void setRegistrations(List<Registration> registrations) {
		this.registrations = registrations;
	}
	public TeachingUnit getTeachingUnit() {
		return teachingUnit;
	}
	public void setTeachingUnit(TeachingUnit teachingUnit) {
		this.teachingUnit = teachingUnit;
	}
}
