package it.smartcommunitylab.csengine.storage;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import it.smartcommunitylab.csengine.model.Professor;

public interface ProfessoriRepository extends MongoRepository<Professor, String> {
	@Query(value="{origin:?0, extId:?1}")
	Professor findByExtId(String origin, String extId);

	@Query("{'creationDate': {$gte: ?0}}")
	Page<Professor> fetchAllAfterTime(Date date, Pageable pageable);
}

