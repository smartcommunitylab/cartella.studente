package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.exception.EntityNotFoundException;
import it.smartcommunitylab.csengine.exception.StorageException;
import it.smartcommunitylab.csengine.model.CV;
import it.smartcommunitylab.csengine.model.Certificate;
import it.smartcommunitylab.csengine.model.CertificationRequest;
import it.smartcommunitylab.csengine.model.Consent;
import it.smartcommunitylab.csengine.model.Course;
import it.smartcommunitylab.csengine.model.Experience;
import it.smartcommunitylab.csengine.model.Institute;
import it.smartcommunitylab.csengine.model.Registration;
import it.smartcommunitylab.csengine.model.Student;
import it.smartcommunitylab.csengine.model.StudentExperience;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
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
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private ConsentRepository consentRepository;
	
	@Autowired
	private CVRepository cvRepository;
	
	private MongoTemplate mongoTemplate;
	private String defaultLang;
	
	public RepositoryManager(MongoTemplate template, String defaultLang) {
		this.mongoTemplate = template;
		this.defaultLang = defaultLang;
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

	public List<StudentExperience> searchStudentExperience(String studentId, String expType, Boolean institutional, 
			String instituteId,	String schoolYear, String certifierId, Long dateFrom, Long dateTo, 
			String text, Pageable pageable) {
		List<StudentExperience> result = studentExperienceRepository.searchExperience(studentId, expType, institutional, 
				instituteId,	schoolYear, certifierId, dateFrom, dateTo, text, pageable);
		return result;
	}
	
	public List<StudentExperience> searchStudentExperienceById(String studentId, String instituteId,
			String experienceId) {
		List<StudentExperience> result = studentExperienceRepository.searchExperienceById(studentId, instituteId, 
				experienceId);
		return result;
	}
	
	public List<Experience> searchExperience(String expType, Boolean institutional, 
			String instituteId,	String schoolYear, String certifierId, Long dateFrom, Long dateTo, 
			String text, Pageable pageable) {
		List<Experience> result = experienceRepository.searchExperience(expType, institutional, 
				instituteId, schoolYear, certifierId, dateFrom, dateTo, text, pageable);
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
			updateExperienceAttributes(experienceId, experience.getAttributes(), null);
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
		StudentExperience result = studentExperienceRepository.findByStudentAndExperience(studentId, experienceId);
		if(result == null) {
			throw new EntityNotFoundException("entity not found");
		}
		return result.getCertificate();
	}

	public Certificate updateCertificateAttributes(String experienceId, String studentId, 
			Map<String, Object> attributes) throws EntityNotFoundException, StorageException {
		StudentExperience studentExperience = studentExperienceRepository.findByStudentAndExperience(studentId, experienceId);
		if(studentExperience == null) {
			throw new EntityNotFoundException("entity not found");
		}
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
		StudentExperience studentExperience = studentExperienceRepository.findByStudentAndExperience(studentId, experienceId);
		if(studentExperience == null) {
			throw new EntityNotFoundException("entity not found");
		}
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
		StudentExperience studentExperience = studentExperienceRepository.findByStudentAndExperience(
				certificate.getStudentId(), certificate.getExperienceId());
		if(studentExperience == null) {
			throw new EntityNotFoundException("entity not found");
		}
		if(Utils.isCertified(studentExperience.getExperience())) {
			throw new StorageException("modify is not allowed");
		}
		certificate.setStorageId(Utils.getUUID());
		certificate.setDocumentPresent(Boolean.FALSE);
		studentExperience.setCertificate(certificate);
		studentExperienceRepository.save(studentExperience);
		return certificate;
	}

	public Experience certifyMyExperience(Certificate certificate, String certifierId) 
			throws StorageException, EntityNotFoundException {
		String experienceId = certificate.getExperienceId();
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
			updateExperienceAttributes(experienceId, experienceDb.getAttributes(), certificate);
		} else {
			throw new EntityNotFoundException("entity not found");
		}
		return experienceDb;
	}

	public List<CertificationRequest> getCertificationRequest(String certifierId, Pageable pageable) {
		Page<CertificationRequest> page = certificationRequestRepository.findByCertifierId(certifierId, pageable);
		List<CertificationRequest> requestList = page.getContent();
		for(CertificationRequest request : requestList) {
			StudentExperience attendance = studentExperienceRepository.findByStudentAndExperience(
					request.getStudentId(), request.getExperienceId());
			if(attendance == null) {
				continue;
			}
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
			updateExperienceAttributes(experienceId, experienceDb.getAttributes(), null);
			for(Certificate certificate : certificates) {
				String studentId = certificate.getStudentId();
				StudentExperience studentExperience = studentExperienceRepository.findByStudentAndExperience(
						studentId, experienceId);
				if(studentExperience == null) {
					continue;
				}
				certificate.setExperienceId(experienceId);
				studentExperience.setCertificate(certificate);
				studentExperience.setExperience(experienceDb);
				studentExperienceRepository.save(studentExperience);
			}
		} else {
			throw new EntityNotFoundException("entity not found");
		}
	}

	private void updateExperienceAttributes(String experienceId, Map<String, Object> attributes, Certificate certificate) {
		Query query = new Query(Criteria.where("experienceId").is(experienceId));
		Update update = new Update().set("experience.attributes", attributes);
		if(certificate!= null) {
			update.set("certificate", certificate);
		}
		mongoTemplate.upsert(query, update, StudentExperience.class);
	}
	
	public List<Registration> searchRegistration(String studentId, String instituteId,
			String schoolYear, Long dateFrom, Long dateTo, Pageable pageable) {
		List<Registration> result = registrationRepository.searchRegistration(studentId, instituteId, schoolYear, 
				dateFrom, dateTo, pageable);
		return result;
	}

	public Institute addInstitute(Institute institute) {
		Date now = new Date();
		institute.setCreationDate(now);
		institute.setLastUpdate(now);
		institute.setId(Utils.getUUID());
		Institute instituteDb = instituteRepository.save(institute);
		return instituteDb;
	}

	public Student addStudent(Student student) {
		Date now = new Date();
		student.setCreationDate(now);
		student.setLastUpdate(now);
		student.setId(Utils.getUUID());
		Student studentDb = studentRepository.save(student);
		return studentDb;
	}

	public Registration addRegistration(Registration registration) throws StorageException {
		Date now = new Date();
		registration.setCreationDate(now);
		registration.setLastUpdate(now);
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

	public List<Course> getCourseByInstitute(String instituteId, String schoolYear) {
		List<Course> result = courseRepository.findByInstitute(instituteId, schoolYear);
		return result;
	}

	public Consent addConsent(Consent consent) throws StorageException {
		Consent consentDb = null;
		if(Utils.isNotEmpty(consent.getSubject())) {
			consentDb = consentRepository.findBySubject(consent.getSubject());
			if(consentDb != null) {
				throw new StorageException("consent already exists for this subject");
			}
		}
		if(Utils.isNotEmpty(consent.getStudentId())) {
			consentDb = consentRepository.findByStudent(consent.getStudentId());
			if(consentDb != null) {
				throw new StorageException("consent already exists for this studentId");
			}
		}
		Date now = new Date();
		consent.setCreationDate(now);
		consent.setLastUpdate(now);
		consent.setId(Utils.getUUID());
		consent.setAuthorized(Boolean.TRUE);
		consentDb = consentRepository.save(consent);
		return consentDb;
	}

	public Consent removeAuthorization(String subject) throws EntityNotFoundException {
		Consent consentDb = consentRepository.findBySubject(subject);
		if(consentDb == null) {
			throw new EntityNotFoundException("entity not found");
		}
		Date now = new Date();
		consentDb.setAuthorized(Boolean.FALSE);
		consentDb.setLastUpdate(now);
		consentRepository.save(consentDb);
		return consentDb;
	}

	public Consent addAuthorization(String subject) throws EntityNotFoundException {
		Consent consentDb = consentRepository.findBySubject(subject);
		if(consentDb == null) {
			throw new EntityNotFoundException("entity not found");
		}
		Date now = new Date();
		consentDb.setAuthorized(Boolean.TRUE);
		consentDb.setLastUpdate(now);
		consentRepository.save(consentDb);
		return consentDb;
	}

	public Consent getConsentBySubject(String subject) {
		Consent consentDb = consentRepository.findBySubject(subject);
		return consentDb;
	}

	public Consent getConsentByStudent(String studentId) {
		Consent consentDb = consentRepository.findByStudent(studentId);
		return consentDb;
	}

	public List<Registration> getRegistrationByStudent(String studentId) {
		List<Registration> result = registrationRepository.findByStudent(studentId);
		return result;
	}
	
	public List<Registration> getRegistrationByCourse(String courseId) {
		List<Registration> result = registrationRepository.findByCourse(courseId);
		return result;
	}

	public Student updateStudentContact(Student student) throws EntityNotFoundException {
		Student studentDb = studentRepository.findOne(student.getId());
		if(studentDb == null) {
			throw new EntityNotFoundException("entity not found");
		}
		studentDb.setAddress(student.getAddress());
		studentDb.setEmail(student.getEmail());
		studentDb.setMobilePhone(student.getMobilePhone());
		studentDb.setSocialMap(student.getSocialMap());
		Date now = new Date();
		studentDb.setLastUpdate(now);
		studentRepository.save(studentDb);
		return studentDb;
	}

	public CV getStudentCV(String studentId) throws EntityNotFoundException {
		CV cv = cvRepository.findByStudent(studentId);
		if(cv == null) {
			throw new EntityNotFoundException("entity not found");
		}
		Student student = studentRepository.findOne(studentId);
		if(student != null) {
			cv.setStudent(student);
		}
		for(String studentExperienceId : cv.getStudentExperienceIds()) {
			StudentExperience studentExperience = studentExperienceRepository.findOne(studentExperienceId);
			if(studentExperience!= null) {
				cv.getExperiences().add(studentExperience);
			}
		}
		return cv;
	}

	public CV addStudentCV(CV cv) {
		Date now = new Date();
		cv.setCreationDate(now);
		cv.setLastUpdate(now);
		cv.setId(Utils.getUUID());
		CV cvDb = cvRepository.save(cv);
		return cvDb;
	}

	public CV updateStudentCV(CV cv) throws EntityNotFoundException {
		Date now = new Date();
		CV cvDb = cvRepository.findOne(cv.getId());
		if(cvDb == null) {
			throw new EntityNotFoundException("entity not found");
		}
		cvDb.setStudentExperienceIds(cv.getStudentExperienceIds());
		cvDb.setDrivingLicence(cv.getDrivingLicence());
		cvDb.setManagementSkills(cv.getManagementSkills());
		cvDb.setLastUpdate(now);
		cvRepository.save(cvDb);
		return cvDb;
	}

	public Certificate updateCertificateUri(String experienceId, String studentId, String documentUri, String contentType) 
			throws EntityNotFoundException, StorageException {
		StudentExperience studentExperience = studentExperienceRepository.findByStudentAndExperience(studentId, experienceId);
		if(studentExperience == null) {
			throw new EntityNotFoundException("entity not found");
		}
		if(studentExperience.getCertificate() == null) {
			throw new StorageException("certificate does not exist");
		}
		studentExperience.getCertificate().setDocumentUri(documentUri);
		studentExperience.getCertificate().setContentType(contentType);
		studentExperience.getCertificate().setDocumentPresent(Boolean.TRUE);
		studentExperienceRepository.save(studentExperience);
		return studentExperience.getCertificate();
	}

	public Certificate removeCertificateUri(String experienceId, String studentId) 
			throws EntityNotFoundException, StorageException {
		StudentExperience studentExperience = studentExperienceRepository.findByStudentAndExperience(studentId, experienceId);
		if(studentExperience == null) {
			throw new EntityNotFoundException("entity not found");
		}
		if(studentExperience.getCertificate() == null) {
			throw new StorageException("certificate does not exist");
		}
		studentExperience.getCertificate().setDocumentUri(null);
		studentExperience.getCertificate().setContentType(null);
		studentExperience.getCertificate().setDocumentPresent(Boolean.FALSE);
		studentExperienceRepository.save(studentExperience);
		return studentExperience.getCertificate();
	}
	

}
