package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.Student;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentRepository extends MongoRepository<Student, String> {

}
