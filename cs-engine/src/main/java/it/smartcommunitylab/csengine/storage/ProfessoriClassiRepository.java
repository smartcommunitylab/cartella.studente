package it.smartcommunitylab.csengine.storage;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import it.smartcommunitylab.csengine.model.ProfessoriClassi;

public interface ProfessoriClassiRepository extends MongoRepository<ProfessoriClassi, String> {
	@Query(value = "{origin:?0, extId:?1}")
	ProfessoriClassi findByExtId(String origin, String extId);

	@Query("{'creationDate': {$gte: ?0}}")
	Page<ProfessoriClassi> fetchAllAfterTime(Date date, Pageable pageable);
}
