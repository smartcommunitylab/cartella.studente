package it.smartcommunitylab.csengine.controller;

import io.swagger.annotations.ApiParam;
import it.smartcommunitylab.aac.authorization.beans.AuthorizationDTO;
import it.smartcommunitylab.aac.authorization.beans.AuthorizationUserDTO;
import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.exception.EntityNotFoundException;
import it.smartcommunitylab.csengine.exception.StorageException;
import it.smartcommunitylab.csengine.exception.UnauthorizedException;
import it.smartcommunitylab.csengine.model.CV;
import it.smartcommunitylab.csengine.model.Document;
import it.smartcommunitylab.csengine.model.CertificationRequest;
import it.smartcommunitylab.csengine.model.Experience;
import it.smartcommunitylab.csengine.model.Registration;
import it.smartcommunitylab.csengine.model.Student;
import it.smartcommunitylab.csengine.model.StudentAuth;
import it.smartcommunitylab.csengine.model.StudentExperience;
import it.smartcommunitylab.csengine.model.TeachingUnit;
import it.smartcommunitylab.csengine.storage.DocumentManager;
import it.smartcommunitylab.csengine.storage.RepositoryManager;
import it.smartcommunitylab.csengine.ui.StudentRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class StudentController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(StudentController.class);
	
	@Autowired
	@Value("${apiToken}")	
	private String apiToken;
		
	@Autowired
	private RepositoryManager dataManager;
	
	@Autowired
	private DocumentManager documentManager;

	@RequestMapping(value = "/api/student/{studentId}", method = RequestMethod.GET)
	public @ResponseBody Student getStudentById(@PathVariable String studentId,
			HttpServletRequest request) throws Exception {
		if (!validateAuthorizationByStudentId(studentId, "Student", "ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Student result = dataManager.getStudent(studentId);
		result.setImageUrl(documentManager.getPhotoSignedUrl(studentId));
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getStudentById[%s]: %s", "tenant", result.getId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}", method = RequestMethod.PUT)
	public @ResponseBody Student updateStudentContact(@PathVariable String studentId,
			@RequestBody Student student,
			HttpServletRequest request) throws Exception {
		if (!validateAuthorizationByStudentId(studentId, "Student", "ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		student.setId(studentId);
		Student result = dataManager.updateStudentContact(student);
		result.setImageUrl(documentManager.getPhotoSignedUrl(studentId));
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateStudentContact[%s]: %s", "tenant", result.getId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/photo/file", 
			method = RequestMethod.POST)
	public @ResponseBody String uploadPhotoProfile(
			@PathVariable String studentId,
			@RequestParam("file") MultipartFile file,
			HttpServletRequest request) throws Exception {
		if (!validateAuthorizationByStudentId(studentId, "PhotoProfile", "ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		documentManager.addFileToProfile(studentId, file);
		String url = documentManager.getPhotoSignedUrl(studentId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("uploadPhotoProfile[%s]: %s", "tenant", studentId));
		}
		return url;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/photo", method = RequestMethod.GET)
	public @ResponseBody String getPhotoProfile(
			@PathVariable String studentId,
			HttpServletRequest request) throws Exception {
		if (!validateAuthorizationByStudentId(studentId, "PhotoProfile", "ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		String url = documentManager.getPhotoSignedUrl(studentId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getPhotoProfile[%s]: %s", "tenant", studentId));
		}
		return url;
	}

	@RequestMapping(value = "/api/student/{studentId}/experience/{experienceId}/document/{storageId}/link", method = RequestMethod.GET)
	public @ResponseBody String getDocumentLink(
			@PathVariable String studentId,
			@PathVariable String experienceId,
			@PathVariable String storageId,
			HttpServletRequest request) throws Exception {
		if (!validateAuthorizationByResource(studentId, "Document", experienceId, "storageId", storageId, 
				"ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Document document = dataManager.getDocument(experienceId, studentId, storageId);
		String url = documentManager.getDocumentSignedUrl(document);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getDocumentLink[%s]: %s", "tenant", studentId));
		}
		return url;
	}

//	@RequestMapping(value = "/api/student/experience/{experienceId}", method = RequestMethod.GET)
//	public @ResponseBody List<Student> getStudentsByExperience(
//			@PathVariable String experienceId,
//			@RequestParam(required=false) String teachingUnitId,
//			@RequestParam(required=false) String schoolYear,
//			@ApiParam Pageable pageable,
//			HttpServletRequest request) throws Exception {
//		if (!Utils.validateAPIRequest(request, apiToken)) {
//			throw new UnauthorizedException("Unauthorized Exception: token not valid");
//		}
//		List<Student> result = dataManager.searchStudentByExperience(experienceId, teachingUnitId, schoolYear, pageable);
//		if(logger.isInfoEnabled()) {
//			logger.info(String.format("getStudentsByExperience[%s]: %s", "tenant", result.size()));
//		}
//		return result;
//	}
	
	@RequestMapping(value = "/api/student/{studentId}/experience/{expType}", method = RequestMethod.GET)
	public @ResponseBody List<StudentExperience> getExperiencesByStudent(
			@PathVariable String studentId,
			@PathVariable String expType,
			@RequestParam(required=false) Boolean institutional,
			@RequestParam(required=false) String instituteId,
			@RequestParam(required=false) String teachingUnitId,
			@RequestParam(required=false) String schoolYear,
			@RequestParam(required=false) String registrationId,
			@RequestParam(required=false) String certifierId,
			@RequestParam(required=false) Long dateFrom,
			@RequestParam(required=false) Long dateTo,
			@RequestParam(required=false) String text,	
			@ApiParam Pageable pageable,
			HttpServletRequest request) throws Exception {
		if (!validateAuthorizationByExp(studentId, "Experience", null, expType, schoolYear, institutional, 
				"ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<StudentExperience> result = dataManager.searchStudentExperience(studentId, expType, institutional, 
				instituteId, teachingUnitId, schoolYear, registrationId, certifierId, dateFrom, dateTo, text, pageable);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getExperiencesByStudent[%s]: %s", "tenant", result.size()));
		}
		return result;		
	}
	
	@RequestMapping(value = "/api/student/{studentId}/my/experience", method = RequestMethod.POST)
	public @ResponseBody Experience addMyExperience(
			@PathVariable String studentId,
			@RequestParam(required=false) String certifierId,
			@RequestBody Experience experience,
			HttpServletRequest request) throws Exception {
		if (!validateAuthorizationByExp(studentId, "Experience", null, null, null, Boolean.FALSE, 
				"ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		experience.getAttributes().put(Const.ATTR_INSTITUTIONAL, Boolean.FALSE);
		if(Utils.isNotEmpty(certifierId)) {
			experience.getAttributes().put(Const.ATTR_CERTIFIERID, certifierId);
		}
		Experience result = dataManager.addMyExperience(studentId, experience);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addMyExperience[%s]: %s - %s", "tenant", studentId, result.getId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/my/experience/{experienceId}", method = RequestMethod.PUT)
	public @ResponseBody Experience updateMyExperience(
			@PathVariable String studentId,
			@PathVariable String experienceId,
			@RequestParam(required=false) String certifierId,
			@RequestBody Experience experience,
			HttpServletRequest request) throws Exception {
		if (!validateAuthorizationByExp(studentId, "Experience", experienceId, null, null, Boolean.FALSE, 
				"ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		experience.setId(experienceId);
		experience.getAttributes().put(Const.ATTR_INSTITUTIONAL, Boolean.FALSE);
		if(Utils.isNotEmpty(certifierId)) {
			experience.getAttributes().put(Const.ATTR_CERTIFIERID, certifierId);
		}
		Experience result = dataManager.updateMyExperience(studentId, experience);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateMyExperience[%s]: %s - %s", "tenant", studentId, result.getId()));
		}
		return result;
	}
		
	@RequestMapping(value = "/api/student/{studentId}/my/experience/{experienceId}", method = RequestMethod.DELETE)
	public @ResponseBody Experience deleteMyExperience(
			@PathVariable String studentId,
			@PathVariable String experienceId,
			HttpServletRequest request) throws Exception {
		if (!validateAuthorizationByExp(studentId, "Experience", experienceId, null, null, Boolean.FALSE, 
				"ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Experience result = dataManager.removeExperience(experienceId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteMyExperience[%s]: %s - %s", "tenant", studentId, result.getId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/registration", method = RequestMethod.GET)
	public @ResponseBody List<StudentRegistration> getStudentRegistration(
			@PathVariable String studentId,
			HttpServletRequest request) throws Exception {
		if (!validateAuthorizationByResource(studentId, "Registration", null, null, null, "ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		//convert to StudentRegistration
		Map<TeachingUnit, List<Registration>> registrationMap = new HashMap<TeachingUnit, List<Registration>>();
		List<Registration> registrations = dataManager.getRegistrationByStudent(studentId);
		for(Registration registration : registrations) {
			TeachingUnit teachingUnit = registration.getTeachingUnit();
			List<Registration> registrationList = registrationMap.get(teachingUnit);
			if(registrationList == null) {
				registrationList = new ArrayList<Registration>();
				registrationMap.put(teachingUnit, registrationList);
			}
			registrationList.add(registration);
		}
		List<StudentRegistration> result = new ArrayList<StudentRegistration>();
		for(TeachingUnit teachingUnit : registrationMap.keySet()) {
			StudentRegistration studentRegistration = new StudentRegistration();
			studentRegistration.setTeachingUnit(teachingUnit);
			studentRegistration.setRegistrations(registrationMap.get(teachingUnit));
			result.add(studentRegistration);
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getStudentRegistration[%s]: %s - %s", "tenant", studentId, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/registration/{registrationId}/subject", 
			method = RequestMethod.GET)
	public @ResponseBody List<StudentExperience> getSubjectsByRegistration(
			@PathVariable String studentId,
			@PathVariable String registrationId,
			HttpServletRequest request) throws Exception {
		if (!validateAuthorizationByResource(studentId, "Registration", null, "registrationId", registrationId, 
				"ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Registration registration = dataManager.getRegistrationById(registrationId);
		List<StudentExperience> result = dataManager.searchStudentExperience(studentId, Const.EXP_TYPE_SUBJECT, 
				Boolean.TRUE, registration.getInstituteId(), registration.getTeachingUnitId(), registration.getSchoolYear(),
				registrationId, null, null, null, null, null);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getSubjectsByRegistration[%s]: %s - %s - %s", "tenant", studentId, 
					registrationId, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/cv", method = RequestMethod.GET)
	public @ResponseBody CV getStudentCV(
			@PathVariable String studentId,
			HttpServletRequest request) throws Exception {
		if (!validateAuthorizationByResource(studentId, "CV", null, null, null, "ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		CV result = dataManager.getStudentCV(studentId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getStudentCV[%s]: %s - %s", "tenant", studentId, result.getId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/cv", method = RequestMethod.POST)
	public @ResponseBody CV addStudentCV(
			@PathVariable String studentId,
			@RequestBody CV cv,
			HttpServletRequest request) throws Exception {
		if (!validateAuthorizationByResource(studentId, "CV", null, null, null, "ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		cv.setStudentId(studentId);
		CV result = dataManager.addStudentCV(cv);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addStudentCV[%s]: %s - %s", "tenant", studentId, result.getId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/cv", method = RequestMethod.PUT)
	public @ResponseBody CV updateStudentCV(
			@PathVariable String studentId,
			@RequestBody CV cv,
			HttpServletRequest request) throws Exception {
		if (!validateAuthorizationByResource(studentId, "CV", null, null, null, "ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		cv.setStudentId(studentId);
		CV result = dataManager.updateStudentCV(cv);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateStudentCV[%s]: %s - %s", "tenant", studentId, result.getId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/experience/{experienceId}/document", method = RequestMethod.POST)
	public @ResponseBody Document addDocumentToExperience(
			@PathVariable String experienceId,
			@PathVariable String studentId,
			@RequestBody Document document,
			HttpServletRequest request) throws Exception {
		if (!validateAuthorizationByResource(studentId, "Document", experienceId, null, null, 
				"ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		document.setExperienceId(experienceId);
		document.setStudentId(studentId);
		Document result = dataManager.addDocument(document);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addDocumentToExperience[%s]: %s", "tenant", result.getStorageId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/experience/{experienceId}/document/{storageId}/attributes", 
			method = RequestMethod.PATCH)
	public @ResponseBody Document updateDocumentAttributes(
			@PathVariable String experienceId,
			@PathVariable String studentId,
			@PathVariable String storageId,
			@RequestBody Map<String, Object> attributes,
			HttpServletRequest request) throws Exception {
		if (!validateAuthorizationByResource(studentId, "Document", experienceId, "storageId", storageId, 
				"ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Document result = dataManager.updateDocumentAttributes(experienceId, studentId, storageId, attributes);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateDocumentAttributes[%s]: %s", "tenant", result.getStorageId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/experience/{experienceId}/document/{storageId}", 
			method = RequestMethod.DELETE)
	public @ResponseBody Document deleteDocument(
			@PathVariable String experienceId,
			@PathVariable String studentId,
			@PathVariable String storageId,
			HttpServletRequest request) throws Exception {
		if (!validateAuthorizationByResource(studentId, "Document", experienceId, "storageId", storageId, 
				"ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		documentManager.removeFileFromDocument(experienceId, studentId, storageId);
		Document result = dataManager.removeDocument(experienceId, studentId, storageId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteDocument[%s]: %s", "tenant", result.getStorageId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/experience/{experienceId}/document/{storageId}/file", 
			method = RequestMethod.POST)
	public @ResponseBody Document addFileToDocument(
			@PathVariable String experienceId,
			@PathVariable String studentId,
			@PathVariable String storageId,
			@RequestParam("file") MultipartFile file,
			@RequestParam("filename") String filename,
			HttpServletRequest request) throws Exception {
		if (!validateAuthorizationByResource(studentId, "Document", experienceId, "storageId", storageId, 
				"ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Document result = documentManager.addFileToDocument(experienceId, studentId,storageId, filename, file);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addFileToDocument[%s]: %s", "tenant", result.getStorageId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/experience/{experienceId}/document/{storageId}/file", 
			method = RequestMethod.DELETE)
	public @ResponseBody Document deleteFileFromDocument(
			@PathVariable String experienceId,
			@PathVariable String studentId,
			@PathVariable String storageId,
			HttpServletRequest request) throws Exception {
		if (!validateAuthorizationByResource(studentId, "Document", experienceId, "storageId", storageId, 
				"ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Document result = documentManager.removeFileFromDocument(experienceId, studentId, storageId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteFileFromDocument[%s]: %s", "tenant", result.getStorageId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/certification", method = RequestMethod.GET)
	public @ResponseBody List<CertificationRequest> getCertificationRequest(
			@PathVariable String studentId,
			@ApiParam Pageable pageable,
			HttpServletRequest request) throws Exception {
		if (!validateAuthorizationByResource(studentId, "Certification", null, null, null, 
				"ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<CertificationRequest> result = dataManager.getCertificationRequestByStudent(studentId, pageable);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getCertificationRequest[%s]: %s - %s", "tenant", studentId, result.size()));
		}
		return result;		
	}
	
	@RequestMapping(value = "/api/student/{studentId}/certification", method = RequestMethod.POST)
	public @ResponseBody CertificationRequest addCertificationRequest(
			@PathVariable String studentId,
			@RequestBody CertificationRequest certificationRequest,
			HttpServletRequest request) throws Exception {
		if (!validateAuthorizationByResource(studentId, "Certification", null, null, null, 
				"ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		certificationRequest.setStudentId(studentId);
		CertificationRequest result = dataManager.addCertificationRequest(certificationRequest);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addCertificationRequest[%s]: %s - %s", "tenant", studentId, result.getId()));
		}
		return result;		
	}
	
	@RequestMapping(value = "/api/student/{studentId}/certification/{certificationId}", method = RequestMethod.DELETE)
	public @ResponseBody CertificationRequest deleteCertificationRequest(
			@PathVariable String studentId,
			@PathVariable String certificationId,
			HttpServletRequest request) throws Exception {
		if (!validateAuthorizationByResource(studentId, "Certification", null, "certificationId", certificationId, 
				"ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		CertificationRequest result = dataManager.removeCertificationRequest(certificationId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteCertificationRequest[%s]: %s - %s", "tenant", studentId, result.getId()));
		}
		return result;		
	}
	
	@RequestMapping(value = "/api/student/{studentId}/auth", method = RequestMethod.GET)
	public @ResponseBody List<StudentAuth> getAuthorization(
			@PathVariable String studentId,
			HttpServletRequest request) throws Exception {
		if (!validateAuthorizationByResource(studentId, "Authorization", null, null, null, 
				"ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<StudentAuth> result = dataManager.getStudentAuthByStudent(studentId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getAuthorization[%s]: %s - %s", "tenant", studentId, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/auth", method = RequestMethod.POST)
	public @ResponseBody StudentAuth addAuthorization(
			@PathVariable String studentId,
			@RequestBody AuthorizationDTO auth,
			HttpServletRequest request) throws Exception {
		if (!validateAuthorizationByResource(studentId, "Authorization", null, null, null, 
				"ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		String subject = getSubject(getAccoutProfile(request));
		AuthorizationUserDTO subjectDTO = authorizationManager.getSubject(subject);
		auth.setSubject(subjectDTO);
		AuthorizationDTO authorizationDTO = authorizationManager.insertAuthorization(auth);
		StudentAuth studentAuth = new StudentAuth();
		studentAuth.setStudentId(studentId);
		studentAuth.setAuth(authorizationDTO);
		StudentAuth result = dataManager.addStudentAuth(studentAuth);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addAuthorization[%s]: %s - %s", "tenant", studentId, result.getId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/auth/{authId}", method = RequestMethod.DELETE)
	public @ResponseBody StudentAuth deleteAuthorization(
			@PathVariable String studentId,
			@PathVariable String authId,
			HttpServletRequest request) throws Exception {
		if (!validateAuthorizationByResource(studentId, "Authorization", null, "authId", authId, 
				"ALL", request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		StudentAuth result = dataManager.getStudentAuthById(authId);
		authorizationManager.deleteAuthorization(result.getAuth().getId());
		dataManager.removeStudentAuth(authId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteAuthorization[%s]: %s - %s", "tenant", studentId, authId));
		}
		return result;
	}
	
	private boolean validateAuthorizationByStudentId(String studentId, String dataType, 
			String action, HttpServletRequest request) throws Exception {
		String subject = getSubject(getAccoutProfile(request));
		String resourceName = "student-data";
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("student-studentId", studentId);
		attributes.put("student-data-dataType", dataType);
		AuthorizationDTO authorization = authorizationManager.getAuthorization(subject, action, 
				resourceName, attributes);
		if(!authorizationManager.validateAuthorization(authorization)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid or call not authorized");
		}
		return true;
	}
		
	private boolean validateAuthorizationByExp(String studentId, String dataType, 
			String experienceId, String expType, String schoolYear, Boolean institutional, 
			String action,	HttpServletRequest request) throws Exception {
		String subject = getSubject(getAccoutProfile(request));
		String resourceName = "student-data-attr";
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("student-studentId", studentId);
		attributes.put("student-data-dataType", dataType);
		if(Utils.isNotEmpty(experienceId)) {
			attributes.put("student-data-attr-experienceId", experienceId);
		}
		if(Utils.isNotEmpty(expType)) {
			attributes.put("student-data-attr-expType", expType);
		}
		if(Utils.isNotEmpty(schoolYear)) {
			attributes.put("student-data-attr-schoolYear", schoolYear);
		}
		if(institutional != null) {
			attributes.put("student-data-attr-institutional", institutional.toString());
		}
		AuthorizationDTO authorization = authorizationManager.getAuthorization(subject, action, 
				resourceName, attributes);
		if(!authorizationManager.validateAuthorization(authorization)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid or call not authorized");
		}
		return true;
	}
	
	private boolean validateAuthorizationByResource(String studentId, String dataType, 
			String experienceId, String resourceKey, String resourceValue, String action, 
			HttpServletRequest request) throws Exception {
		String subject = getSubject(getAccoutProfile(request));
		String resourceName = "student-data-attr";
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("student-studentId", studentId);
		attributes.put("student-data-dataType", dataType);
		if(Utils.isNotEmpty(resourceKey)) {
			attributes.put("student-data-attr-" + resourceKey, resourceValue);
		}
		if(Utils.isNotEmpty(experienceId)) {
			attributes.put("student-data-attr-experienceId", experienceId);
		}
		AuthorizationDTO authorization = authorizationManager.getAuthorization(subject, action, 
				resourceName, attributes);
		if(!authorizationManager.validateAuthorization(authorization)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid or call not authorized");
		}
		return true;
	}
	
	@ExceptionHandler({EntityNotFoundException.class, StorageException.class})
	@ResponseStatus(value=HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Map<String,String> handleEntityNotFoundError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}
	
	@ExceptionHandler(UnauthorizedException.class)
	@ResponseStatus(value=HttpStatus.FORBIDDEN)
	@ResponseBody
	public Map<String,String> handleUnauthorizedError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Map<String,String> handleGenericError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}		
}
