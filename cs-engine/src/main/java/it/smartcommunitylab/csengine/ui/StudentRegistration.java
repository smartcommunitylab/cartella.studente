package it.smartcommunitylab.csengine.ui;

import java.util.List;

import it.smartcommunitylab.csengine.model.Institute;
import it.smartcommunitylab.csengine.model.Registration;

public class StudentRegistration {
	private Institute institute;
	private List<Registration> registrations;
	
	public Institute getInstitute() {
		return institute;
	}
	public void setInstitute(Institute institute) {
		this.institute = institute;
	}
	public List<Registration> getRegistrations() {
		return registrations;
	}
	public void setRegistrations(List<Registration> registrations) {
		this.registrations = registrations;
	}
}
