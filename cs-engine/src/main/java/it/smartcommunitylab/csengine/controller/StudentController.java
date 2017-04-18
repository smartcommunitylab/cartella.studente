package it.smartcommunitylab.csengine.controller;

import io.swagger.annotations.ApiParam;
import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.exception.EntityNotFoundException;
import it.smartcommunitylab.csengine.exception.StorageException;
import it.smartcommunitylab.csengine.exception.UnauthorizedException;
import it.smartcommunitylab.csengine.model.CV;
import it.smartcommunitylab.csengine.model.Certificate;
import it.smartcommunitylab.csengine.model.Experience;
import it.smartcommunitylab.csengine.model.Institute;
import it.smartcommunitylab.csengine.model.Registration;
import it.smartcommunitylab.csengine.model.Student;
import it.smartcommunitylab.csengine.model.StudentExperience;
import it.smartcommunitylab.csengine.storage.RepositoryManager;
import it.smartcommunitylab.csengine.ui.StudentExtended;
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

@Controller
public class StudentController {
	private static final transient Logger logger = LoggerFactory.getLogger(StudentController.class);
	
	@Autowired
	@Value("${apiToken}")	
	private String apiToken;
	
	@Autowired
	private RepositoryManager dataManager;

	
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
	
	@RequestMapping(value = "/api/student/institute/{instituteId}/year/{schoolYear}", method = RequestMethod.GET)
	public @ResponseBody List<Student> getStudentsByInstitute(
			@PathVariable String instituteId,
			@PathVariable String schoolYear,
			@ApiParam Pageable pageable,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<Student> result = dataManager.searchStudentByInstitute(instituteId, schoolYear, pageable);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getStudentsByInstitute[%s]: %s", "tenant", result.size()));
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
			@RequestParam(required=false) String instituteId,
			@RequestParam(required=false) String schoolYear,
			@ApiParam Pageable pageable,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<Student> result = dataManager.searchStudentByExperience(experienceId, instituteId, schoolYear, pageable);
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
			@RequestParam Boolean institutional,
			@RequestParam(required=false) String instituteId,
			@RequestParam(required=false) String schoolYear,
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
				instituteId, schoolYear, certifierId, dateFrom, dateTo, text, pageable);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getExperiencesByStudent[%s]: %s", "tenant", result.size()));
		}
		return result;		
	}
	
	@RequestMapping(value = "/api/student/{studentId}/is/extendedexp/", method = RequestMethod.GET)
	public @ResponseBody StudentExtended getExtendedExperiencesByInstitute(
			@PathVariable String studentId,
			@RequestParam String instituteId,
			@RequestParam String schoolYear,
			@RequestParam(required=false) Long dateFrom,
			@RequestParam(required=false) Long dateTo,
			@RequestParam(required=false) String text,
			@ApiParam Pageable pageable,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<StudentExperience> studentExperienceList = dataManager.searchStudentExperience(studentId, null, Boolean.TRUE, 
				instituteId, schoolYear, null, dateFrom, dateTo, text, pageable);
		StudentExtended result = convertStudentExperience(studentExperienceList);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getExtendedExperiencesByInstitute[%s]: %s", "tenant", studentId));
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
	
	@RequestMapping(value = "/api/student/{studentId}/my/experience/{experienceId}/certify", method = RequestMethod.PUT)
	public @ResponseBody Experience certifyMyExperience(
			@PathVariable String studentId,
			@PathVariable String experienceId,
			@RequestParam String certifierId,
			@RequestBody Certificate certificate,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		certificate.setStudentId(studentId);
		certificate.setExperienceId(experienceId);
		Experience result = dataManager.certifyMyExperience(certificate, certifierId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("certifyMyExperience[%s]: %s - %s - %s", "tenant", studentId, experienceId, certifierId));
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
		Map<Institute, List<Registration>> registrationMap = new HashMap<Institute, List<Registration>>();
		List<Registration> registrations = dataManager.getRegistrationByStudent(studentId);
		for(Registration registration : registrations) {
			Institute institute = registration.getInstitute();
			List<Registration> registrationList = registrationMap.get(institute);
			if(registrationList == null) {
				registrationList = new ArrayList<Registration>();
				registrationMap.put(institute, registrationList);
			}
			registrationList.add(registration);
		}
		List<StudentRegistration> result = new ArrayList<StudentRegistration>();
		for(Institute institute : registrationMap.keySet()) {
			StudentRegistration studentRegistration = new StudentRegistration();
			studentRegistration.setInstitute(institute);
			studentRegistration.setRegistrations(registrationMap.get(institute));
			result.add(studentRegistration);
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getStudentRegistration[%s]: %s - %s", "tenant", studentId, result.size()));
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
	
	private StudentExtended convertStudentExperience(List<StudentExperience> studentExperienceList) {
		StudentExtended result = new StudentExtended();
		for(StudentExperience studentExperience : studentExperienceList) {
			String expType = studentExperience.getExperience().getType();
			List<StudentExperience> experienceList = result.getExperienceMap().get(expType);
			if(experienceList == null) {
				experienceList = new ArrayList<StudentExperience>();
				result.getExperienceMap().put(expType, experienceList);
			}
			studentExperience.setStudent(null);
			experienceList.add(studentExperience);
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
