package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.TeachingUnit;
import it.smartcommunitylab.csengine.model.Typology;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class TeachingUnitRepositoryImpl implements TeachingUnitRepositoryCustom {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<TeachingUnit> findByClassification(List<Typology> classification) {
		Criteria criteria = new Criteria();
		for(Typology typology : classification) {
			criteria = criteria.and("classifications." + typology.getQualifiedName() + ".name").is(typology.getName());
		}
		Query query = new Query(criteria);
		List<TeachingUnit> result = mongoTemplate.find(query, TeachingUnit.class);
		return result;
	}

}
