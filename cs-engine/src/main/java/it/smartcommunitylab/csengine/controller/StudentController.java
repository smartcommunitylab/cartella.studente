package it.smartcommunitylab.csengine.controller;

import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.exception.EntityNotFoundException;
import it.smartcommunitylab.csengine.exception.UnauthorizedException;
import it.smartcommunitylab.csengine.model.Experience;
import it.smartcommunitylab.csengine.model.Student;
import it.smartcommunitylab.csengine.storage.RepositoryManager;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	private RepositoryManager storage;

	@RequestMapping(value = "/api/student/institute/{instituteId}/year/{schoolYear}", method = RequestMethod.GET)
	public @ResponseBody List<Student> getStudentsByInstitute(
			@PathVariable String instituteId,
			@PathVariable String schoolYear,
			@RequestParam(required=false) Integer page, 
			@RequestParam(required=false) Integer limit,
			@RequestParam(required=false) String orderBy,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		if(page == null) {
			page = 1;
		}
		if(limit == null) {
			limit = 10;
		}
		List<Student> result = storage.searchStudent(instituteId, schoolYear, page, limit, orderBy);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getStudentsByInstitute[%s]: %s", "tenant", result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/experience/{type}", method = RequestMethod.GET)
	public @ResponseBody List<Experience> getExperiencesByStudent(
			@PathVariable String studentId,
			@PathVariable String expType,
			@RequestParam Boolean institutional,
			@RequestParam(required=false) String instituteId,
			@RequestParam(required=false) String schoolYear,
			@RequestParam(required=false) Integer page, 
			@RequestParam(required=false) Integer limit,
			@RequestParam(required=false) String orderBy,
			@RequestParam(required=false) String dateFrom,
			@RequestParam(required=false) String dateTo,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		if(page == null) {
			page = 1;
		}
		if(limit == null) {
			limit = 10;
		}
		List<Experience> result = storage.searchExperience(studentId, expType, institutional, instituteId, schoolYear,
				dateFrom, dateTo, page, limit, orderBy);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getExperiencesByStudent[%s]: %s", "tenant", result.size()));
		}
		return result;		
	}
	
	@RequestMapping(value = "/api/student/{studentId}/my/experience", method = RequestMethod.POST)
	public @ResponseBody Experience addMyExperience(
			@PathVariable String studentId,
			@RequestBody Experience experience,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		experience.setStudentId(studentId);
		experience.getAttributes().put(Const.ATTR_INSTITUTIONAL, Boolean.FALSE);
		Experience result = storage.saveExperience(experience);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addMyExperience[%s]: %s - %s", "tenant", studentId, result.getId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/is/experience", method = RequestMethod.POST)
	public @ResponseBody Experience addIsExperience(
			@PathVariable String studentId,
			@RequestBody Experience experience,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		experience.setStudentId(studentId);
		experience.getAttributes().put(Const.ATTR_INSTITUTIONAL, Boolean.TRUE);
		Experience result = storage.saveExperience(experience);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addIsExperience[%s]: %s - %s", "tenant", studentId, result.getId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/my/experience/{experienceId}", method = RequestMethod.PUT)
	public @ResponseBody Experience updateMyExperience(
			@PathVariable String studentId,
			@PathVariable String experienceId,
			@RequestBody Experience experience,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		experience.setId(experienceId);
		experience.setStudentId(studentId);
		experience.getAttributes().put(Const.ATTR_INSTITUTIONAL, Boolean.FALSE);
		Experience result = storage.saveExperience(experience);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateMyExperience[%s]: %s - %s", "tenant", studentId, result.getId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/student/{studentId}/is/experience/{experienceId}", method = RequestMethod.PUT)
	public @ResponseBody Experience updateIsExperience(
			@PathVariable String studentId,
			@PathVariable String experienceId,
			@RequestBody Experience experience,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		experience.setId(experienceId);
		experience.setStudentId(studentId);
		experience.getAttributes().put(Const.ATTR_INSTITUTIONAL, Boolean.FALSE);
		Experience result = storage.saveExperience(experience);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateIsExperience[%s]: %s - %s", "tenant", studentId, result.getId()));
		}
		return result;
	}
	
	@ExceptionHandler(EntityNotFoundException.class)
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
