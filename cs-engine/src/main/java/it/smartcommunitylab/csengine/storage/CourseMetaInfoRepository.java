package it.smartcommunitylab.csengine.storage;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import it.smartcommunitylab.csengine.model.CourseMetaInfo;

public interface CourseMetaInfoRepository extends MongoRepository<CourseMetaInfo, String> {

	@Query(value = "{origin:?0, extId:?1}")
	CourseMetaInfo findByExtId(String origin, String extId);

	@Query("{'lastUpdate': {$gte: ?0}}")
	Page<CourseMetaInfo> fetchAllAfterTime(Date date, Pageable pageable);
}
