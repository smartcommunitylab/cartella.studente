package it.smartcommunitylab.csengine.ui;

import java.util.ArrayList;
import java.util.List;

import it.smartcommunitylab.csengine.model.Experience;
import it.smartcommunitylab.csengine.model.StudentExperience;

public class ExperienceExtended extends Experience {
	private List<StudentExperience> studentExperiences = new ArrayList<StudentExperience>();

	public ExperienceExtended(Experience experience) {
		this.setId(experience.getId());
		this.setOrigin(experience.getOrigin());
		this.setExtId(experience.getExtId());
		this.setType(experience.getType());
		this.setAttributes(experience.getAttributes());
	}

	public List<StudentExperience> getStudentExperiences() {
		return studentExperiences;
	}

	public void setStudentExperiences(List<StudentExperience> studentExperiences) {
		this.studentExperiences = studentExperiences;
	}
}
