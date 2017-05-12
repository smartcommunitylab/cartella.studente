package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.StudentExperience;

import java.util.List;

import org.springframework.data.domain.Pageable;


public interface StudentExperienceRepositoryCustom {
	
	public List<StudentExperience> searchExperience(String studentId, String expType, Boolean institutional, 
			String instituteId, String teachingUnitId, String schoolYear, String certifierId, Long dateFrom, Long dateTo, 
			String text, Pageable pageable);
	
	public List<StudentExperience> searchExperienceById(String studentId, String instituteId,
			String teachingUnitId, String experienceId, Boolean institutional);
	
}
