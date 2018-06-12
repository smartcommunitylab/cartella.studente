package it.smartcommunitylab.csengine.storage;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import it.smartcommunitylab.csengine.model.Professor;

public interface ProfessoriRepository extends MongoRepository<Professor, String> {
	@Query(value="{origin:?0, extId:?1}")
	Professor findByExtId(String origin, String extId);
}

