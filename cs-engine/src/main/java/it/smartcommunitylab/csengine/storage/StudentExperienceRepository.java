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
	StudentExperience findByStudentAndExperience(String studentId, String experienceId);
	
	@Query(value="{experienceId:?0}")
	List<StudentExperience> findByExperienceId(String experienceId);
	
	@Query(value="{experienceId:{$in : ?0}}")
	List<StudentExperience> findByExperienceIds(List<String> experienceIds);
	
	@Query(value="{origin:?0, extId:?1}")
	StudentExperience findByExtId(String origin, String extId);
	
}
