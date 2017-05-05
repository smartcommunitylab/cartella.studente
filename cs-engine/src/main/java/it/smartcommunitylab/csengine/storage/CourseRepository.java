package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.Course;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CourseRepository extends MongoRepository<Course, String> {
	List<Course> findAll(Sort sort);
	
	@Query(value="{instituteId:?0, schoolYear:?1}")
	List<Course> findByInstitute(String instituteId, String schoolYear);
	
	@Query(value="{teachingUnitId:?0, schoolYear:?1}")
	List<Course> findByTeachingUnit(String teachingUnitId, String schoolYear);
	
	@Query(value="{origin:?0, extId:?1}")
	Course findByExtId(String origin, String extId);
}
