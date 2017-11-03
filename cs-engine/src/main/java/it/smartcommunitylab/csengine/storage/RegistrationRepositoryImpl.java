package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.model.Registration;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class RegistrationRepositoryImpl implements RegistrationRepositoryCustom {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<Registration> searchRegistration(String studentId, String teachingUnitId,
			String schoolYear, Long dateFrom, Long dateTo, Pageable pageable) {
		Criteria criteria = new Criteria("teachingUnitId").is(teachingUnitId)
				.and("schoolYear").is(schoolYear);
		if(Utils.isNotEmpty(studentId)) {
			criteria = criteria.and("studentId").is(studentId);
		}
		if(dateFrom != null) {
			criteria = criteria.and("dateFrom").gte(new Date(dateFrom));
		}
		if(dateTo != null) {
			criteria = criteria.and("dateFrom").lte(new Date(dateTo));
		}
		Query query = new Query(criteria);
		if(pageable != null) {
			query = query.with(pageable);
		}
		List<Registration> result = mongoTemplate.find(query, Registration.class);
		return result;
	}

	@Override
	public List<Registration> findByClassification(String typologyName, 
			String schoolYear) {
		Criteria criteria = new Criteria("schoolYear").is(schoolYear)
			.and("teachingUnit.classifications." + typologyName).exists(true);
		Query query = new Query(criteria);
		List<Registration> result = mongoTemplate.find(query, Registration.class);
		return result;
	}

}
