package it.smartcommunitylab.csengine.storage;

import java.util.List;

import it.smartcommunitylab.csengine.model.StudentExperience;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface StudentExperienceRepository extends 
	MongoRepository<StudentExperience, String>, 
	StudentExperienceRepositoryCustom {
	
	@Query(value="{studentId:{$in : ?0}, experienceId:?1}")
	List<StudentExperience> findByStudentsAndExperience(List<String> studentIds, String experienceId);
	
	@Query(value="{studentId:?0, experienceId:?1}")
	List<StudentExperience> findByStudentAndExperience(String studentId, String experienceId);
}
