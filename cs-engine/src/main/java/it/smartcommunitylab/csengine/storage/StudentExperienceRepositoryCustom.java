package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.StudentExperience;

import java.util.List;

import org.springframework.data.domain.Pageable;


public interface StudentExperienceRepositoryCustom {
	
	public List<StudentExperience> searchStudentExperience(String studentId, String expType,
			Boolean institutional, String instituteId, String schoolYear, String certifierId,
			Long dateFrom, Long dateTo, String text, Pageable pageable);
	
}
