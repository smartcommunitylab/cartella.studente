package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.exception.EntityNotFoundException;
import it.smartcommunitylab.csengine.exception.StorageException;
import it.smartcommunitylab.csengine.model.Certificate;
import it.smartcommunitylab.csengine.model.CertificationRequest;
import it.smartcommunitylab.csengine.model.Experience;
import it.smartcommunitylab.csengine.model.Institute;
import it.smartcommunitylab.csengine.model.Registration;
import it.smartcommunitylab.csengine.model.Student;
import it.smartcommunitylab.csengine.model.StudentExperience;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.springframework.data.mongodb.core.query.Update;

public class RepositoryManager {
	@SuppressWarnings("unused")
	private static final transient Logger logger = LoggerFactory.getLogger(RepositoryManager.class);
	
	@Autowired
	private InstituteRepository instituteRepository;
	
	@Autowired
	private ExperienceRepository experienceRepository;
	
	@Autowired
	private StudentExperienceRepository studentExperienceRepository;
	
	@Autowired
	private CertificationRequestRepository certificationRequestRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private RegistrationRepository registrationRepository;
	
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

	public List<Student> searchStudentByInstitute(String instituteId, String schoolYear, Pageable pageable) {
		List<Student> result = studentRepository.findByInstitute(instituteId, schoolYear, pageable);
		return result;
	}

	public List<StudentExperience> searchExperience(String studentId, String expType, Boolean institutional, 
			String instituteId,	String schoolYear, String certifierId, Long dateFrom, Long dateTo, 
			String text, Pageable pageable) {
		List<StudentExperience> result = new ArrayList<StudentExperience>();
		if(Utils.isNotEmpty(instituteId)) {
			result = studentExperienceRepository.searchExperienceByInstitute(expType, 
					instituteId, schoolYear, dateFrom, dateTo, text, pageable);
		} else if(Utils.isNotEmpty(studentId)) {
			result = studentExperienceRepository.searchExperienceByStudent(studentId, expType, institutional, 
					instituteId,	schoolYear, certifierId, dateFrom, dateTo, text, pageable);
		} else if(Utils.isNotEmpty(certifierId)) {
			result = studentExperienceRepository.searchExperienceByCertifier(expType, 
					certifierId, dateFrom, dateTo, text, pageable);
		}
		return result;
	}

	public List<Student> searchStudentByCertifier(String certifierId, Pageable pageable) {
		List<Student> result = studentRepository.findByCertifier(certifierId, pageable);
		return result;
	}
	
	public Experience addMyExperience(String studentId, Experience experience) {
		String experienceId = Utils.getUUID(); 
		experience.setId(experienceId);
		Experience experienceDb = experienceRepository.save(experience);
		StudentExperience studentExperience = new StudentExperience();
		studentExperience.setId(Utils.getUUID());
		studentExperience.setExperienceId(experienceId);
		studentExperience.setExperience(experienceDb);
		studentExperience.setStudentId(studentId);
		Student studentDb = studentRepository.findOne(studentId);
		if(studentDb != null) {
			studentExperience.setStudent(studentDb);
		}
		studentExperienceRepository.save(studentExperience);
		return experienceDb;
	}

