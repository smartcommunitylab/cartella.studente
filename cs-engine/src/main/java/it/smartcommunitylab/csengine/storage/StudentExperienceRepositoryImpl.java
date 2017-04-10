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
	public List<StudentExperience> searchExperienceByInstitute(String expType, String instituteId,
			String schoolYear, Long dateFrom, Long dateTo, String text, Pageable pageable) {
		Criteria criteria = new Criteria("experience.type").is(expType)
				.and("experience.attributes." + Const.ATTR_INSTITUTIONAL).is(Boolean.TRUE);
		if(Utils.isNotEmpty(instituteId) && Utils.isNotEmpty(schoolYear)) {
			criteria = criteria.and("experience.attributes." + Const.ATTR_INSTITUTEID).is(instituteId)
					.and("experience.attributes." + Const.ATTR_SCHOOLYEAR).is(schoolYear);
		}
		if(dateFrom != null) {
			criteria = criteria.and("experience.attributes." + Const.ATTR_DATEFROM).gte(new Date(dateFrom));
		}
		if(dateTo != null) {
			criteria = criteria.and("experience.attributes." + Const.ATTR_DATEFROM).lte(new Date(dateTo));
		}
		Query query = new Query(criteria).with(pageable);
		List<StudentExperience> result = mongoTemplate.find(query, StudentExperience.class);
		return result;
	}

	@Override
	public List<StudentExperience> searchExperienceByStudent(String studentId, String expType, Boolean institutional, 
			String instituteId,	String schoolYear, String certifierId, Long dateFrom, Long dateTo, 
			String text, Pageable pageable) {
		Criteria criteria = new Criteria("experience.type").is(expType)
				.and("studentId").is(studentId)
				.and("experience.attributes." + Const.ATTR_INSTITUTIONAL).is(institutional);
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
		Query query = new Query(criteria).with(pageable);
		List<StudentExperience> result = mongoTemplate.find(query, StudentExperience.class);
		return result;
	}

	@Override
	public List<StudentExperience> searchExperienceByCertifier(String expType, String certifierId,
			Long dateFrom, Long dateTo, String text, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

}
