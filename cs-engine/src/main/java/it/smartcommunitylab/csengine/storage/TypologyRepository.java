package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.Typology;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface TypologyRepository extends MongoRepository<Typology, String> {
	List<Typology> findAll(Sort sort);
	
	@Query(value="{qualifiedName:?0}")
	List<Typology> findByQName(String qName);
	
}
