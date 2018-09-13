package it.smartcommunitylab.csengine.storage;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import it.smartcommunitylab.csengine.model.Student;

public interface StudentRepository extends MongoRepository<Student, String>, StudentRepositoryCustom {

	@Query(value = "{studentId:{$in : ?0}}")
	List<Student> findByIds(List<String> studentIds);

	@Query(value = "{cf:?0}")
	Student findByCF(String cf);

	@Query(value = "{origin:?0, extId:?1}")
	Student findByExtId(String origin, String extId);

	@Query("{'creationDate': {$gte: ?0}}")
	Page<Student> fetchAllAfterTime(Date date, Pageable pageable);
}
