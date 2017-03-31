package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.CertificationRequest;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CertificationRequestRepository extends MongoRepository<CertificationRequest, String> {

}
