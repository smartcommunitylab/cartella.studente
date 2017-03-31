package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.Experience;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExperienceRepository extends 
	MongoRepository<Experience, String>,
	ExperienceRepositoryCustom {

}
