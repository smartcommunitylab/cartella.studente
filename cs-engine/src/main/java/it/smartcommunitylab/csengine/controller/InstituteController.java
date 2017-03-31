package it.smartcommunitylab.csengine.controller;

import io.swagger.annotations.ApiParam;
import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.exception.EntityNotFoundException;
import it.smartcommunitylab.csengine.exception.UnauthorizedException;
import it.smartcommunitylab.csengine.model.Experience;
import it.smartcommunitylab.csengine.model.Institute;
import it.smartcommunitylab.csengine.storage.RepositoryManager;

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
public class InstituteController {
	private static final transient Logger logger = LoggerFactory.getLogger(InstituteController.class);
	
	@Autowired
	@Value("${apiToken}")	
	private String apiToken;
	
	@Autowired
	private RepositoryManager dataManager;
	
	@RequestMapping(value = "/api/institute/", method = RequestMethod.GET)
	public @ResponseBody List<Institute> getInstitutes(HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<Institute> result = dataManager.getInstitute();
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getInstitutes[%s]: %s", "tenant", result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/institute/{instituteId}/year/{schoolYear}/experience/{type}", method = RequestMethod.GET)
	public @ResponseBody List<Experience> getExperienceByInstitute(
			@PathVariable String instituteId,
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
		List<Experience> result = dataManager.searchExperience(null, expType, true, 
				instituteId, schoolYear, null, dateFrom, dateTo, text, pageable);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getExperienceByInstitute[%s]: %s", "tenant", result.size()));
		}
		return result;
	}
		
	@RequestMapping(value = "/api/institute/{instituteId}/is/experience", method = RequestMethod.POST)
	public @ResponseBody Experience addIsExperience(
			@PathVariable String instituteId,
			@RequestParam(name="studentIds") List<String> studentIds,
			@RequestBody Experience experience,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		experience.getAttributes().put(Const.ATTR_INSTITUTIONAL, Boolean.TRUE);
		if(Utils.isNotEmpty(instituteId)) {
			experience.getAttributes().put(Const.ATTR_INSTITUTEID, instituteId);
		}
		Experience result = dataManager.addIsExperience(studentIds, experience);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addIsExperience[%s]: %s - %s", "tenant", studentIds.toString(), result.getId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/institute/{instituteId}/is/experience/{experienceId}", method = RequestMethod.PUT)
	public @ResponseBody Experience updateIsExperience(
			@PathVariable String instituteId,
			@PathVariable String experienceId,
			@RequestParam(name="studentIds") List<String> studentIds,
			@RequestBody Experience experience,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		experience.setId(experienceId);
		experience.getAttributes().put(Const.ATTR_INSTITUTIONAL, Boolean.TRUE);
		Experience result = dataManager.updateIsExperience(studentIds, experience);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateIsExperience[%s]: %s - %s", "tenant", studentIds.toString(), result.getId()));
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
