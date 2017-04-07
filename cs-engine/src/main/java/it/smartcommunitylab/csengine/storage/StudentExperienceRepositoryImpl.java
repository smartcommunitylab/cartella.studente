package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.Experience;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;

public class StudentExperienceRepositoryImpl implements StudentExperienceRepositoryCustom {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<Experience> searchExperienceByInstitute(String expType, String instituteId,
			String schoolYear, Long dateFrom, Long dateTo, String text, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Experience> searchExperienceByStudent(String expType, String studentId,
			Long dateFrom, Long dateTo, String text, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Experience> searchExperienceByCertifier(String expType, String certifierId,
			Long dateFrom, Long dateTo, String text, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

}
