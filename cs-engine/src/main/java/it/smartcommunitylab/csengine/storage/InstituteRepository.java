package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.Institute;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InstituteRepository extends MongoRepository<Institute, String> {
	List<Institute> findAll(Sort sort);
}
