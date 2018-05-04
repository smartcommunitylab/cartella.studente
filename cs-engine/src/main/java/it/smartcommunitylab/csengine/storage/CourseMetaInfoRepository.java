package it.smartcommunitylab.csengine.storage;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import it.smartcommunitylab.csengine.model.CourseMetaInfo;

public interface CourseMetaInfoRepository extends MongoRepository<CourseMetaInfo, String> {

	@Query(value = "{origin:?0, extId:?1}")
	CourseMetaInfo findByExtId(String origin, String extId);
}
