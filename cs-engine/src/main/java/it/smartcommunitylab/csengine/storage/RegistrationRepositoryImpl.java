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
	public List<Registration> searchRegistration(String studentId, String instituteId,
			String schoolYear, Long dateFrom, Long dateTo, Pageable pageable) {
		Criteria criteria = new Criteria("instituteId").is(instituteId).and("schoolYear").is(schoolYear);
		if(Utils.isNotEmpty(studentId)) {
			criteria = criteria.andOperator(new Criteria("studentId").is(studentId));
		}
		if(dateFrom != null) {
			criteria = criteria.andOperator(new Criteria("dateFrom").gte(new Date(dateFrom)));
		}
		if(dateTo != null) {
			criteria = criteria.andOperator(new Criteria("dateFrom").lte(new Date(dateTo)));
		}
		Query query = new Query(criteria).with(pageable);
		List<Registration> result = mongoTemplate.find(query, Registration.class);
		return result;
	}

}
