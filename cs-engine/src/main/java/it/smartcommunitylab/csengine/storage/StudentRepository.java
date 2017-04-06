package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.Student;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface StudentRepository extends MongoRepository<Student, String>, StudentRepositoryCustom {
	
	@Query(value="{studentId:{$in : ?0}}")
	List<Student> findByIds(List<String> studentIds);
	
}