	public Experience updateMyExperience(String studentId, Experience experience) 
			throws StorageException, EntityNotFoundException {
		String experienceId = experience.getId();
		Experience experienceDb = experienceRepository.findOne(experienceId);
		if(experienceDb != null) {
			if(Utils.isCertified(experienceDb)) {
				throw new StorageException("modify is not allowed");
			}
			experienceDb.setAttributes(experience.getAttributes());
			experienceRepository.save(experienceDb);
			updateExperienceAttributes(experienceId, experience.getAttributes());
		} else {
			throw new EntityNotFoundException("entity not found");
		}
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
			Student studentDb = studentRepository.findOne(studentId);
			if(studentDb != null) {
				studentExperience.setStudent(studentDb);
			}
			studentExperienceRepository.save(studentExperience);
		}
		return experienceDb;
	}

	public Experience updateIsExperience(List<String> studentIds, Experience experience) 
			throws EntityNotFoundException, StorageException {
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
					Student studentDb = studentRepository.findOne(studentId);
					if(studentDb != null) {
						studentExperience.setStudent(studentDb);
					}
					studentExperienceRepository.save(studentExperience);
				}
			}
		} else {
			throw new EntityNotFoundException("entity not found");
		}
		return null;
	}
	
	public Experience removeExperience(String experienceId) 
			throws EntityNotFoundException, StorageException {
		Experience experienceDb = experienceRepository.findOne(experienceId);
		if(experienceDb == null) {
			throw new EntityNotFoundException("entity not found");
		}
		if(Utils.isCertified(experienceDb)) {
			throw new StorageException("modify is not allowed");
		}
		experienceRepository.delete(experienceDb);
		List<StudentExperience> list = studentExperienceRepository.findByExperienceId(experienceId);
		studentExperienceRepository.delete(list);
		return experienceDb;
	}

	public Student getStudent(String studentId) throws EntityNotFoundException {
		Student result = studentRepository.findOne(studentId);
		if(result == null) {
			throw new EntityNotFoundException("entity not found");
		}
		return result;
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

	public Experience certifyMyExperience(Certificate certificate, String certifierId) 
			throws StorageException, EntityNotFoundException {
		String experienceId = certificate.getExperienceId();
		String studentId = certificate.getStudentId();
		Experience experienceDb = experienceRepository.findOne(experienceId);
		if(experienceDb != null) {
			if(Utils.isCertified(experienceDb)) {
				throw new StorageException("modify is not allowed");
			}
			String refCertifierId = (String) experienceDb.getAttributes().get(Const.ATTR_CERTIFIERID);
			if(Utils.isEmpty(refCertifierId) || refCertifierId.equals(certifierId)) {
				throw new StorageException("ceritfier not allowed");
			}
			experienceDb.getAttributes().put(Const.ATTR_CERTIFIED, Boolean.TRUE);
			experienceRepository.save(experienceDb);
			updateExperienceAttributes(experienceId, experienceDb.getAttributes());
			List<StudentExperience> list = studentExperienceRepository.findByStudentAndExperience(studentId, experienceId);
			if(!list.isEmpty()) {
				StudentExperience studentExperience = list.get(0);
				studentExperience.setCertificate(certificate);
				studentExperience.setExperience(experienceDb);
				studentExperienceRepository.save(studentExperience);
			}
		} else {
			throw new EntityNotFoundException("entity not found");
		}
		return experienceDb;
	}

	public List<CertificationRequest> getCertificationRequest(String certifierId, Pageable pageable) {
		Page<CertificationRequest> page = certificationRequestRepository.findByCertifierId(certifierId, pageable);
		List<CertificationRequest> requestList = page.getContent();
		for(CertificationRequest request : requestList) {
			List<StudentExperience> attendanceList = studentExperienceRepository.findByStudentAndExperience(request.getStudentId(), request.getExperienceId());
			if(attendanceList.isEmpty()) {
				continue;
			}
			StudentExperience attendance = attendanceList.get(0);
			request.setExperience(attendance.getExperience());
			request.setStudent(attendance.getStudent());
		}
		return requestList;
	}

	public CertificationRequest addCertificationRequest(CertificationRequest certificationRequest) 
			throws StorageException {
		certificationRequest.setId(Utils.getUUID());
		Experience experience = experienceRepository.findOne(certificationRequest.getExperienceId());
		Student student = studentRepository.findOne(certificationRequest.getStudentId());
		if((experience == null) || (student == null)) {
			throw new StorageException("experience or student not found");
		}
		certificationRequest.setExperience(experience);
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
			String schoolYear, Pageable pageable) {
		List<Student> result = studentRepository.findByExperience(experienceId, instituteId, schoolYear, pageable);
		return result;
	}

	public void certifyIsExperience(String experienceId, List<Certificate> certificates) 
			throws StorageException, EntityNotFoundException {
		Experience experienceDb = experienceRepository.findOne(experienceId);
		if(experienceDb != null) {
			if(Utils.isCertified(experienceDb)) {
				throw new StorageException("modify is not allowed");
			}
			experienceDb.getAttributes().put(Const.ATTR_CERTIFIED, Boolean.TRUE);
			experienceRepository.save(experienceDb);
			updateExperienceAttributes(experienceId, experienceDb.getAttributes());
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
		} else {
			throw new EntityNotFoundException("entity not found");
		}
	}

	private void updateExperienceAttributes(String experienceId, Map<String, Object> attributes) {
		Query query = new Query(Criteria.where("experienceId").is(experienceId));
		Update update = new Update().set("experience.attributes", attributes);
		mongoTemplate.upsert(query, update, StudentExperience.class);
	}
	
	public List<Registration> searchRegistration(String studentId, String instituteId,
			String schoolYear, Long dateFrom, Long dateTo, Pageable pageable) {
		List<Registration> result = registrationRepository.searchRegistration(studentId, instituteId, schoolYear, 
				dateFrom, dateTo, pageable);
		return result;
	}

	public Institute addInstitute(Institute institute) {
		institute.setId(Utils.getUUID());
		Institute instituteDb = instituteRepository.save(institute);
		return instituteDb;
	}

	public Student addStudent(Student student) {
		student.setId(Utils.getUUID());
		Student studentDb = studentRepository.save(student);
		return studentDb;
	}

	public Registration addRegistration(Registration registration) throws StorageException {
		registration.setId(Utils.getUUID());
		String studentId = registration.getStudentId();
		String instituteId = registration.getInstituteId();
		Student student = studentRepository.findOne(studentId);
		Institute institute = instituteRepository.findOne(instituteId);
		if((student == null) || (institute == null)) {
			throw new StorageException("student or institute not found");
		}
		registration.setStudent(student);
		registration.setInstitute(institute);
		Registration registrationDb = registrationRepository.save(registration);
		return registrationDb;
	}

}
