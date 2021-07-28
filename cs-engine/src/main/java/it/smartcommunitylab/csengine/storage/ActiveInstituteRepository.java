package it.smartcommunitylab.csengine.storage;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import it.smartcommunitylab.csengine.model.ActiveInstitute;

public interface ActiveInstituteRepository extends MongoRepository<ActiveInstitute, String> {
	List<ActiveInstitute> findAll(Sort sort);	
}
