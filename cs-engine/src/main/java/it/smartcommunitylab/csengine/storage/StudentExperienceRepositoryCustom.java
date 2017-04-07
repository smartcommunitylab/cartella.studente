package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.Experience;

import java.util.List;

import org.springframework.data.domain.Pageable;


public interface StudentExperienceRepositoryCustom {
	
	public List<Experience> searchExperienceByInstitute(String expType, String instituteId, String schoolYear, 
			Long dateFrom, Long dateTo, String text, Pageable pageable);
	
	public List<Experience> searchExperienceByStudent(String expType, String studentId, 
			Long dateFrom, Long dateTo, String text, Pageable pageable);
	
	public List<Experience> searchExperienceByCertifier(String expType, String certifierId, 
			Long dateFrom, Long dateTo, String text, Pageable pageable);
	
}
