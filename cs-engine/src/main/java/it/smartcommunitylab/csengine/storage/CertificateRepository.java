package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.Certificate;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CertificateRepository extends MongoRepository<Certificate, String> {

}
