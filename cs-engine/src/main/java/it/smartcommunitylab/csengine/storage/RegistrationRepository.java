package it.smartcommunitylab.csengine.storage;

import java.util.List;

import it.smartcommunitylab.csengine.model.Registration;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface RegistrationRepository extends MongoRepository<Registration, String>,
	RegistrationRepositoryCustom {

	@Query(value="{instituteId:?0, schoolYear:?1}")
	Page<Registration> findByInstitute(String instituteId, String schoolYear, Pageable pageable);
	
	@Query(value="{teachingUnitId:?0, schoolYear:?1}")
	Page<Registration> findByTeachingUnit(String teachingUnitId, String schoolYear, Pageable pageable);
	
	@Query(value="{studentId:?0}")
	List<Registration> findByStudent(String studentId);
	
	@Query(value="{courseId:?0}")
	List<Registration> findByCourse(String courseId);
	
	@Query(value="{origin:?0, extId:?1}")
	Registration findByExtId(String origin, String extId);

}
