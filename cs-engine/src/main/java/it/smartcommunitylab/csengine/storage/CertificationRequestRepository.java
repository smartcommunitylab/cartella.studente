package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.CertificationRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CertificationRequestRepository extends MongoRepository<CertificationRequest, String> {
	
	Page<CertificationRequest> findByCertifierId(String certifierId, Pageable pageable);
	
	Page<CertificationRequest> findByStudentId(String studentId, Pageable pageable);
	
}
