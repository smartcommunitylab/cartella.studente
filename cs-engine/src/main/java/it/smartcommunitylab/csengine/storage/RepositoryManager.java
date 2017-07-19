package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.exception.EntityNotFoundException;
import it.smartcommunitylab.csengine.exception.StorageException;
import it.smartcommunitylab.csengine.model.CV;
import it.smartcommunitylab.csengine.model.CertificationRequest;
import it.smartcommunitylab.csengine.model.Consent;
import it.smartcommunitylab.csengine.model.Course;
import it.smartcommunitylab.csengine.model.Document;
import it.smartcommunitylab.csengine.model.Experience;
import it.smartcommunitylab.csengine.model.Institute;
import it.smartcommunitylab.csengine.model.PersonInCharge;
import it.smartcommunitylab.csengine.model.Registration;
import it.smartcommunitylab.csengine.model.Student;
import it.smartcommunitylab.csengine.model.StudentAuth;
import it.smartcommunitylab.csengine.model.StudentExperience;
import it.smartcommunitylab.csengine.model.TeachingUnit;

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

public class RepositoryManager {
	@SuppressWarnings("unused")
	private static final transient Logger logger = LoggerFactory.getLogger(RepositoryManager.class);
	
	@Autowired
	private InstituteRepository instituteRepository;
	
	@Autowired
	private TeachingUnitRepository teachingUnitRepository;  
	
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
	private PersonInChargeRepository personInChargeRepository;
	
	@Autowired
	private CVRepository cvRepository;
	
	@Autowired
	private StudentAuthRepository studentAuthRepository;
	
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

	public List<Student> searchStudentByInstitute(String teachingUnitId, String schoolYear, Pageable pageable) {
		List<Student> result = studentRepository.findByInstitute(teachingUnitId, schoolYear, pageable);
		return result;
	}

	public List<StudentExperience> searchStudentExperience(String studentId, String expType, Boolean institutional, 
			String instituteId, String teachingUnitId, String schoolYear, String registrationId, 
			String certifierId, Long dateFrom, Long dateTo, String text, Pageable pageable) {
		List<StudentExperience> result = studentExperienceRepository.searchExperience(studentId, expType, institutional, 
				instituteId, teachingUnitId,	schoolYear, registrationId, certifierId, dateFrom, dateTo, text, pageable);
		return result;
	}
	
	public List<StudentExperience> searchStudentExperienceById(String studentId, String instituteId,
			String teachingUnitId, String experienceId, Boolean institutional) {
		List<StudentExperience> result = studentExperienceRepository.searchExperienceById(studentId, instituteId,
				teachingUnitId, experienceId, institutional);
		return result;
	}
	
