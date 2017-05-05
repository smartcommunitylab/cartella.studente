package it.smartcommunitylab.csengine.controller;

import io.swagger.annotations.ApiParam;
import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.exception.EntityNotFoundException;
import it.smartcommunitylab.csengine.exception.StorageException;
import it.smartcommunitylab.csengine.exception.UnauthorizedException;
import it.smartcommunitylab.csengine.model.Certificate;
import it.smartcommunitylab.csengine.model.Course;
import it.smartcommunitylab.csengine.model.Experience;
import it.smartcommunitylab.csengine.model.Registration;
import it.smartcommunitylab.csengine.model.Student;
import it.smartcommunitylab.csengine.model.StudentExperience;
import it.smartcommunitylab.csengine.model.TeachingUnit;
import it.smartcommunitylab.csengine.storage.DocumentManager;
import it.smartcommunitylab.csengine.storage.RepositoryManager;
import it.smartcommunitylab.csengine.ui.ExperienceExtended;

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
public class TeachingUnitController {
	private static final transient Logger logger = LoggerFactory.getLogger(TeachingUnitController.class);
	
	@Autowired
	@Value("${apiToken}")	
	private String apiToken;
	
	@Autowired
	private RepositoryManager dataManager;
	
	@Autowired
	private DocumentManager documentManager;
	
