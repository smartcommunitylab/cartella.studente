package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.CV;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CVRepository extends MongoRepository<CV, String> {
	List<CV> findAll(Sort sort);
	
	@Query(value="{studentId:?0}")
	CV findByStudent(String studentId);
	
}
