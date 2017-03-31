package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.Registration;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RegistrationRepository extends MongoRepository<Registration, String> {

}
