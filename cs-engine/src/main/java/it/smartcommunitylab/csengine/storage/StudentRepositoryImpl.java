package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.model.Registration;
import it.smartcommunitylab.csengine.model.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class StudentRepositoryImpl implements StudentRepositoryCustom {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<Student> findByInstitute(String teachingUnitId, String schoolYear, Pageable pageable) {
		Criteria criteria = new Criteria("teachingUnitId").is(teachingUnitId).and("schoolYear").is(schoolYear);
		Query query = new Query(criteria);
		if(pageable != null) {
			query = query.with(pageable);
		}
		List<Registration> registrations = mongoTemplate.find(query, Registration.class);
		List<Student> result = aggregateByStudent(registrations);
		return result;
	}
	
	@Override
	public List<Student> findByCertifier(String certifierId, Pageable pageable) {
		Criteria criteria = new Criteria("experience.attributes." + Const.ATTR_CERTIFIERID).is(certifierId);
		Query query = new Query(criteria);
		if(pageable != null) {
			query = query.with(pageable);
		}
		List<Registration> registrations = mongoTemplate.find(query, Registration.class);
		List<Student> result = aggregateByStudent(registrations);
		return result;
	}

	@Override
	public List<Student> findByExperience(String experienceId, String teachingUnitId,
			String schoolYear, Pageable pageable) {
		Criteria criteria = Criteria.where("experienceId").is(experienceId);
		if(Utils.isNotEmpty(teachingUnitId)) {
			criteria = criteria.andOperator(new Criteria(
					"experience.attributes." + Const.ATTR_TUID).is(teachingUnitId));
		}
		if(Utils.isNotEmpty(schoolYear)) {
			criteria = criteria.andOperator(new Criteria(
					"experience.attributes." + Const.ATTR_SCHOOLYEAR).is(schoolYear));
		}
		Query query = new Query(criteria);
		if(pageable != null) {
			query = query.with(pageable);
		}
		List<Registration> registrations = mongoTemplate.find(query, Registration.class);
		List<Student> result = aggregateByStudent(registrations);
		return result;
	}
	
	private List<Student> aggregateByStudent(List<Registration> registrations) {
		Map<String, Student> result = new HashMap<String, Student>();
		for(Registration registration : registrations) {
			String studentId = registration.getStudentId();
			if(result.containsKey(studentId)) {
				continue;
			}
			result.put(studentId, registration.getStudent());
		}
		return new ArrayList<Student>(result.values());
	}

}
