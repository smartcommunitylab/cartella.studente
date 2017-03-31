package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.PersonInCharge;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PersonInChargeRepository extends MongoRepository<PersonInCharge, String> {

}
