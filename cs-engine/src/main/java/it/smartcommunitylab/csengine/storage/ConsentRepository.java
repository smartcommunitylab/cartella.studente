package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.Consent;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ConsentRepository extends MongoRepository<Consent, String> {
	List<Consent> findAll(Sort sort);
	
	@Query(value="{studentId:?0}")
	Consent findByStudent(String studentId);
	
	@Query(value="{subject:?0}")
	Consent findBySubject(String subject);
	
	@Query(value="{origin:?0, extId:?1}")
	Consent findByExtId(String origin, String extId);
}