	@RequestMapping(value = "/api/tu", method = RequestMethod.GET)
	public @ResponseBody List<TeachingUnit> getTeachingUnits(HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<TeachingUnit> result = dataManager.getTeachingUnit();
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getTeachingUnits[%s]: %s", "tenant", result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/tu/{teachingUnitId}/year/{schoolYear}/course", method = RequestMethod.GET)
	public @ResponseBody List<Course> getCourseByTeachingUnit(
			@PathVariable String teachingUnitId,
			@PathVariable String schoolYear,			
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<Course> result = dataManager.getCourseByTeachingUnit(teachingUnitId, schoolYear);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getCourseByInstitute[%s]: %s - %s - %s", "tenant", 
					teachingUnitId, schoolYear, result.size()));
		}
		return result;
	}
		
	@RequestMapping(value = "/api/tu/course/{courseId}/classroom", method = RequestMethod.GET)
	public @ResponseBody List<String> getClassroomByTeachingUnit(
			@PathVariable String courseId,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<String> result = new ArrayList<String>(); 
		List<Registration> registrations = dataManager.getRegistrationByCourse(courseId);
		for(Registration registration : registrations) {
			String classroom = registration.getClassroom();
			if(!result.contains(classroom)) {
				result.add(classroom);
			}
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getClassroomByTeachingUnit[%s]: %s - %s", "tenant", 
					courseId, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/tu/course/{courseId}/student", method = RequestMethod.GET)
	public @ResponseBody List<Student> getStudentByClassroom(
			@PathVariable String courseId,
			@RequestParam String classroom,			
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<Student> result = new ArrayList<Student>();
		List<Registration> registrations = dataManager.getRegistrationByCourse(courseId);
		for(Registration registration : registrations) {
			String regClassroom = registration.getClassroom();
			if(regClassroom.equals(classroom)) {
				Student student = registration.getStudent();
				if(!result.contains(student)) {
					result.add(student);
				}
			}
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getStudentByClassroom[%s]: %s - %s - %s", "tenant", 
					courseId, classroom, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/tu/{teachingUnitId}/year/{schoolYear}/studentexperience/{expType}", 
			method = RequestMethod.GET)
	public @ResponseBody List<StudentExperience> getStudentExperienceByInstitute(
			@PathVariable String teachingUnitId,
			@PathVariable String schoolYear,
			@PathVariable String expType,
			@RequestParam(required=false) Long dateFrom,
			@RequestParam(required=false) Long dateTo,
			@RequestParam(required=false) String text,
			@ApiParam Pageable pageable,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<StudentExperience> result = dataManager.searchStudentExperience(null, expType, true, 
				teachingUnitId, schoolYear, null, dateFrom, dateTo, text, pageable);
		for(StudentExperience studentExperience : result) {
			documentManager.setSignedUrl(studentExperience.getCertificate());
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getStudentExperienceByInstitute[%s]: %s", "tenant", result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/tu/{teachingUnitId}/studentexperience/{experienceId}", 
			method = RequestMethod.GET)
	public @ResponseBody List<StudentExperience> getStudentExperienceById(
			@PathVariable String teachingUnitId,
			@PathVariable String experienceId,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<StudentExperience> result = dataManager.searchStudentExperienceById(null, teachingUnitId, experienceId, true);
		for(StudentExperience studentExperience : result) {
			documentManager.setSignedUrl(studentExperience.getCertificate());
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getStudentExperienceById[%s]: %s", "tenant", result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/tu/{teachingUnitId}/year/{schoolYear}/experience/{expType}", 
			method = RequestMethod.GET)
	public @ResponseBody List<Experience> getExperienceByTeachingUnit(
			@PathVariable String teachingUnitId,
			@PathVariable String schoolYear,
			@PathVariable String expType,
			@RequestParam(required=false) Long dateFrom,
			@RequestParam(required=false) Long dateTo,
			@RequestParam(required=false) String text,
			@ApiParam Pageable pageable,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<Experience> result = dataManager.searchExperience(expType, true, 
				teachingUnitId, schoolYear, null, dateFrom, dateTo, text, pageable);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getExperienceByTeachingUnit[%s]: %s", "tenant", result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/tu/{teachingUnitId}/year/{schoolYear}/registration", method = RequestMethod.GET)
	public @ResponseBody List<Registration> getRegistrationByTeachingUnit(
			@PathVariable String teachingUnitId,
			@PathVariable String schoolYear,
			@RequestParam(required=false) Long dateFrom,
			@RequestParam(required=false) Long dateTo,
			@ApiParam Pageable pageable,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<Registration> result = dataManager.searchRegistration(null, teachingUnitId, schoolYear, 
				dateFrom, dateTo, pageable);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getRegistrationByTeachingUnit[%s]: %s", "tenant", result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/tu/{teachingUnitId}/year/{schoolYear}/is/experience", method = RequestMethod.POST)
	public @ResponseBody Experience addIsExperience(
			@PathVariable String teachingUnitId,
			@PathVariable String schoolYear,
			@RequestParam(name="studentIds") List<String> studentIds,
			@RequestBody Experience experience,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		experience.getAttributes().put(Const.ATTR_INSTITUTIONAL, Boolean.TRUE);
		experience.getAttributes().put(Const.ATTR_TUID, teachingUnitId);
		experience.getAttributes().put(Const.ATTR_SCHOOLYEAR, schoolYear);
		Experience result = dataManager.addIsExperience(studentIds, experience);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addIsExperience[%s]: %s - %s", "tenant", studentIds.toString(), result.getId()));
		}
		return result;
	}	
	
	@RequestMapping(value = "/api/tu/{teachingUnitId}/is/experience/{experienceId}", 
			method = RequestMethod.PUT)
	public @ResponseBody Experience updateIsExperience(
			@PathVariable String teachingUnitId,
			@PathVariable String experienceId,
			@RequestParam(name="studentIds") List<String> studentIds,
			@RequestBody Experience experience,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		experience.setId(experienceId);
		experience.getAttributes().put(Const.ATTR_INSTITUTIONAL, Boolean.TRUE);
		experience.getAttributes().put(Const.ATTR_TUID, teachingUnitId);
		Experience result = dataManager.updateIsExperience(studentIds, experience);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateIsExperience[%s]: %s - %s", "tenant", studentIds.toString(), result.getId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/tu/{teachingUnitId}/is/experience/{experienceId}/certify", 
			method = RequestMethod.PUT)
	public @ResponseBody void certifyIsExperience(
			@PathVariable String teachingUnitId,
			@PathVariable String experienceId,
			@RequestBody List<Certificate> certificates,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		dataManager.certifyIsExperience(experienceId, certificates);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("certifyIsExperience[%s]: %s - %s", "tenant", teachingUnitId, experienceId));
		}
	}

	@RequestMapping(value = "/api/tu/{teachingUnitId}/year/{schoolYear}/extendedexp/{expType}", 
			method = RequestMethod.GET)
	public @ResponseBody List<ExperienceExtended> getExperienceExtendedByTeachingUnit(
			@PathVariable String teachingUnitId,
			@PathVariable String schoolYear,
			@PathVariable String expType,
			@RequestParam(required=false) Long dateFrom,
			@RequestParam(required=false) Long dateTo,
			@RequestParam(required=false) String text,
			@ApiParam Pageable pageable,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<ExperienceExtended> result = new ArrayList<ExperienceExtended>();
		List<Registration> registrations = dataManager.getRegistrationByTeachingUnit(teachingUnitId, schoolYear);
		//TODO get list of studentId and filter StudentExperience by student ids
		Map<String, ExperienceExtended> extendedExpMap = new HashMap<String, ExperienceExtended>();
		List<StudentExperience> studentExperiences = dataManager.searchStudentExperience(null, expType, Boolean.TRUE, 
				teachingUnitId, schoolYear, null, dateFrom, dateTo, text, pageable);
		for(StudentExperience studentExperience : studentExperiences) {
			documentManager.setSignedUrl(studentExperience.getCertificate());
			ExperienceExtended experienceExtended = extendedExpMap.get(studentExperience.getExperienceId());
			if(experienceExtended == null) {
				experienceExtended = new ExperienceExtended(studentExperience.getExperience());
				extendedExpMap.put(studentExperience.getExperienceId(), experienceExtended);
			}
			studentExperience.setExperience(null);
			experienceExtended.getStudentExperiences().add(studentExperience);
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getExperienceExtendedByTeachingUnit[%s]: %s", "tenant", result.size()));
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
