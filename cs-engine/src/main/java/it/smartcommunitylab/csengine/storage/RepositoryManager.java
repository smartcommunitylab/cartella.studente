package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.exception.EntityNotFoundException;
import it.smartcommunitylab.csengine.model.Certificate;
import it.smartcommunitylab.csengine.model.CertificationRequest;
import it.smartcommunitylab.csengine.model.Experience;
import it.smartcommunitylab.csengine.model.Institute;
import it.smartcommunitylab.csengine.model.Student;
import it.smartcommunitylab.csengine.model.StudentExperience;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.data.mongodb.core.index.TextIndexDefinition.TextIndexDefinitionBuilder;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class RepositoryManager {
	private static final transient Logger logger = LoggerFactory.getLogger(RepositoryManager.class);
	
	@Autowired
	private InstituteRepository instituteRepository;
	
	@Autowired
	private ExperienceRepository experienceRepository;
	
	@Autowired
	private StudentExperienceRepository studentExperienceRepository;
	
	private MongoTemplate mongoTemplate;
	private String defaultLang;
	
	public RepositoryManager(MongoTemplate template, String defaultLang) {
		this.mongoTemplate = template;
		this.defaultLang = defaultLang;
		TextIndexDefinition textIndex = new TextIndexDefinitionBuilder()
	  .onField("type")
	  .onField("attributes." + Const.ATTR_TITLE)
	  .onField("attributes." + Const.ATTR_DESCRIPTION)
	  .onField("attributes." + Const.ATTR_CATEGORIZATION)
	  .build(); 
		this.mongoTemplate.indexOps(Experience.class).ensureIndex(textIndex);
		this.mongoTemplate.indexOps(StudentExperience.class).ensureIndex(textIndex);
		//this.mongoTemplate.indexOps(Poi.class).ensureIndex(new GeospatialIndex("coordinates"));
	}
	
	public String getDefaultLang() {
		return defaultLang;
	}

	public List<?> findData(Class<?> entityClass, Criteria criteria, Sort sort, String ownerId)
			throws ClassNotFoundException {
		Query query = null;
		if (criteria != null) {
			query = new Query(new Criteria("ownerId").is(ownerId).andOperator(criteria));
		} else {
			query = new Query(new Criteria("ownerId").is(ownerId));
		}
		if (sort != null) {
			query.with(sort);
		}
		query.limit(5000);
		List<?> result = mongoTemplate.find(query, entityClass);
		return result;
	}

	public <T> T findOneData(Class<T> entityClass, Criteria criteria, String ownerId)
			throws ClassNotFoundException {
		Query query = null;
		if (criteria != null) {
			query = new Query(new Criteria("ownerId").is(ownerId).andOperator(criteria));
		} else {
			query = new Query(new Criteria("ownerId").is(ownerId));
		}
		T result = mongoTemplate.findOne(query, entityClass);
		return result;
	}

	private String generateObjectId() {
		return UUID.randomUUID().toString();
	}

	public List<Student> searchStudentByInstitute(String instituteId, String schoolYear, Integer page, Integer limit, String orderBy) {
		// TODO Auto-generated method stub
		return new ArrayList<Student>();
	}

	public List<Experience> searchExperience(String studentId, String expType, Boolean institutional, 
			String instituteId,	String schoolYear, String certifierId, Long dateFrom, Long dateTo, 
			String text, Pageable pageable) {
		List<Experience> result = experienceRepository.searchExperience(
				studentId, expType, institutional, instituteId, schoolYear, certifierId, dateFrom, dateTo, 
				text, pageable);
		return result;
	}

	public List<Student> searchStudentByCertifier(String certifierId, Integer page, Integer limit,
			String orderBy) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Experience addMyExperience(String studentId, Experience experience) {
		// TODO Auto-generated method stub
		return null;
	}

	public Experience updateMyExperience(String studentId, Experience experience) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Experience addIsExperience(List<String> studentIds, Experience experience) {
		// TODO Auto-generated method stub
		return null;
	}

	public Experience updateIsExperience(List<String> studentIds, Experience experience) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Experience removeExperience(String experienceId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Student getStudent(String studentId) throws EntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public Certificate getCertificate(String certificateId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Certificate getCertificateByExperience(String experienceId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Certificate updateCertificateAttributes(String certificateId,
			Map<String, Object> attributes) {
		// TODO Auto-generated method stub
		return null;
	}

	public Certificate removeCertificate(String certificateId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Certificate addCertificateToExperience(String certifierId, Certificate certificate) {
		// TODO Auto-generated method stub
		return null;
	}

	public Experience certifyExperience(String studentId, String experienceId, String certifierId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<CertificationRequest> getCertificationRequest(String certifierId, Integer page,
			Integer limit, String orderBy) {
		// TODO Auto-generated method stub
		return null;
	}

	public CertificationRequest addCertificationRequest(CertificationRequest certificationRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	public CertificationRequest removeCertificationRequest(String certificationId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Institute> getInstitute() {
		List<Institute> result = instituteRepository.findAll(new Sort(Sort.Direction.ASC, "name"));
		return result;
	}

	public List<Student> searchStudentByExperience(String experienceId, String instituteId,
			String schoolYear) {
		// TODO Auto-generated method stub
		return null;
	}

}
