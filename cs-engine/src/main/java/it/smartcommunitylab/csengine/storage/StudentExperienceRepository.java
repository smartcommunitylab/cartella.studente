package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.StudentExperience;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentExperienceRepository extends 
	MongoRepository<StudentExperience, String>, 
	StudentExperienceRepositoryCustom {

}
