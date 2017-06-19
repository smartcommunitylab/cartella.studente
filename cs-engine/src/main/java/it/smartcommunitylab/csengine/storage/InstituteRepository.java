package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.Institute;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface InstituteRepository extends MongoRepository<Institute, String> {
	List<Institute> findAll(Sort sort);
	
	@Query(value="{origin:?0, extId:?1}")
	Institute findByExtId(String origin, String extId);
}
