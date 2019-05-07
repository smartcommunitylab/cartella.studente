package it.smartcommunitylab.csengine.storage;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import it.smartcommunitylab.csengine.model.TeachingUnit;

public interface TeachingUnitRepository extends MongoRepository<TeachingUnit, String>, TeachingUnitRepositoryCustom {
	@Query(value="{origin:?0, extId:?1}")
	TeachingUnit findByExtId(String origin, String extId);
	
	@Query("{'creationDate': {$gte: ?0}}")
	Page<TeachingUnit> fetchAllAfterTime(Date  date, Pageable pageable);
}
