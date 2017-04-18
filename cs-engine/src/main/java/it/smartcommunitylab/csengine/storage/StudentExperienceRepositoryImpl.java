package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.model.StudentExperience;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class StudentExperienceRepositoryImpl implements StudentExperienceRepositoryCustom {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<StudentExperience> searchExperienceById(String studentId, String instituteId,
			String experienceId) {
		Criteria criteria = new Criteria("experienceId").is(experienceId);
		if(Utils.isNotEmpty(studentId)) {
			criteria = criteria.and("studentId").is(studentId);
		}
		if(Utils.isNotEmpty(instituteId)) {
			criteria = criteria.and("experience.attributes." + Const.ATTR_INSTITUTEID).is(instituteId);
		}
		Query query = new Query(criteria);
		List<StudentExperience> result = mongoTemplate.find(query, StudentExperience.class);
		return result;
	}

	@Override
	public List<StudentExperience> searchExperience(String studentId, String expType,
			Boolean institutional, String instituteId, String schoolYear, String certifierId,
			Long dateFrom, Long dateTo, String text, Pageable pageable) {
		Criteria criteria = new Criteria();
		if(Utils.isNotEmpty(studentId)) {
			criteria = criteria.and("studentId").is(studentId);
		}
		if(Utils.isNotEmpty(expType)) {
			criteria = criteria.and("experience.type").is(expType);
		}
		if(institutional != null) {
			criteria = criteria.and("experience.attributes." + Const.ATTR_INSTITUTIONAL).is(institutional);
		}
		if(Utils.isNotEmpty(instituteId)) {
			criteria = criteria.and("experience.attributes." + Const.ATTR_INSTITUTEID).is(instituteId);
		}
		if(Utils.isNotEmpty(schoolYear)) {
			criteria = criteria.and("experience.attributes." + Const.ATTR_SCHOOLYEAR).is(schoolYear);
		}
		if(Utils.isNotEmpty(certifierId)) {
			criteria = criteria.and("experience.attributes." + Const.ATTR_CERTIFIERID).is(certifierId);
		}
		if(dateFrom != null) {
			criteria = criteria.and("experience.attributes." + Const.ATTR_DATEFROM).gte(new Date(dateFrom));
		}
		if(dateTo != null) {
			criteria = criteria.and("experience.attributes." + Const.ATTR_DATEFROM).lte(new Date(dateTo));
		}
		Query query = new Query(criteria);
		if(pageable != null) {
			query = query.with(pageable);
		}
		List<StudentExperience> result = mongoTemplate.find(query, StudentExperience.class);
		return result;
	}

}
