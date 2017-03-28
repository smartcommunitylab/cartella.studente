package it.smartcommunitylab.csengine.controller;

import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.exception.EntityNotFoundException;
import it.smartcommunitylab.csengine.exception.UnauthorizedException;
import it.smartcommunitylab.csengine.model.Experience;
import it.smartcommunitylab.csengine.model.Institute;
import it.smartcommunitylab.csengine.storage.RepositoryManager;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class InstituteController {
	private static final transient Logger logger = LoggerFactory.getLogger(InstituteController.class);
	
	@Autowired
	@Value("${apiToken}")	
	private String apiToken;
	
	@Autowired
	private RepositoryManager storage;
	
	@RequestMapping(value = "/api/institute/", method = RequestMethod.GET)
	public @ResponseBody List<Institute> getInstitutes(HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<Institute> result = new ArrayList<Institute>();
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getInstitutes[%s]: %s", "tenant", result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/institute/{instituteId}/year/{schoolYear}", method = RequestMethod.GET)
	public @ResponseBody List<Experience> getCoursesByInstitute(@PathVariable String instituteId,
			@PathVariable String schoolYear, HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<Experience> result = new ArrayList<Experience>();
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getCoursesByInstitute[%s]: %s", "tenant", result.size()));
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
