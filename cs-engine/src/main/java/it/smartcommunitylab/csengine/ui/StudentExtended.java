package it.smartcommunitylab.csengine.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.smartcommunitylab.csengine.model.Student;
import it.smartcommunitylab.csengine.model.StudentExperience;

public class StudentExtended extends Student {
	private Map<String, List<StudentExperience>> experienceMap = new HashMap<String, List<StudentExperience>>();

	public Map<String, List<StudentExperience>> getExperienceMap() {
		return experienceMap;
	}

	public void setExperienceMap(Map<String, List<StudentExperience>> experienceMap) {
		this.experienceMap = experienceMap;
	}
}
