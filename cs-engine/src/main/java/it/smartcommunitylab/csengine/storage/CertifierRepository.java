package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.Certifier;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CertifierRepository extends MongoRepository<Certifier, String> {
	@Query(value="{origin:?0, extId:?1}")
	Certifier findByExtId(String origin, String extId);
}
