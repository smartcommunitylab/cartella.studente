package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.exception.EntityNotFoundException;
import it.smartcommunitylab.csengine.exception.StorageException;
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
import org.springframework.data.domain.Page;
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
	
	@Autowired
	private CertificationRequestRepository certificationRequestRepository;
	
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
		String experienceId = Utils.getUUID(); 
		experience.setId(experienceId);
		Experience experienceDb = experienceRepository.save(experience);
		for(String studentId : studentIds) {
			StudentExperience studentExperience = new StudentExperience();
			studentExperience.setId(Utils.getUUID());
			studentExperience.setExperienceId(experienceId);
			studentExperience.setExperience(experienceDb);
			studentExperience.setStudentId(studentId);
			studentExperienceRepository.save(studentExperience);
		}
		return experienceDb;
	}

	public Experience updateIsExperience(List<String> studentIds, Experience experience) 
			throws EntityNotFoundException, StorageException {
		// TODO Auto-generated method stub
		String experienceId = experience.getId();
		Experience experienceDb = experienceRepository.findOne(experienceId);
		if(experienceDb != null) {
			if(Utils.isCertified(experienceDb)) {
				throw new StorageException("modify is not allowed");
			}
			experienceDb.setAttributes(experience.getAttributes());
			experienceRepository.save(experienceDb);
			if((studentIds != null) && (!studentIds.isEmpty())) {
				//remove the existing relations
				List<StudentExperience> list = studentExperienceRepository.findByStudentsAndExperience(studentIds, experienceId);
				studentExperienceRepository.delete(list);
				//create the new ones
				for(String studentId : studentIds) {
					StudentExperience studentExperience = new StudentExperience();
					studentExperience.setId(Utils.getUUID());
					studentExperience.setExperienceId(experienceId);
					studentExperience.setExperience(experienceDb);
					studentExperience.setStudentId(studentId);
					studentExperienceRepository.save(studentExperience);
				}
			}
		} else {
			throw new EntityNotFoundException("entity not found");
		}
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

	public Certificate getCertificate(String experienceId, String studentId) 
			throws EntityNotFoundException {
		List<StudentExperience> list = studentExperienceRepository.findByStudentAndExperience(studentId, experienceId);
		if(list.isEmpty()) {
			throw new EntityNotFoundException("entity not found");
		}
		return list.get(0).getCertificate();
	}

	public Certificate updateCertificateAttributes(String experienceId, String studentId, 
			Map<String, Object> attributes) throws EntityNotFoundException, StorageException {
		List<StudentExperience> list = studentExperienceRepository.findByStudentAndExperience(studentId, experienceId);
		if(list.isEmpty()) {
			throw new EntityNotFoundException("entity not found");
		}
		StudentExperience studentExperience = list.get(0);
		if(Utils.isCertified(studentExperience.getExperience())) {
			throw new StorageException("modify is not allowed");
		}
		if(studentExperience.getCertificate() == null) {
			throw new StorageException("certificate fot found");
		}
		studentExperience.getCertificate().setAttributes(attributes);
		studentExperienceRepository.save(studentExperience);
 		return studentExperience.getCertificate();
	}

	public Certificate removeCertificate(String experienceId, String studentId) 
			throws EntityNotFoundException, StorageException {
		List<StudentExperience> list = studentExperienceRepository.findByStudentAndExperience(studentId, experienceId);
		if(list.isEmpty()) {
			throw new EntityNotFoundException("entity not found");
		}
		StudentExperience studentExperience = list.get(0);
		if(Utils.isCertified(studentExperience.getExperience())) {
			throw new StorageException("modify is not allowed");
		}
		Certificate oldCertificate = studentExperience.getCertificate();
		studentExperience.setCertificate(null);
		studentExperienceRepository.save(studentExperience);
		return oldCertificate;
	}

	public Certificate addCertificate(Certificate certificate) 
			throws EntityNotFoundException, StorageException {
		List<StudentExperience> list = studentExperienceRepository.findByStudentAndExperience(
				certificate.getStudentId(), certificate.getExperienceId());
		if(list.isEmpty()) {
			throw new EntityNotFoundException("entity not found");
		}
		StudentExperience studentExperience = list.get(0);
		if(Utils.isCertified(studentExperience.getExperience())) {
			throw new StorageException("modify is not allowed");
		}
		studentExperience.setCertificate(certificate);
		studentExperienceRepository.save(studentExperience);
		return certificate;
	}

	public Experience certifyExperience(String studentId, String experienceId, String certifierId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<CertificationRequest> getCertificationRequest(String certifierId, Pageable pageable) {
		Page<CertificationRequest> page = certificationRequestRepository.findByCertifierId(certifierId, pageable);
		return page.getContent();
	}

	public CertificationRequest addCertificationRequest(CertificationRequest certificationRequest) 
			throws StorageException {
		certificationRequest.setId(Utils.getUUID());
		Experience experience = experienceRepository.findOne(certificationRequest.getExperienceId());
		if(experience == null) {
			throw new StorageException("experience not found");
		}
		certificationRequest.setExperience(experience);
		CertificationRequest certificationRequestDB = certificationRequestRepository.save(certificationRequest);
		return certificationRequestDB;
	}

	public CertificationRequest removeCertificationRequest(String certificationId) 
			throws EntityNotFoundException {
		CertificationRequest certificationRequestDB = certificationRequestRepository.findOne(certificationId);
		if(certificationRequestDB == null) {
			throw new EntityNotFoundException("entity not found");
		}
		certificationRequestRepository.delete(certificationId);
		return certificationRequestDB;
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

	public void certifyIsExperience(String experienceId, List<Certificate> certificates) 
			throws StorageException, EntityNotFoundException {
		Experience experienceDb = experienceRepository.findOne(experienceId);
		if(experienceDb != null) {
			if(Utils.isCertified(experienceDb)) {
				throw new StorageException("modify is not allowed");
			}
			experienceDb.getAttributes().put(Const.ATTR_CERTIFIED, Boolean.TRUE);
			for(Certificate certificate : certificates) {
				String studentId = certificate.getStudentId();
				List<StudentExperience> list = studentExperienceRepository.findByStudentAndExperience(studentId, experienceId);
				if(list.isEmpty()) {
					continue;
				}
				StudentExperience studentExperience = list.get(0);
				certificate.setExperienceId(experienceId);
				studentExperience.setCertificate(certificate);
				studentExperience.setExperience(experienceDb);
				studentExperienceRepository.save(studentExperience);
			}
			experienceRepository.save(experienceDb);
		} else {
			throw new EntityNotFoundException("entity not found");
		}
	}

}
