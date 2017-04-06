package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.Registration;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface RegistrationRepository extends MongoRepository<Registration, String>,
	RegistrationRepositoryCustom {

	@Query(value="{instituteId:?0, schoolYear:?1}")
	Page<Registration> findByInstitute(String instituteId, String schoolYear, Pageable pageable);
}
