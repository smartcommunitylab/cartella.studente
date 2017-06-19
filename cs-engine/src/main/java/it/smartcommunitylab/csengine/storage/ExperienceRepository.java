package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.Experience;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ExperienceRepository extends MongoRepository<Experience, String>,
	ExperienceRepositoryCustom {
	
	@Query(value="{origin:?0, extId:?1}")
	Experience findByExtId(String origin, String extId);
}
