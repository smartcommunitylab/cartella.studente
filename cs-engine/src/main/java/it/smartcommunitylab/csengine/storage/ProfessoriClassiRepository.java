package it.smartcommunitylab.csengine.storage;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import it.smartcommunitylab.csengine.model.ProfessoriClassi;

public interface ProfessoriClassiRepository extends MongoRepository<ProfessoriClassi, String> {
	@Query(value = "{origin:?0, extId:?1}")
	ProfessoriClassi findByExtId(String origin, String extId);
}
