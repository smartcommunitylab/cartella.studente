package it.smartcommunitylab.csengine.storage;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import it.smartcommunitylab.csengine.model.User;

public interface UserRepository extends MongoRepository<User, String> {
	@Query(value="{cf:?0}")
	User findByCf(String cf);
	
	@Query(value="{email:?0}")
	User findByEmail(String email);
	
	@Query(value="{origin:?0, extId:?1}")
	User findByExtId(String origin, String extId);
	
	@Query(value="{originalId:?0}")
	User findByOriginalId(String originalId);
}
