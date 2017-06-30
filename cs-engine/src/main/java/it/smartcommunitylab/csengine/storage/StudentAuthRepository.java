package it.smartcommunitylab.csengine.storage;

import java.util.List;

import it.smartcommunitylab.csengine.model.StudentAuth;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface StudentAuthRepository extends MongoRepository<StudentAuth, String> {
	
	@Query(value="{studentId:?0}")
	List<StudentAuth> findByStudent(String studentId);
}
