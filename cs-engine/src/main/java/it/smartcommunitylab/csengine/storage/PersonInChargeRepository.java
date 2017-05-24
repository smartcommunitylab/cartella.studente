package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.PersonInCharge;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface PersonInChargeRepository extends MongoRepository<PersonInCharge, String> {

	@Query(value="{cf:?0}}")
	PersonInCharge findByCF(String cf);
	
}
