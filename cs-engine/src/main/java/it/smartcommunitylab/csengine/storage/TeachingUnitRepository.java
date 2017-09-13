package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.TeachingUnit;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface TeachingUnitRepository extends MongoRepository<TeachingUnit, String>, TeachingUnitRepositoryCustom {
	@Query(value="{origin:?0, extId:?1}")
	TeachingUnit findByExtId(String origin, String extId);
}