	public List<Experience> searchExperience(String expType, Boolean institutional, String instituteId, 
			String teachingUnitId,	String schoolYear, String certifierId, Long dateFrom, Long dateTo, 
			String text, Pageable pageable) {
		List<Experience> result = experienceRepository.searchExperience(expType, institutional, 
				instituteId, teachingUnitId, schoolYear, certifierId, dateFrom, dateTo, text, pageable);
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
			StudentExperience studentExperienceDb = studentExperienceRepository.
					findByStudentAndExperience(studentId, experienceId);
			if(Utils.isCertified(studentExperienceDb)) {
				throw new StorageException("modify is not allowed");
			}
			experienceDb.setAttributes(experience.getAttributes());
			experienceRepository.save(experienceDb);
			updateExperienceAttributes(experienceId, studentId, experience.getAttributes());
		} else {
			throw new EntityNotFoundException("entity not found");
		}
		return experienceDb;
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
			experienceDb.setAttributes(experience.getAttributes());
			experienceRepository.save(experienceDb);
			if((studentIds != null) && (!studentIds.isEmpty())) {
				//get the existing relations
				List<StudentExperience> list = studentExperienceRepository.findByStudentsAndExperience(studentIds, experienceId);
				for(StudentExperience studentExperience : list) {
					String studentId = studentExperience.getStudentId();
					if(studentIds.contains(studentId)) {
						if(Utils.isCertified(studentExperience)) {
							continue;
						}
						//update attributes
						updateExperienceAttributes(experienceId, studentId, experience.getAttributes());
					} else {
						//remove old relation
						studentExperienceRepository.delete(studentExperience);
					}
				}
				for(String studentId : studentIds) {
					StudentExperience studentExperience = studentExperienceRepository.
							findByStudentAndExperience(studentId, experienceId);
					if(studentExperience == null) {
						//add new relation
						StudentExperience studentExperienceNew = new StudentExperience();
						studentExperienceNew.setId(Utils.getUUID());
						studentExperienceNew.setExperienceId(experienceId);
						studentExperienceNew.setExperience(experienceDb);
						studentExperienceNew.setStudentId(studentId);
						Student studentDb = studentRepository.findOne(studentId);
						if(studentDb != null) {
							studentExperienceNew.setStudent(studentDb);
						}
						studentExperienceRepository.save(studentExperienceNew);
					}
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

	public List<Document> getDocuments(String experienceId, String studentId) 
			throws EntityNotFoundException {
		StudentExperience result = studentExperienceRepository.findByStudentAndExperience(studentId, experienceId);
		if(result == null) {
			throw new EntityNotFoundException("entity not found");
		}
		return result.getDocuments();
	}
	
	public Document getDocument(String experienceId, String studentId, String storageId) 
			throws EntityNotFoundException {
		StudentExperience studentExperience = studentExperienceRepository.
				findByStudentAndExperience(studentId, experienceId);
		if(studentExperience == null) {
			throw new EntityNotFoundException("experience not found");
		}
		Document document = Utils.findDocument(studentExperience, storageId);
		if(document == null) {
			throw new EntityNotFoundException("document not found");
		}
		return document;
	}

	public Document updateDocumentAttributes(String experienceId, String studentId, String storageId, 
			Map<String, Object> attributes) throws EntityNotFoundException, StorageException {
		StudentExperience studentExperience = studentExperienceRepository.findByStudentAndExperience(studentId, experienceId);
		if(studentExperience == null) {
			throw new EntityNotFoundException("entity not found");
		}
		Document document = Utils.findDocument(studentExperience, storageId);
		if(document == null) {
			throw new StorageException("document fot found");
		}
		document.setAttributes(attributes);
		studentExperienceRepository.save(studentExperience);
 		return document;
	}

	public Document addDocument(Document document) 
			throws EntityNotFoundException, StorageException {
		StudentExperience studentExperience = studentExperienceRepository.findByStudentAndExperience(
				document.getStudentId(), document.getExperienceId());
		if(studentExperience == null) {
			throw new EntityNotFoundException("entity not found");
		}
		document.setStorageId(Utils.getUUID());
		document.setDocumentPresent(Boolean.FALSE);
		studentExperience.getDocuments().add(document);
		studentExperienceRepository.save(studentExperience);
		return document;
	}
	
	public Document removeDocument(String experienceId, String studentId, String storageId) 
			throws EntityNotFoundException, StorageException {
		StudentExperience studentExperience = studentExperienceRepository.
				findByStudentAndExperience(studentId, experienceId);
		if(studentExperience == null) {
			throw new EntityNotFoundException("entity not found");
		}
		Document document = Utils.findDocument(studentExperience, storageId);
		if(document == null) {
			throw new StorageException("document does not exist");
		}
		studentExperience.getDocuments().remove(document);
		studentExperienceRepository.save(studentExperience);
		return document;
	}

	public StudentExperience certifyMyExperience(String experienceId, String studentId, String certifierId) 
			throws StorageException, EntityNotFoundException {
		StudentExperience studentExperienceDb = studentExperienceRepository.findByStudentAndExperience(studentId, experienceId);
		if(studentExperienceDb != null) {
			if(Utils.isCertified(studentExperienceDb)) {
				throw new StorageException("modify is not allowed");
			}
			String refCertifierId = (String) studentExperienceDb.getExperience()
					.getAttributes().get(Const.ATTR_CERTIFIERID);
			if(Utils.isEmpty(refCertifierId) || refCertifierId.equals(certifierId)) {
				throw new StorageException("ceritfier not allowed");
			}
			studentExperienceDb.getExperience().getAttributes().put(Const.ATTR_CERTIFIED, Boolean.TRUE);
			studentExperienceRepository.save(studentExperienceDb);
		} else {
			throw new EntityNotFoundException("entity not found");
		}
		return studentExperienceDb;
	}

	public List<CertificationRequest> getCertificationRequestByCertifier(String certifierId, Pageable pageable) {
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

	public List<CertificationRequest> getCertificationRequestByStudent(String studentId, Pageable pageable) {
		Page<CertificationRequest> page = certificationRequestRepository.findByStudentId(studentId, pageable);
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

	public List<Student> searchStudentByExperience(String experienceId, String teachingUnitId,
			String schoolYear, Pageable pageable) {
		List<Student> result = studentRepository.findByExperience(experienceId, teachingUnitId, schoolYear, pageable);
		return result;
	}

	public void certifyIsExperience(String experienceId, List<String> students) 
			throws StorageException, EntityNotFoundException {
		for(String studentId : students) {
			StudentExperience studentExperience = studentExperienceRepository.findByStudentAndExperience(
					studentId, experienceId);
			if(studentExperience == null) {
				continue;
			}
			if(Utils.isCertified(studentExperience)) {
				continue;
			}
			studentExperience.getExperience().getAttributes().put(Const.ATTR_CERTIFIED, Boolean.TRUE);
			studentExperienceRepository.save(studentExperience);
		}
	}

	private void updateExperienceAttributes(String experienceId, String studentId, 
			Map<String, Object> attributes) {
		StudentExperience studentExperience = studentExperienceRepository.
				findByStudentAndExperience(studentId, experienceId);
		if(studentExperience!= null) {
			studentExperience.getExperience().getAttributes().putAll(attributes);
			studentExperienceRepository.save(studentExperience);
		}
	}
	
	public List<Registration> searchRegistration(String studentId, String teachingUnitId,
			String schoolYear, Long dateFrom, Long dateTo, Pageable pageable) {
		List<Registration> result = registrationRepository.searchRegistration(studentId, teachingUnitId, schoolYear, 
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
		String teachingUnitId = registration.getTeachingUnitId();
		Student student = studentRepository.findOne(studentId);
		Institute institute = instituteRepository.findOne(teachingUnitId);
		if((student == null) || (institute == null)) {
			throw new StorageException("student or institute not found");
		}
		registration.setStudent(student);
		registration.setInstitute(institute);
		Registration registrationDb = registrationRepository.save(registration);
		return registrationDb;
	}

	public List<Course> getCourseByInstitute(String teachingUnitId, String schoolYear) {
		List<Course> result = courseRepository.findByInstitute(teachingUnitId, schoolYear);
		return result;
	}

	public List<Course> getCourseByTeachingUnit(String teachingUnitId, String schoolYear) {
		List<Course> result = courseRepository.findByTeachingUnit(teachingUnitId, schoolYear);
		return result;
	}
	
	public Consent addConsent(Consent consent) throws StorageException {
		Consent consentDb = null;
		if(Utils.isNotEmpty(consent.getStudentId())) {
			consentDb = consentRepository.findByStudent(consent.getStudentId());
		}
		if((consentDb == null) && Utils.isNotEmpty(consent.getSubject())) {
			consentDb = consentRepository.findBySubject(consent.getSubject());
		}
		if(consentDb == null) {
			Date now = new Date();
			consent.setCreationDate(now);
			consent.setLastUpdate(now);
			consent.setId(Utils.getUUID());
			consent.setAuthorized(Boolean.TRUE);
			consentDb = consentRepository.save(consent);
		} else {
			Date now = new Date();
			consentDb.setAuthorized(Boolean.TRUE);
			consentDb.setLastUpdate(now);
			consentRepository.save(consentDb);
		}
		return consentDb;
	}

	public Consent removeAuthorization(String studentId) throws EntityNotFoundException {
		Consent consentDb = consentRepository.findByStudent(studentId);
		if(consentDb == null) {
			throw new EntityNotFoundException("entity not found");
		}
		Date now = new Date();
		consentDb.setAuthorized(Boolean.FALSE);
		consentDb.setLastUpdate(now);
		consentRepository.save(consentDb);
		return consentDb;
	}

	public Consent addAuthorization(String studentId) throws EntityNotFoundException {
		Consent consentDb = consentRepository.findByStudent(studentId);
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
		studentDb.setPhone(student.getPhone());
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
		cvDb.setStudentExperienceIdMap(cv.getStudentExperienceIdMap());
		cvDb.setRegistrationIdList(cv.getRegistrationIdList());
		cvDb.setStorageIdList(cv.getStorageIdList());
		cvDb.setLastUpdate(now);
		cvRepository.save(cvDb);
		return cvDb;
	}

	public Document addFileToDocument(String experienceId, String studentId, String storageId,
			String contentType, String filename) 
					throws EntityNotFoundException, StorageException {
		StudentExperience studentExperience = studentExperienceRepository.
				findByStudentAndExperience(studentId, experienceId);
		if(studentExperience == null) {
			throw new EntityNotFoundException("entity not found");
		}
		Document document = Utils.findDocument(studentExperience, storageId);
		if(document == null) {
			throw new StorageException("document does not exist");
		}
		document.setContentType(contentType);
		document.setFilename(filename);
		document.setDocumentPresent(Boolean.TRUE);
		studentExperienceRepository.save(studentExperience);
		return document;
	}
	
	public Document removeFileToDocument(String experienceId, String studentId, String storageId) 
			throws EntityNotFoundException, StorageException {
		StudentExperience studentExperience = studentExperienceRepository.
				findByStudentAndExperience(studentId, experienceId);
		if(studentExperience == null) {
			throw new EntityNotFoundException("entity not found");
		}
		Document document = Utils.findDocument(studentExperience, storageId);
		if(document == null) {
			throw new StorageException("document does not exist");
		}
		document.setContentType(null);
		document.setFilename(null);
		document.setDocumentPresent(Boolean.FALSE);
		studentExperienceRepository.save(studentExperience);
		return document;
	}


	public List<TeachingUnit> getTeachingUnit() {
		return teachingUnitRepository.findAll();
	}

	public List<Registration> getRegistrationByTeachingUnit(String teachingUnitId, String schoolYear) {
		List<Registration> result = registrationRepository.searchRegistration(null, teachingUnitId, schoolYear, 
				null, null, null);
		return result;
	}

	public TeachingUnit getTeachingUnitById(String teachingUnitId) throws EntityNotFoundException {
		TeachingUnit result = teachingUnitRepository.findOne(teachingUnitId);
		if(result == null) {
			throw new EntityNotFoundException("entity not found");
		}
		return result;
	}
	
	public Registration getRegistrationById(String registrationId) throws EntityNotFoundException {
		Registration result = registrationRepository.findOne(registrationId);
		if(result == null) {
			throw new EntityNotFoundException("entity not found");
		}
		return result;
	}

	public Experience getExperienceById(String experienceId) throws EntityNotFoundException {
		Experience result = experienceRepository.findOne(experienceId);
		if(result == null) {
			throw new EntityNotFoundException("entity not found");
		}
		return result;
	}

	public Student getStudentByCF(String cf) {
		Student result = studentRepository.findByCF(cf);
		return result;
	}

	public PersonInCharge getPersonInChargeByCF(String cf) {
		PersonInCharge result = personInChargeRepository.findByCF(cf);
		return result;
	}

	public Student updateStudentContentType(String studentId, String contentType) 
			throws EntityNotFoundException {
		Student studentDb = studentRepository.findOne(studentId);
		if(studentDb == null) {
			throw new EntityNotFoundException("entity not found");
		}
		studentDb.setContentType(contentType);
		Date now = new Date();
		studentDb.setLastUpdate(now);
		studentRepository.save(studentDb);
		return studentDb;
	}
	
	public StudentAuth getStudentAuthById(String authId) throws EntityNotFoundException {
		StudentAuth studentAuthDB = studentAuthRepository.findOne(authId);
		if(studentAuthDB == null) {
			throw new EntityNotFoundException("entity not found");
		}
		return studentAuthDB;
	}
	
	public List<StudentAuth> getStudentAuthByStudent(String studentId) {
		return studentAuthRepository.findByStudent(studentId);
	}
	
	public StudentAuth addStudentAuth(StudentAuth studentAuth) {
		Date now = new Date();
		studentAuth.setCreationDate(now);
		studentAuth.setLastUpdate(now);
		studentAuth.setId(Utils.getUUID());
		StudentAuth studentAuthDb = studentAuthRepository.save(studentAuth);
		return studentAuthDb;
	}
	
	public StudentAuth removeStudentAuth(String authId) throws EntityNotFoundException {
		StudentAuth studentAuthDB = studentAuthRepository.findOne(authId);
		if(studentAuthDB == null) {
			throw new EntityNotFoundException("entity not found");
		}
		studentAuthRepository.delete(studentAuthDB);
		return studentAuthDB;
	}

}
