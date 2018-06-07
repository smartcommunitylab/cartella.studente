package it.smartcommunitylab.csengine.model.statistics;

import java.util.List;

import com.google.common.collect.Lists;

public class StudentProfile {

	private String name;
	private String surname;
	private String birthdate;
	
	private List<SchoolRegistration> registrations = Lists.newArrayList();
	private List<Stage> stages = Lists.newArrayList();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public List<SchoolRegistration> getRegistrations() {
		return registrations;
	}

	public void setRegistrations(List<SchoolRegistration> registrations) {
		this.registrations = registrations;
	}

	public List<Stage> getStages() {
		return stages;
	}

	public void setStages(List<Stage> stages) {
		this.stages = stages;
	}

}
