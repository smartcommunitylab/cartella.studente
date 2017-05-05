package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.Student;

import java.util.List;

import org.springframework.data.domain.Pageable;

public interface StudentRepositoryCustom {
	
	List<Student> findByInstitute(String teachingUnitId, String schoolYear, Pageable pageable);
	
	List<Student> findByCertifier(String certifierId, Pageable pageable);
	
	List<Student> findByExperience(String experienceId, String teachingUnitId,	
			String schoolYear, Pageable pageable);
	
}
