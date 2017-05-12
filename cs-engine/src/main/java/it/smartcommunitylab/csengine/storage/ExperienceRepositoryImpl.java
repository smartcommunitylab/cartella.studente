package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.model.Experience;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class ExperienceRepositoryImpl implements ExperienceRepositoryCustom {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<Experience> searchExperience(String expType, Boolean institutional, String instituteId, 
			String teachingUnitId, String schoolYear, String certifierId, Long dateFrom, Long dateTo,
			String text, Pageable pageable) {
		Criteria criteria = new Criteria("type").is(expType)
				.and("attributes." + Const.ATTR_INSTITUTIONAL).is(institutional);
		if(Utils.isNotEmpty(instituteId)) {
			criteria = criteria.and("experience.attributes." + Const.ATTR_INSTITUTEID).is(instituteId);
		}
		if(Utils.isNotEmpty(teachingUnitId)) {
			criteria = criteria.and("experience.attributes." + Const.ATTR_TUID).is(teachingUnitId);
		}
		if(Utils.isNotEmpty(schoolYear)) {
			criteria = criteria.and("experience.attributes." + Const.ATTR_SCHOOLYEAR).is(schoolYear);
		}
		if(Utils.isNotEmpty(certifierId)) {
			criteria = criteria.and("attributes." + Const.ATTR_CERTIFIERID).is(certifierId);
		}
		if(dateFrom != null) {
			criteria = criteria.and("attributes." + Const.ATTR_DATEFROM).gte(new Date(dateFrom));
		}
		if(dateTo != null) {
			criteria = criteria.and("attributes." + Const.ATTR_DATEFROM).lte(new Date(dateTo));
		}
		Query query = new Query(criteria).with(pageable);
		List<Experience> result = mongoTemplate.find(query, Experience.class);
		return result;
	}

}
