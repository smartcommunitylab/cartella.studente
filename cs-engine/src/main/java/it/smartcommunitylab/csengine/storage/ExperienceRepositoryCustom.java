package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.Experience;

import java.util.List;

import org.springframework.data.domain.Pageable;


public interface ExperienceRepositoryCustom {
	
	List<Experience> searchExperience(String expType, Boolean institutional,
			String instituteId, String schoolYear, String certifierId, Long dateFrom, Long dateTo,
			String text, Pageable pageable);
	
}
