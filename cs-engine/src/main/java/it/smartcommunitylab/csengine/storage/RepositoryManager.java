package it.smartcommunitylab.csengine.storage;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.CloseableIterator;

import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.ErrorLabelManager;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.exception.EntityNotFoundException;
import it.smartcommunitylab.csengine.exception.StorageException;
import it.smartcommunitylab.csengine.model.ActiveInstitute;
import it.smartcommunitylab.csengine.model.CV;
import it.smartcommunitylab.csengine.model.CertificationRequest;
import it.smartcommunitylab.csengine.model.Certifier;
import it.smartcommunitylab.csengine.model.Consent;
import it.smartcommunitylab.csengine.model.Course;
import it.smartcommunitylab.csengine.model.CourseMetaInfo;
import it.smartcommunitylab.csengine.model.Document;
import it.smartcommunitylab.csengine.model.Experience;
import it.smartcommunitylab.csengine.model.Institute;
import it.smartcommunitylab.csengine.model.PersonInCharge;
import it.smartcommunitylab.csengine.model.Professor;
import it.smartcommunitylab.csengine.model.ProfessoriClassi;
import it.smartcommunitylab.csengine.model.Registration;
import it.smartcommunitylab.csengine.model.Student;
import it.smartcommunitylab.csengine.model.StudentAuth;
import it.smartcommunitylab.csengine.model.StudentExperience;
import it.smartcommunitylab.csengine.model.TeachingUnit;
import it.smartcommunitylab.csengine.model.Typology;
import it.smartcommunitylab.csengine.model.statistics.Address;
import it.smartcommunitylab.csengine.model.statistics.CourseData;
import it.smartcommunitylab.csengine.model.statistics.KPI;
import it.smartcommunitylab.csengine.model.statistics.Location;
import it.smartcommunitylab.csengine.model.statistics.Organization;
import it.smartcommunitylab.csengine.model.statistics.POI;
import it.smartcommunitylab.csengine.model.statistics.Product;
import it.smartcommunitylab.csengine.model.statistics.Provider;
import it.smartcommunitylab.csengine.model.statistics.SchoolRegistration;
import it.smartcommunitylab.csengine.model.statistics.Stage;
import it.smartcommunitylab.csengine.model.statistics.StudentProfile;
import it.smartcommunitylab.csengine.model.stats.KeyValue;
import it.smartcommunitylab.csengine.model.stats.RegistrationStats;

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
	private CourseMetaInfoRepository courseMetaInfoRepo;

	@Autowired
	private ConsentRepository consentRepository;

	@Autowired
	private PersonInChargeRepository personInChargeRepository;

	@Autowired
	private CVRepository cvRepository;

	@Autowired
	private StudentAuthRepository studentAuthRepository;

	@Autowired
	private CertifierRepository certifierRepository;
	
	@Autowired
	private ProfessoriRepository professoriRepository;
	
	@Autowired
	private ProfessoriClassiRepository professoriClassiRepository;

	@Autowired
	private ActiveInstituteRepository activeInstituteRepository;

	@Autowired
	private ErrorLabelManager errorLabelManager;

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

	public <T> T findOneData(Class<T> entityClass, Criteria criteria, String ownerId) throws ClassNotFoundException {
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
			String instituteId, String teachingUnitId, String schoolYear, String registrationId, String certifierId,
			Long dateFrom, Long dateTo, String text, Pageable pageable) {
		List<StudentExperience> result = studentExperienceRepository.searchExperience(studentId, expType, institutional,
				instituteId, teachingUnitId, schoolYear, registrationId, certifierId, dateFrom, dateTo, text, pageable);
		return result;
	}

	public List<StudentExperience> searchStudentExperienceById(String studentId, String instituteId,
			String teachingUnitId, String experienceId, Boolean institutional) {
		List<StudentExperience> result = studentExperienceRepository.searchExperienceById(studentId, instituteId,
				teachingUnitId, experienceId, institutional);
		return result;
	}

	public List<Experience> searchExperience(String expType, Boolean institutional, String instituteId,
			String teachingUnitId, String schoolYear, String certifierId, Long dateFrom, Long dateTo, String text,
			Pageable pageable) {
		List<Experience> result = experienceRepository.searchExperience(expType, institutional, instituteId,
				teachingUnitId, schoolYear, certifierId, dateFrom, dateTo, text, pageable);
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
		if (studentDb != null) {
			studentExperience.setStudent(studentDb);
		}
		studentExperienceRepository.save(studentExperience);
		return experienceDb;
	}

	public Experience updateMyExperience(String studentId, Experience experience)
			throws StorageException, EntityNotFoundException {
		String experienceId = experience.getId();
		Experience experienceDb = experienceRepository.findOne(experienceId);
		if (experienceDb != null) {
			StudentExperience studentExperienceDb = studentExperienceRepository.findByStudentAndExperience(studentId,
					experienceId);
			if (Utils.isCertified(studentExperienceDb)) {
//				throw new StorageException("modify is not allowed");
				throw new StorageException(errorLabelManager.get("modify.not.allowed"));
			}
			experienceDb.setAttributes(experience.getAttributes());
			experienceRepository.save(experienceDb);
			updateExperienceAttributes(experienceId, studentId, experience.getAttributes());
		} else {
//			throw new EntityNotFoundException("entity not found");
			throw new EntityNotFoundException(errorLabelManager.get("esp.error.notfound"));
		}
		return experienceDb;
	}

	public Experience addIsExperience(List<String> studentIds, Experience experience) {
		String experienceId = Utils.getUUID();
		experience.setId(experienceId);
		Experience experienceDb = experienceRepository.save(experience);
		for (String studentId : studentIds) {
			StudentExperience studentExperience = new StudentExperience();
			studentExperience.setId(Utils.getUUID());
			studentExperience.setExperienceId(experienceId);
			studentExperience.setExperience(experienceDb);
			studentExperience.setStudentId(studentId);
			Student studentDb = studentRepository.findOne(studentId);
			if (studentDb != null) {
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
		if (experienceDb != null) {
			experienceDb.setAttributes(experience.getAttributes());
			experienceRepository.save(experienceDb);
			if ((studentIds != null) && (!studentIds.isEmpty())) {
				// get the existing relations
				List<StudentExperience> list = studentExperienceRepository.findByStudentsAndExperience(studentIds,
						experienceId);
				for (StudentExperience studentExperience : list) {
					String studentId = studentExperience.getStudentId();
					if (studentIds.contains(studentId)) {
						if (Utils.isCertified(studentExperience)) {
							continue;
						}
						// update attributes
						updateExperienceAttributes(experienceId, studentId, experience.getAttributes());
					} else {
						// remove old relation
						studentExperienceRepository.delete(studentExperience);
					}
				}
				for (String studentId : studentIds) {
					StudentExperience studentExperience = studentExperienceRepository
							.findByStudentAndExperience(studentId, experienceId);
					if (studentExperience == null) {
						// add new relation
						StudentExperience studentExperienceNew = new StudentExperience();
						studentExperienceNew.setId(Utils.getUUID());
						studentExperienceNew.setExperienceId(experienceId);
						studentExperienceNew.setExperience(experienceDb);
						studentExperienceNew.setStudentId(studentId);
						Student studentDb = studentRepository.findOne(studentId);
						if (studentDb != null) {
							studentExperienceNew.setStudent(studentDb);
						}
						studentExperienceRepository.save(studentExperienceNew);
					}
				}
			}
		} else {
//			throw new EntityNotFoundException("entity not found");
			throw new EntityNotFoundException(errorLabelManager.get("esp.error.notfound"));
		}
		return null;
	}

	public Experience removeExperience(String experienceId) throws EntityNotFoundException, StorageException {
		Experience experienceDb = experienceRepository.findOne(experienceId);
		if (experienceDb == null) {
//			throw new EntityNotFoundException("entity not found");
			throw new EntityNotFoundException(errorLabelManager.get("esp.error.notfound"));
		}
		experienceRepository.delete(experienceDb);
		List<StudentExperience> list = studentExperienceRepository.findByExperienceId(experienceId);
		studentExperienceRepository.delete(list);
		return experienceDb;
	}

	public Student getStudent(String studentId) throws EntityNotFoundException {
		Student result = studentRepository.findOne(studentId);
		if (result == null) {
//			throw new EntityNotFoundException("entity not found");
			throw new EntityNotFoundException(errorLabelManager.get("studente.error.notfound"));
		}
		return result;
	}

	public List<Document> getDocuments(String experienceId, String studentId) throws EntityNotFoundException {
		StudentExperience result = studentExperienceRepository.findByStudentAndExperience(studentId, experienceId);
		if (result == null) {
//			throw new EntityNotFoundException("entity not found");
			throw new EntityNotFoundException(errorLabelManager.get("esp.error.notfound"));
		}
		return result.getDocuments();
	}

	public Document getDocument(String experienceId, String studentId, String storageId)
			throws EntityNotFoundException {
		StudentExperience studentExperience = studentExperienceRepository.findByStudentAndExperience(studentId,
				experienceId);
		if (studentExperience == null) {
			throw new EntityNotFoundException(errorLabelManager.get("esp.error.notfound"));
		}
		Document document = Utils.findDocument(studentExperience, storageId);
		if (document == null) {
//			throw new EntityNotFoundException("document not found");
			throw new EntityNotFoundException(errorLabelManager.get("doc.error.notfound"));
		}
		return document;
	}

	public StudentExperience getStudentExperience(String id)
			throws EntityNotFoundException {
		StudentExperience studentExperience = studentExperienceRepository.findOne(id);
		if (studentExperience == null) {
//			throw new EntityNotFoundException("experience not found");
			throw new EntityNotFoundException(errorLabelManager.get("esp.error.notfound"));
		}
		
		return studentExperience;
	}		
	
	public StudentExperience getStudentExperience(String experienceId, String studentId)
			throws EntityNotFoundException {
		StudentExperience studentExperience = studentExperienceRepository.findByStudentAndExperience(studentId,
				experienceId);
		if (studentExperience == null) {
//			throw new EntityNotFoundException("experience not found");
			throw new EntityNotFoundException(errorLabelManager.get("esp.error.notfound"));
		}
		
		return studentExperience;
	}	
	
	public Document updateDocumentAttributes(String experienceId, String studentId, String storageId,
			Map<String, Object> attributes) throws EntityNotFoundException, StorageException {
		StudentExperience studentExperience = studentExperienceRepository.findByStudentAndExperience(studentId,
				experienceId);
		if (studentExperience == null) {
//			throw new EntityNotFoundException("entity not found");
			throw new EntityNotFoundException(errorLabelManager.get("esp.error.notfound"));
		}
		Document document = Utils.findDocument(studentExperience, storageId);
		if (document == null) {
			throw new StorageException(errorLabelManager.get("doc.error.notfound"));
		}
		document.setAttributes(attributes);
		studentExperienceRepository.save(studentExperience);
		return document;
	}

	public Document addDocument(Document document) throws EntityNotFoundException, StorageException {
		StudentExperience studentExperience = studentExperienceRepository
				.findByStudentAndExperience(document.getStudentId(), document.getExperienceId());
		if (studentExperience == null) {
//			throw new EntityNotFoundException("entity not found");
			throw new EntityNotFoundException(errorLabelManager.get("esp.error.notfound"));
		}
		document.setStorageId(Utils.getUUID());
		document.setDocumentPresent(Boolean.FALSE);
		studentExperience.getDocuments().add(document);
		studentExperienceRepository.save(studentExperience);
		return document;
	}

	public Document removeDocument(String experienceId, String studentId, String storageId)
			throws EntityNotFoundException, StorageException {
		StudentExperience studentExperience = studentExperienceRepository.findByStudentAndExperience(studentId,
				experienceId);
		if (studentExperience == null) {
//			throw new EntityNotFoundException("entity not found");
			throw new EntityNotFoundException(errorLabelManager.get("esp.error.notfound"));
		}
		Document document = Utils.findDocument(studentExperience, storageId);
		if (document == null) {
			throw new StorageException(errorLabelManager.get("doc.error.notfound"));
		}
		studentExperience.getDocuments().remove(document);
		studentExperienceRepository.save(studentExperience);
		return document;
	}

	public StudentExperience certifyMyExperience(String experienceId, String studentId, String certifierId)
			throws StorageException, EntityNotFoundException {
		StudentExperience studentExperienceDb = studentExperienceRepository.findByStudentAndExperience(studentId,
				experienceId);
		if (studentExperienceDb != null) {
			if (Utils.isCertified(studentExperienceDb)) {
//				throw new StorageException("modify is not allowed");
				throw new StorageException(errorLabelManager.get("modify.not.allowed"));
			}
			String refCertifierId = (String) studentExperienceDb.getExperience().getAttributes()
					.get(Const.ATTR_CERTIFIERID);
			if (Utils.isEmpty(refCertifierId) || refCertifierId.equals(certifierId)) {
//				throw new StorageException("ceritfier not allowed");
				throw new StorageException(errorLabelManager.get("certifier.not.allowed"));
			}
			studentExperienceDb.getExperience().getAttributes().put(Const.ATTR_CERTIFIED, Boolean.TRUE);
			studentExperienceRepository.save(studentExperienceDb);
		} else {
//			throw new EntityNotFoundException("entity not found");
			throw new EntityNotFoundException(errorLabelManager.get("esp.error.notfound"));
		}
		return studentExperienceDb;
	}

	public List<CertificationRequest> getCertificationRequestByCertifier(String certifierId, Pageable pageable) {
		Page<CertificationRequest> page = certificationRequestRepository.findByCertifierId(certifierId, pageable);
		List<CertificationRequest> requestList = page.getContent();
		for (CertificationRequest request : requestList) {
			StudentExperience attendance = studentExperienceRepository
					.findByStudentAndExperience(request.getStudentId(), request.getExperienceId());
			if (attendance == null) {
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
		for (CertificationRequest request : requestList) {
			StudentExperience attendance = studentExperienceRepository
					.findByStudentAndExperience(request.getStudentId(), request.getExperienceId());
			if (attendance == null) {
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
		if (experience == null){
			throw new StorageException(errorLabelManager.get("esp.error.notfound"));
		}
		if (student == null) {
			throw new StorageException(errorLabelManager.get("studente.error.notfound"));
		}
		certificationRequest.setExperience(experience);
		certificationRequest.setExperience(experience);
		CertificationRequest certificationRequestDB = certificationRequestRepository.save(certificationRequest);
		return certificationRequestDB;
	}

	public CertificationRequest removeCertificationRequest(String certificationId) throws EntityNotFoundException {
		CertificationRequest certificationRequestDB = certificationRequestRepository.findOne(certificationId);
		if (certificationRequestDB == null) {
//			throw new EntityNotFoundException("entity not found");
			throw new EntityNotFoundException(errorLabelManager.get("cert.error.notfound"));
		}
		certificationRequestRepository.delete(certificationId);
		return certificationRequestDB;
	}

	public List<Institute> getInstitute() {
		List<Institute> result = instituteRepository.findAll(new Sort(Sort.Direction.ASC, "name"));
		return result;
	}

	public void certifyIsExperience(String experienceId, List<String> students)
			throws StorageException, EntityNotFoundException {
		for (String studentId : students) {
			StudentExperience studentExperience = studentExperienceRepository.findByStudentAndExperience(studentId,
					experienceId);
			if (studentExperience == null) {
				continue;
			}
			if (Utils.isCertified(studentExperience)) {
				continue;
			}
			studentExperience.getExperience().getAttributes().put(Const.ATTR_CERTIFIED, Boolean.TRUE);
			studentExperienceRepository.save(studentExperience);
		}
	}

	private void updateExperienceAttributes(String experienceId, String studentId, Map<String, Object> attributes) {
		StudentExperience studentExperience = studentExperienceRepository.findByStudentAndExperience(studentId,
				experienceId);
		if (studentExperience != null) {
			studentExperience.getExperience().getAttributes().putAll(attributes);
			studentExperienceRepository.save(studentExperience);
		}
	}

	public List<Registration> searchRegistration(String studentId, String teachingUnitId, String schoolYear,
			Long dateFrom, Long dateTo, Pageable pageable) {
		List<Registration> result = registrationRepository.searchRegistration(studentId, teachingUnitId, schoolYear,
				dateFrom, dateTo, pageable);
		return result;
	}

	public Page<Registration> fetchRegistrations(Pageable pageable) {
		Page<Registration> result = registrationRepository.findAll(pageable);
		return result;
	}
	
	public Page<Registration> fetchRegistrationsAfterTimestamp(Pageable pageable, Long unixTime) {
		Date date = new Date ();
		date.setTime(unixTime);
		Page<Registration> result = registrationRepository.fetchAllAfterTime(date, pageable);
		return result;
	}

	public Page<Student> fetchStudents(Pageable pageable) {
		Page<Student> result = studentRepository.findAll(pageable);
		return result;
	}
	
	public Page<Student> fetchStudentsAfterTimestamp(Pageable pageable, Long unixTime) {
		Date date = new Date ();
		date.setTime(unixTime);
		Page<Student> result = studentRepository.fetchAllAfterTime(date, pageable);
		return result;
	}

	public Page<Institute> fetchInstitutes(Pageable pageable) {
		Page<Institute> result = instituteRepository.findAll(pageable);
		return result;
	}
	
	public Page<Institute> fetchInstituteAfterTimestamp(Pageable pageable, Long unixTime) {
		Date date = new Date ();
		date.setTime(unixTime);
		Page<Institute> result = instituteRepository.fetchAllAfterTime(date, pageable);
		return result;
	}

	public Page<Certifier> fetchCertifier(Pageable pageable) {
		Page<Certifier> result = certifierRepository.findAll(pageable);
		return result;
	}
	
	public Page<Certifier> fetchCertifierAfterTimestamp(Pageable pageable, Long unixTime) {
		Date date = new Date ();
		date.setTime(unixTime);
		Page<Certifier> result = certifierRepository.fetchAllAfterTime(date, pageable);
		return result;
	}
	
	public Page<CourseMetaInfo> fetchCoursesMetaInfo(Pageable pageable) {
		Page<CourseMetaInfo> result = courseMetaInfoRepo.findAll(pageable);
		return result;
	}
	
	public Page<CourseMetaInfo> fetchCourseMetaInfoAfterTimestamp(Pageable pageable, Long unixTime) {
		Date date = new Date ();
		date.setTime(unixTime);
		Page<CourseMetaInfo> result = courseMetaInfoRepo.fetchAllAfterTime(date, pageable);
		return result;
	}
	
	public Page<Course> fetchCourses(Pageable pageable) {
		Page<Course> result = courseRepository.findAll(pageable);
		return result;
	}
	
	public Page<Course> fetchCourseAfterTimestamp(Pageable pageable, Long unixTime) {
		Date date = new Date ();
		date.setTime(unixTime);
		Page<Course> result = courseRepository.fetchAllAfterTime(date, pageable);
		return result;
	}

	public Page<Professor> fetchProfessori(Pageable pageable) {
		Page<Professor> result = professoriRepository.findAll(pageable);
		return result;
	}
	
	public Page<Professor> fetchProfessorAfterTimestamp(Pageable pageable, Long unixTime) {
		Date date = new Date ();
		date.setTime(unixTime);
		Page<Professor> result = professoriRepository.fetchAllAfterTime(date, pageable);
		return result;
	}

	public Page<ProfessoriClassi> fetchProfessoriClassi(Pageable pageable) {
		Page<ProfessoriClassi> result = professoriClassiRepository.findAll(pageable);
		return result;
	}
	
	public Page<ProfessoriClassi> fetchProfessoriClassiAfterTimestamp(Pageable pageable, Long unixTime) {
		Date date = new Date ();
		date.setTime(unixTime);
		Page<ProfessoriClassi> result = professoriClassiRepository.fetchAllAfterTime(date, pageable);
		return result;
	}
	
	public Page<TeachingUnit> fetchTeachingUnit(Pageable pageable) {
		Page<TeachingUnit> result = teachingUnitRepository.findAll(pageable);
		return result;
	}
	
	public Page<TeachingUnit> fetchTeachingUnitAfterTimestamp(Pageable pageable, Long unixTime) {
		Date date = new Date ();
		date.setTime(unixTime);
		Page<TeachingUnit> result = teachingUnitRepository.fetchAllAfterTime(date, pageable);
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
		if (student == null) {
			throw new StorageException(errorLabelManager.get("studente.error.notfound"));
		}
		if (institute == null) {
			throw new StorageException(errorLabelManager.get("istituto.error.notfound"));
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
		if (Utils.isNotEmpty(consent.getStudentId())) {
			consentDb = consentRepository.findByStudent(consent.getStudentId());
		}
		if ((consentDb == null) && Utils.isNotEmpty(consent.getSubject())) {
			consentDb = consentRepository.findBySubject(consent.getSubject());
		}
		if (consentDb == null) {
			Date now = new Date();
			consent.setCreationDate(now);
			consent.setLastUpdate(now);
			consent.setId(Utils.getUUID());
			consentDb = consentRepository.save(consent);
		} else {
			Date now = new Date();
			consentDb.setLastUpdate(now);
			consentDb.setAuthorized(Boolean.TRUE);
			consentRepository.save(consentDb);
		}
		return consentDb;
	}

	public Consent removeAuthorization(String studentId) throws EntityNotFoundException {
		Consent consentDb = consentRepository.findByStudent(studentId);
		if (consentDb == null) {
//			throw new EntityNotFoundException("entity not found");
			throw new EntityNotFoundException(errorLabelManager.get("ent.error.notfound"));		
		}
		Date now = new Date();
		consentDb.setAuthorized(Boolean.FALSE);
		consentDb.setLastUpdate(now);
		consentRepository.save(consentDb);
		return consentDb;
	}

	public Consent addAuthorization(String studentId) throws EntityNotFoundException {
		Consent consentDb = consentRepository.findByStudent(studentId);
		if (consentDb == null) {
//			throw new EntityNotFoundException("entity not found");
			throw new EntityNotFoundException(errorLabelManager.get("ent.error.notfound"));			
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
		if (studentDb == null) {
//			throw new EntityNotFoundException("entity not found");
			throw new EntityNotFoundException(errorLabelManager.get("ent.error.notfound"));
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
		if (cv == null) {
//			throw new EntityNotFoundException("entity not found");
			throw new EntityNotFoundException(errorLabelManager.get("ent.error.notfound"));
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
		if (cvDb == null) {
//			throw new EntityNotFoundException("entity not found");
			throw new EntityNotFoundException(errorLabelManager.get("ent.error.notfound"));
		}
		cvDb.setStudentExperienceIdMap(cv.getStudentExperienceIdMap());
		cvDb.setRegistrationIdList(cv.getRegistrationIdList());
		cvDb.setStorageIdList(cv.getStorageIdList());
		cvDb.setLastUpdate(now);
		cvRepository.save(cvDb);
		return cvDb;
	}

	public Document addFileToDocument(String experienceId, String studentId, String storageId, String contentType,
			String filename, String url) throws EntityNotFoundException, StorageException {
		StudentExperience studentExperience = studentExperienceRepository.findByStudentAndExperience(studentId,
				experienceId);
		if (studentExperience == null) {
//			throw new EntityNotFoundException("entity not found");
			throw new EntityNotFoundException(errorLabelManager.get("esp.error.notfound"));
		}
		Document document = Utils.findDocument(studentExperience, storageId);
		if (document == null) {
			throw new StorageException(errorLabelManager.get("doc.error.notfound"));
		}
		document.setContentType(contentType);
		document.setFilename(filename);
		document.setUrl(url);
		document.setDocumentPresent(Boolean.TRUE);
		studentExperienceRepository.save(studentExperience);
		return document;
	}

	public Document removeFileToDocument(String experienceId, String studentId, String storageId)
			throws EntityNotFoundException, StorageException {
		StudentExperience studentExperience = studentExperienceRepository.findByStudentAndExperience(studentId,
				experienceId);
		if (studentExperience == null) {
//			throw new EntityNotFoundException("entity not found");
			throw new EntityNotFoundException(errorLabelManager.get("esp.error.notfound"));
		}
		Document document = Utils.findDocument(studentExperience, storageId);
		if (document == null) {
			throw new StorageException(errorLabelManager.get("doc.error.notfound"));
		}
		document.setContentType(null);
		document.setFilename(null);
		document.setUrl(null);
		document.setDocumentPresent(Boolean.FALSE);
		studentExperienceRepository.save(studentExperience);
		return document;
	}

	public List<TeachingUnit> getTeachingUnit(String ordine, String tipologia, String indirizzo) {
		List<Typology> classification = new ArrayList<Typology>();
		if (Utils.isNotEmpty(ordine)) {
			Typology typology = new Typology();
			typology.setQualifiedName(Const.TYPOLOGY_QNAME_ORDINE);
			typology.setName(ordine);
			classification.add(typology);
		}
		if (Utils.isNotEmpty(tipologia)) {
			Typology typology = new Typology();
			typology.setQualifiedName(Const.TYPOLOGY_QNAME_TIPOLOGIA);
			typology.setName(tipologia);
			classification.add(typology);
		}
		if (Utils.isNotEmpty(indirizzo)) {
			Typology typology = new Typology();
			typology.setQualifiedName(Const.TYPOLOGY_QNAME_INDIRIZZO);
			typology.setName(indirizzo);
			classification.add(typology);
		}
		return teachingUnitRepository.findByClassification(classification);
	}
	
	public List<Registration> getRegistrationByTeachingUnit(String teachingUnitId, String schoolYear) {
		List<Registration> result = registrationRepository.searchRegistration(null, teachingUnitId, schoolYear, null,
				null, null);
		return result;
	}

	public TeachingUnit getTeachingUnitById(String teachingUnitId) throws EntityNotFoundException {
		TeachingUnit result = teachingUnitRepository.findOne(teachingUnitId);
		if (result == null) {
//			throw new EntityNotFoundException("entity not found");
			throw new EntityNotFoundException(errorLabelManager.get("ent.error.notfound"));
		}
		return result;
	}

	public Registration getRegistrationById(String registrationId) throws EntityNotFoundException {
		Registration result = registrationRepository.findOne(registrationId);
		if (result == null) {
//			throw new EntityNotFoundException("entity not found");
			throw new EntityNotFoundException(errorLabelManager.get("ent.error.notfound"));
		}
		return result;
	}

	public Experience getExperienceById(String experienceId) throws EntityNotFoundException {
		Experience result = experienceRepository.findOne(experienceId);
		if (result == null) {
//			throw new EntityNotFoundException("entity not found");
			throw new EntityNotFoundException(errorLabelManager.get("esp.error.notfound"));
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

	public Student updateStudentContentType(String studentId, String contentType) throws EntityNotFoundException {
		Student studentDb = studentRepository.findOne(studentId);
		if (studentDb == null) {
//			throw new EntityNotFoundException("entity not found");
			throw new EntityNotFoundException(errorLabelManager.get("ent.error.notfound"));
		}
		studentDb.setContentType(contentType);
		Date now = new Date();
		studentDb.setLastUpdate(now);
		studentRepository.save(studentDb);
		return studentDb;
	}

	public StudentAuth getStudentAuthById(String authId) throws EntityNotFoundException {
		StudentAuth studentAuthDB = studentAuthRepository.findOne(authId);
		if (studentAuthDB == null) {
//			throw new EntityNotFoundException("entity not found");
			throw new EntityNotFoundException(errorLabelManager.get("ent.error.notfound"));
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
		if (studentAuthDB == null) {
//			throw new EntityNotFoundException("entity not found");
			throw new EntityNotFoundException(errorLabelManager.get("ent.error.notfound"));
		}
		studentAuthRepository.delete(studentAuthDB);
		return studentAuthDB;
	}

	public RegistrationStats getRegistrationStats(String schoolYear, String typologyName) {
		List<Registration> registrationList = registrationRepository.findByClassification(typologyName, schoolYear);
		RegistrationStats result = new RegistrationStats();
		result.setYear(schoolYear);
		for (Registration registration : registrationList) {
			Typology typology = registration.getTeachingUnit().getClassifications().get(typologyName);
			if (typology != null) {
				String typologyValue = typology.getName();
				increaseStat(result, typologyValue);
			}
		}
		return result;
	}
	
	// Cedus4School
	
	public List<POI> findTeachingUnits(String ordine, String tipologia, Double coords[], Double radius, String schoolYear) {
		Criteria criteria = new Criteria();
		if (ordine != null) {
			criteria = criteria.and("classifications." + Const.TYPOLOGY_QNAME_ORDINE + ".name").regex("[.]*" + ordine.toLowerCase().trim() + "[.]*", "i");
		}
		if (tipologia != null) {
			criteria = criteria.and("classifications." + Const.TYPOLOGY_QNAME_TIPOLOGIA + ".name").regex(tipologia.toLowerCase().trim(), "i");
		}		
		if (coords != null && radius != null) {
			criteria = criteria.and("geocode").withinSphere(new Circle(new Point(coords[1], coords[0]), new Distance(radius, Metrics.KILOMETERS)));
		}		
		
		Query query = new Query(criteria);
		
		List<TeachingUnit> tus = mongoTemplate.find(query, TeachingUnit.class);
		
		List<POI> res = tus.stream().map(x -> teachingUnitToPOI(x)).collect(Collectors.toList());
		
		if (schoolYear != null && !schoolYear.isEmpty()) {
			res.removeIf(x -> !x.getMetadata().containsKey("schoolYears") || !((Map)x.getMetadata().get("schoolYears")).containsKey(schoolYear));
		}
		
		return res;
	}	
	
	public List<POI> findInstitutes(String ordine, String tipologia, Double coords[], Double radius, String schoolYear) {
		Criteria criteria = new Criteria();
		if (ordine != null) {
			criteria = criteria.and("classifications." + Const.TYPOLOGY_QNAME_ORDINE + ".name").regex("[.]*" + ordine.toLowerCase().trim() + "[.]*", "i");
		}
		if (tipologia != null) {
			criteria = criteria.and("classifications." + Const.TYPOLOGY_QNAME_TIPOLOGIA + ".name").regex(tipologia.toLowerCase().trim(), "i");
		}		
		
		
		Query query = new Query(criteria);
		query.fields().include("instituteId");
		
		List<TeachingUnit> tus = mongoTemplate.find(query, TeachingUnit.class);
		
		List<String> institutesIds = tus.stream().map(x -> x.getInstituteId()).collect(Collectors.toList());
		
		criteria = new Criteria("_id").in(institutesIds);
		if (coords != null && radius != null) {
			criteria = criteria.and("geocode").withinSphere(new Circle(new Point(coords[1], coords[0]), new Distance(radius, Metrics.KILOMETERS)));
		}
		
		query = new Query(criteria);
		
		List<Institute> ins = mongoTemplate.find(query, Institute.class);
		
		List<POI> res = ins.stream().map(x -> instituteToPOI(x)).collect(Collectors.toList());
		
		if (schoolYear != null && !schoolYear.isEmpty()) {
			res.removeIf(x -> !x.getMetadata().containsKey("schoolYears") || !((Map)x.getMetadata().get("schoolYears")).containsKey(schoolYear));
		}
		
		return res;
	}		
	
	private POI teachingUnitToPOI(TeachingUnit tu) {
		POI poi = new POI();
		
//		poi.setId("TeachingUnit-" + tu.getExtId());
		poi.setId(tu.getId());
		poi.setName(tu.getName());
		poi.setDescription(tu.getDescription());
		Address address = new Address();
		address.setAddressCountry("IT");
		address.setAddressLocality(tu.getAddress());
		poi.setAddress(address);
		Location location = new Location();
		location.setType("Point");
		if (tu.getGeocode() != null) {
			location.setCoordinates(new Double[] {tu.getGeocode()[1], tu.getGeocode()[0]});
		}
		poi.setLocation(location);
		poi.setSource("");
		
		fillTeachingUnitPOIMetadata(tu, poi);
		
		return poi;
	}
	
	private POI instituteToPOI(Institute is) {
		POI poi = new POI();
		
//		poi.setId("TeachingUnit-" + tu.getExtId());
		poi.setId(is.getId());
		poi.setName(is.getName());
		poi.setDescription(is.getDescription());
		Address address = new Address();
		address.setAddressCountry("IT");
		address.setAddressLocality(is.getAddress());
		poi.setAddress(address);
		Location location = new Location();
		location.setType("Point");
		if (is.getGeocode() != null) {
			location.setCoordinates(new Double[] {is.getGeocode()[1], is.getGeocode()[0]});
		}
		poi.setLocation(location);
		poi.setSource("");
		
		fillInstitutePOIMetadata(is, poi);
		
		return poi;
	}	
	
	private void fillTeachingUnitPOIMetadata(TeachingUnit tu, POI poi) {
		Criteria criteria = new Criteria("teachingUnitId").is(tu.getId());
		Query query = new Query(criteria);
		query.fields().include("id");

//		List<String> cIds = mongoTemplate.find(query, Registration.class).stream().map(x -> x.getId()).collect(Collectors.toList());

		Aggregation aggr = newAggregation(match(criteria), group("schoolYear").count().as("total"), project("total").and("schoolYear").previousOperation(), sort(Sort.Direction.DESC, "total"));
		
		AggregationResults<Map> groupResults = mongoTemplate.aggregate(aggr, Registration.class, Map.class);
		Map result = groupResults.getMappedResults().stream().collect(Collectors.toMap(x -> (String)x.get("schoolYear"), x -> x.get("total")));

		if (!result.isEmpty()) {
			poi.getMetadata().put("schoolYears", result);
		}
		poi.getMetadata().put("ordine", tu.getClassifications().get("ORDINE").getName());
		poi.getMetadata().put("tipologia", tu.getClassifications().get("TIPOLOGIA").getName());
	}
	
	private void fillInstitutePOIMetadata(Institute tu, POI poi) {
		Criteria criteria = new Criteria("instituteId").is(tu.getId());
		Query query = new Query(criteria);
		query.fields().include("id");

		Aggregation aggr = newAggregation(match(criteria), group("schoolYear").count().as("total"), project("total").and("schoolYear").previousOperation(), sort(Sort.Direction.DESC, "total"));
		
		AggregationResults<Map> groupResults = mongoTemplate.aggregate(aggr, Registration.class, Map.class);
		Map result = groupResults.getMappedResults().stream().collect(Collectors.toMap(x -> (String)x.get("schoolYear"), x -> x.get("total")));

		if (!result.isEmpty()) {
			poi.getMetadata().put("schoolYears", result);
		}
	}	
	
	public List<CourseData> findCourses(String tuId, String insId, String schoolYear) {
		Criteria criteria = new Criteria();
		if (tuId != null) {
			criteria = criteria.and("teachingUnitId").is(tuId);
		}
		if (insId != null) {
			criteria = criteria.and("instituteId").is(insId);
		}		
		if (schoolYear != null) {
			criteria = criteria.and("schoolYear").is(schoolYear);
		}		
		
		Aggregation aggr = newAggregation(match(criteria), lookup("courseMetaInfo", "courseMetaInfoId", "_id", "metainfo"));
		
		AggregationResults<Map> groupResults = mongoTemplate.aggregate(aggr, Course.class, Map.class);
		
		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		List<CourseData> res = groupResults.getMappedResults().stream().map(x -> {
			CourseData cd = mapper.convertValue(x, CourseData.class);
			CourseMetaInfo cmi = (CourseMetaInfo)((List)x.get("metainfo")).get(0);
			cd.setId((String)x.get("_id"));
			if (cmi != null) {
				cd.setMiurCode(cmi.getCodMiur());
			}
			return cd;
		}).collect(Collectors.toList());
		
		return res;
	}	
	
	
	public List<KPI> getInstituteKPIs(String insId, String schoolYear) {
		List<KPI> results = Lists.newArrayList();

		int year = Calendar.getInstance().get(Calendar.YEAR);

		Criteria criteria = new Criteria("instituteId").is(insId).and("schoolYear").is(schoolYear);
		Query query = new Query(criteria);
		query.fields().include("student").include("courseId");

//		TeachingUnit tu = teachingUnitRepository.findOne(tuId);
		Institute ins = instituteRepository.findOne(insId);
		
		// List<Student> students = mongoTemplate.find(query, Registration.class).stream().map(x -> x.getStudent()).collect(Collectors.toList());

		Multimap<String, Student> studentsMap = ArrayListMultimap.create();
		mongoTemplate.find(query, Registration.class).forEach(x -> {
			studentsMap.put(x.getCourseId(), x.getStudent());
		});

		Map<String, CourseMetaInfo> coursesMap = Maps.newTreeMap();
		for (String courseId : studentsMap.keySet()) {
			coursesMap.put(courseId, courseMetaInfoRepo.findOne(courseId));
		}

		for (String courseId : studentsMap.keySet()) {

			Collection<Student> students = studentsMap.get(courseId);

			int nM = 0;
			int nF = 0;

			Map<Integer, Integer> ages = Maps.newTreeMap();
			for (Student student : students) {
				Integer bd = Integer.parseInt(student.getCf().substring(9, 11));
				if (bd < 40) {
					nM++;
				} else {
					nF++;
				}
				// TODO: age by full birth date? -> changing day by day
				Integer by = Integer.parseInt(student.getBirthdate().split("/")[2]);
				int age = year - by;

				ages.put(age, ages.getOrDefault(age, 0) + 1);
			}
			
			results.addAll(buildCourseKPI(ins, coursesMap.get(courseId), schoolYear, nF, nM, ages));
		}

		return results;
	}
	
	private List<KPI> buildCourseKPI(Institute institute, CourseMetaInfo course, String schoolYear, int nF, int nM, Map<Integer, Integer> ages) {
		List<KPI> results = Lists.newArrayList();
		
		KPI kpiF = new KPI();
		kpiF.setId("-F");
		kpiF.setKpiValue(nF);
		kpiF.setName("Female students");
		kpiF.setDescription("Number of female students for institute " + institute.getName() + ", course " + course.getCourse() + ((schoolYear != null) ? (", schoolyear " + schoolYear) : ""));
		results.add(kpiF);
		
		KPI kpiM = new KPI();
		kpiM.setId("-M");
		kpiM.setKpiValue(nM);
		kpiM.setName("Male students");
		kpiM.setDescription("Number of male students for institute " + institute.getName() + ", course " + course.getCourse() + ((schoolYear != null) ? (", schoolyear " + schoolYear) : ""));
		results.add(kpiM);
		
		for (Integer age: ages.keySet()) {
			KPI kpiA = new KPI();
			kpiA.setId("-A" + age);
			kpiA.setName("Students of age " + age);
			kpiA.setDescription("Number of students of age " + age + "  for institute " + institute.getName() + ", course " + course.getCourse() + ((schoolYear != null) ? (", schoolyear " + schoolYear) : ""));
			kpiA.setKpiValue(ages.get(age));
			results.add(kpiA);
		}
		
		Organization org = new Organization();
		org.setIdentifier(institute.getId());
		org.setName(institute.getName());
		Product prod = new Product();
		prod.setIdentifier(course.getId());
		prod.setName(course.getCourse());
		Provider prov = new Provider();
		prov.setName("FBK");
		Address addr = new Address();
		addr.setAddressCountry("IT");
		addr.setAddressLocality(institute.getAddress());
		results.forEach(x -> {
			x.getCategory().add("quantitative");
			x.setOrganization(org);
			x.setProduct(prod);
			x.setProvider(prov);
			x.setAddress(addr);
			x.setCalculationFrequency("hourly");
			x.setDateModified(new Date());
			x.setId(institute.getId() + "-" + course.getId() + x.getId());
		});
		
		return results;
	}
	

	
	public StudentProfile getStudentProfile(String cf) throws EntityNotFoundException {
		Student student = studentRepository.findByCF(cf);
		if(student == null) {
			throw new EntityNotFoundException("entity not found: Student");
		}
		
		StudentProfile profile = new StudentProfile();
		
		List<Registration> registrations = registrationRepository.findByStudent(student.getId());
		for (Registration registration: registrations) {
			SchoolRegistration sr = new SchoolRegistration();
			sr.setYear(registration.getSchoolYear());
			sr.setClassroom(registration.getClassroom());
			Institute institute = instituteRepository.findOne(registration.getInstituteId());
			if (institute == null) {
//				throw new EntityNotFoundException("entity not found: Institute");
				throw new EntityNotFoundException(errorLabelManager.get("istituto.error.notfound"));
			}
			sr.setInstitute(institute.getName());
			CourseMetaInfo course = courseMetaInfoRepo.findOne(registration.getCourseId());
			if (course == null) {
//				throw new EntityNotFoundException("entity not found: CourseMetaInfo");
				throw new EntityNotFoundException(errorLabelManager.get("metainfo.error.notfound"));
			}			
			sr.setCourse(course.getCourse());
			profile.getRegistrations().add(sr);
		};
		
		
		profile.setId(student.getId());
		profile.setName(student.getName());
		profile.setSurname(student.getSurname());
		profile.setBirthdate(student.getBirthdate());
		profile.setCf(student.getCf());
		profile.setAddress(student.getAddress());
		
		List<StudentExperience> experiences = studentExperienceRepository.findByStudentId(student.getId());
		
		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		List<Stage> stages = experiences.stream().filter(x -> "STAGE".equals(x.getExperience().getType())).map(x -> mapper.convertValue(x.getExperience().getAttributes(), Stage.class)).collect(Collectors.toList());
		
		profile.setStages(stages);
		
		return profile;
	}
	
	public void deleteNonActiveInstitute() {	
		List<String> activeIds = getActiveInstituteIds();
		if(activeIds.size() > 0) {
			Criteria criteria = new Criteria("id").nin(activeIds);
			Query query = new Query(criteria);
			mongoTemplate.findAllAndRemove(query, Institute.class);
		}
	}

	public void deleteNonActiveTeachingUnit() {
		List<String> activeIds = getActiveInstituteIds();
		if(activeIds.size() > 0) {
			Criteria criteria = new Criteria("instituteId").nin(activeIds);
			Query query = new Query(criteria);
			mongoTemplate.findAllAndRemove(query, TeachingUnit.class);
		}
	}

	public void deleteNonActiveCourse() {
		List<String> activeIds = getActiveInstituteIds();
		if(activeIds.size() > 0) {
			Criteria criteria = new Criteria("instituteId").nin(activeIds);
			Query query = new Query(criteria);
			mongoTemplate.findAllAndRemove(query, Course.class);
		}
	}

	public void deleteNonActiveRegistration() {
		List<String> activeIds = getActiveInstituteIds();
		if(activeIds.size() > 0) {
			Criteria criteria = new Criteria("instituteId").nin(activeIds);
			Query query = new Query(criteria);
			mongoTemplate.findAllAndRemove(query, Registration.class);
		}
	}

	public void deleteNonActiveStudent() {
		List<String> activeIds = getActiveInstituteIds();
		List<String> studentIds = new ArrayList<>();
		if(activeIds.size() > 0) {
			int page = 0;
			List<Student> studentList = new ArrayList<>();
			do {
				Pageable pageable = new PageRequest(page, 500, new Sort("id"));
				studentList = mongoTemplate.find(new Query().with(pageable), Student.class);
				logger.info(String.format("page %s - %s", page, studentList.size()));
				for(Student student : studentList) {
					long num = getStudentRegistrationNum(student.getId());
					//logger.info(String.format("student %s - %s", student.getId(), num));
					if(num == 0) {
						studentIds.add(student.getId());
					}
				}
				page++;
			} while (!studentList.isEmpty()); 
			int count = 1;
			List<String> studentToDelete = new ArrayList<>();
			for(String studentId : studentIds) {
				studentToDelete.add(studentId);
				count++;
				if(count > 5000) {
					Criteria criteria = new Criteria("id").in(studentToDelete);
					Query query = new Query(criteria);
					mongoTemplate.findAllAndRemove(query, Student.class);
					logger.info("removed " + studentToDelete.size() + " students");
					studentToDelete.clear();
					count = 1;
				}
			}
			if(studentToDelete.size() > 0) {
				Criteria criteria = new Criteria("id").in(studentToDelete);
				Query query = new Query(criteria);
				mongoTemplate.findAllAndRemove(query, Student.class);			
				logger.info("removed " + studentToDelete.size() + " students");
			}
		}
	}

	private long getStudentRegistrationNum(String studentId) {
		Criteria criteria = new Criteria("studentId").is(studentId);
		Query query = new Query(criteria);
		return mongoTemplate.count(query, Registration.class);
	}

	private List<String> getActiveInstituteIds() {
		List<ActiveInstitute> activeList = activeInstituteRepository.findAll();
		List<String> activeIds = new ArrayList<>();
		activeList.forEach(e -> {
			activeIds.add(e.getInstituteId());
		});
		return activeIds;
	}

//	public void countOrphansTeachingUnits() {
//		Criteria criteria = new Criteria();
//		Query query = new Query(criteria);
//		query.fields().include("id");
//
//		List<String> tuIds = mongoTemplate.find(query, TeachingUnit.class).stream().map(x -> x.getId()).collect(Collectors.toList());
//		
//		System.err.println("TU: " + tuIds.size());
//		
//		Map<Integer, Integer> courseMap = Maps.newTreeMap();
//		
//		int tot = 0;
//		for (String tuId: tuIds) {
//			criteria = new Criteria("teachingUnitId").is(tuId);
//			query = new Query(criteria);
//			query.fields().include("id");
//
//			List<String> cIds = mongoTemplate.find(query, Course.class).stream().map(x -> x.getId()).collect(Collectors.toList());
//			courseMap.put(cIds.size(), courseMap.getOrDefault(cIds.size(),0) + 1);
//			tot += cIds.size();
//		}
//
//		System.err.println("COURSES: " + tot + "/" + mongoTemplate.count(new Query(), Course.class));
//		System.err.println(courseMap);
//		
//		Map<Integer, Integer> registrationMap = Maps.newTreeMap();
//		
//		tot = 0;
//		for (String tuId: tuIds) {
//			criteria = new Criteria("teachingUnitId").is(tuId);
//			query = new Query(criteria);
//			query.fields().include("id");
//
//			List<String> cIds = mongoTemplate.find(query, Registration.class).stream().map(x -> x.getId()).collect(Collectors.toList());
//			registrationMap.put(cIds.size(), registrationMap.getOrDefault(cIds.size(),0) + 1);
//			tot += cIds.size();
//		}
//		
//		System.err.println("REGISTRATIONS: " + tot + "/" + mongoTemplate.count(new Query(), Registration.class));
//		System.err.println(registrationMap);		
//		
//	}
	
	
	// ----
	
	private void increaseStat(RegistrationStats stats, String typologyValue) {
		boolean found = false;
		for (KeyValue keyValue : stats.getValues()) {
			if (keyValue.getName().equals(typologyValue)) {
				int count = (Integer) keyValue.getValue();
				count++;
				keyValue.setValue(count);
				found = true;
				break;
			}
		}
		if (!found) {
			KeyValue keyValue = new KeyValue();
			keyValue.setName(typologyValue);
			keyValue.setValue(1);
			stats.getValues().add(keyValue);
		}
	}

}
