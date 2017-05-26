package it.smartcommunitylab.csengine.controller;

import io.swagger.annotations.ApiParam;
import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.exception.EntityNotFoundException;
import it.smartcommunitylab.csengine.exception.StorageException;
import it.smartcommunitylab.csengine.exception.UnauthorizedException;
import it.smartcommunitylab.csengine.model.CV;
import it.smartcommunitylab.csengine.model.Certificate;
import it.smartcommunitylab.csengine.model.CertificationRequest;
import it.smartcommunitylab.csengine.model.Experience;
import it.smartcommunitylab.csengine.model.Registration;
import it.smartcommunitylab.csengine.model.Student;
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
public class StudentController {
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
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Student result = dataManager.getStudent(studentId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getStudentById[%s]: %s", "tenant", result.getId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}", method = RequestMethod.PUT)
	public @ResponseBody Student updateStudentContact(@PathVariable String studentId,
			@RequestBody Student student,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		student.setId(studentId);
		Student result = dataManager.updateStudentContact(student);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateStudentContact[%s]: %s", "tenant", result.getId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/tu/{teachingUnitId}/year/{schoolYear}", method = RequestMethod.GET)
	public @ResponseBody List<Student> getStudentsByTeachingUnit(
			@PathVariable String teachingUnitId,
			@PathVariable String schoolYear,
			@ApiParam Pageable pageable,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<Student> result = dataManager.searchStudentByInstitute(teachingUnitId, schoolYear, pageable);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getStudentsByTeachingUnit[%s]: %s", "tenant", result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/certifier/{certifierId}", method = RequestMethod.GET)
	public @ResponseBody List<Student> getStudentsByCertifier(
			@PathVariable String certifierId,
			@ApiParam Pageable pageable,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<Student> result = dataManager.searchStudentByCertifier(certifierId, pageable);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getStudentsByCertifier[%s]: %s", "tenant", result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/experience/{experienceId}", method = RequestMethod.GET)
	public @ResponseBody List<Student> getStudentsByExperience(
			@PathVariable String experienceId,
			@RequestParam(required=false) String teachingUnitId,
			@RequestParam(required=false) String schoolYear,
			@ApiParam Pageable pageable,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<Student> result = dataManager.searchStudentByExperience(experienceId, teachingUnitId, schoolYear, pageable);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getStudentsByCertifier[%s]: %s", "tenant", result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student", method = RequestMethod.POST)
	public @ResponseBody Student addStudent(@RequestBody Student student,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Student result = dataManager.addStudent(student);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addStudent[%s]: %s", "tenant", result.getId()));
		}
		return result;		
	}
	
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
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<StudentExperience> result = dataManager.searchStudentExperience(studentId, expType, institutional, 
				instituteId, teachingUnitId, schoolYear, registrationId, certifierId, dateFrom, dateTo, text, pageable);
		for(StudentExperience studentExperience : result) {
			documentManager.setSignedUrl(studentExperience.getCertificate());
		}
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
		if (!Utils.validateAPIRequest(request, apiToken)) {
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
		if (!Utils.validateAPIRequest(request, apiToken)) {
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
		if (!Utils.validateAPIRequest(request, apiToken)) {
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
		if (!Utils.validateAPIRequest(request, apiToken)) {
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
		if (!Utils.validateAPIRequest(request, apiToken)) {
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
		if (!Utils.validateAPIRequest(request, apiToken)) {
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
		if (!Utils.validateAPIRequest(request, apiToken)) {
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
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		cv.setStudentId(studentId);
		CV result = dataManager.updateStudentCV(cv);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateStudentCV[%s]: %s - %s", "tenant", studentId, result.getId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/experience/{experienceId}/certificate", method = RequestMethod.POST)
	public @ResponseBody Certificate addCertificateToExperience(
			@PathVariable String experienceId,
			@PathVariable String studentId,
			@RequestBody Certificate certificate,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		certificate.setExperienceId(experienceId);
		certificate.setStudentId(studentId);
		Certificate result = dataManager.addCertificate(certificate);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addCertificateToExperience[%s]: %s", "tenant", result.getStorageId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/experience/{experienceId}/certificate/attributes", 
			method = RequestMethod.PATCH)
	public @ResponseBody Certificate updateCertificateAttributes(
			@PathVariable String experienceId,
			@PathVariable String studentId,
			@RequestBody Map<String, Object> attributes,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Certificate result = dataManager.updateCertificateAttributes(experienceId, studentId, attributes);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateCertificateAttributes[%s]: %s", "tenant", result.getStorageId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/experience/{experienceId}/certificate", 
			method = RequestMethod.DELETE)
	public @ResponseBody Certificate deleteCertificate(
			@PathVariable String experienceId,
			@PathVariable String studentId,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		documentManager.removeFileFromCertificate(experienceId, studentId);
		Certificate result = dataManager.removeCertificate(experienceId, studentId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteCertificate[%s]: %s", "tenant", result.getStorageId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/experience/{experienceId}/certificate/file", 
			method = RequestMethod.POST)
	public @ResponseBody Certificate uploadFile(
			@PathVariable String experienceId,
			@PathVariable String studentId,
			@RequestParam("file") MultipartFile file,
			@RequestParam("filename") String filename,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Certificate result = documentManager.addFileToCertificate(experienceId, studentId, filename, file);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("uploadFile[%s]: %s", "tenant", result.getStorageId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/experience/{experienceId}/certificate/file", 
			method = RequestMethod.DELETE)
	public @ResponseBody Certificate deleteFileFromCertificate(
			@PathVariable String experienceId,
			@PathVariable String studentId,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Certificate result = documentManager.removeFileFromCertificate(experienceId, studentId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteFileFromCertificate[%s]: %s", "tenant", result.getStorageId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/certification/", method = RequestMethod.GET)
	public @ResponseBody List<CertificationRequest> getCertificationRequest(
			@PathVariable String studentId,
			@ApiParam Pageable pageable,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<CertificationRequest> result = dataManager.getCertificationRequestByStudent(studentId, pageable);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getCertificationRequest[%s]: %s - %s", "tenant", studentId, result.size()));
		}
		return result;		
	}
	
	@RequestMapping(value = "/api/student/{studentId}/certification/", method = RequestMethod.POST)
	public @ResponseBody CertificationRequest addCertificationRequest(
			@PathVariable String studentId,
			@RequestBody CertificationRequest certificationRequest,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
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
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		CertificationRequest result = dataManager.removeCertificationRequest(certificationId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteCertificationRequest[%s]: %s", "tenant", result.getId()));
		}
		return result;		
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
