package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.TeachingUnit;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TeachingUnitRepository extends MongoRepository<TeachingUnit, String> {
	
}
