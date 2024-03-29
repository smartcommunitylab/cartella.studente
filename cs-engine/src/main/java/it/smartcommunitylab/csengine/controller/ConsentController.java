package it.smartcommunitylab.csengine.controller;

import it.smartcommunitylab.aac.authorization.beans.AccountAttributeDTO;
import it.smartcommunitylab.aac.authorization.beans.RequestedAuthorizationDTO;
import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.ErrorLabelManager;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.exception.EntityNotFoundException;
import it.smartcommunitylab.csengine.exception.StorageException;
import it.smartcommunitylab.csengine.exception.UnauthorizedException;
import it.smartcommunitylab.csengine.model.Consent;
import it.smartcommunitylab.csengine.storage.RepositoryManager;

import java.util.HashMap;
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
public class ConsentController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(ConsentController.class);
	
	@Autowired
	@Value("${apiToken}")	
	private String apiToken;
	
	@Autowired
	private RepositoryManager dataManager;
	
	@Autowired
	private ErrorLabelManager errorLabelManager;
	
	@RequestMapping(value = "/api/consent/student/{studentId}", method = RequestMethod.POST)
	public @ResponseBody Consent addConsent(
			@PathVariable String studentId,
			HttpServletRequest request) throws Exception {
		if (!validateStudentAuthorization(studentId, Const.AUTH_ACTION_ADD, request)) {
			throw new UnauthorizedException(errorLabelManager.get("api.access.error"));
		}
		Consent consent = new Consent();
		consent.setStudentId(studentId);
		consent.setSubject(getCF(getAccoutProfile(request)));
		consent.setAuthorized(Boolean.TRUE);
		Consent result = dataManager.addConsent(consent);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addConsent[%s]: %s", "tenant", result.getId()));
		}
		return result;		
	}
	
	@RequestMapping(value = "/api/consent/student/{studentId}/remove", method = RequestMethod.PUT)
	public @ResponseBody Consent removeAuthorization(
			@PathVariable String studentId,
			HttpServletRequest request) throws Exception {
		if (!validateStudentAuthorization(studentId, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException(errorLabelManager.get("api.access.error"));
		}
		Consent result = dataManager.removeAuthorization(studentId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("removeAuthorization[%s]: %s", "tenant", result.getId()));
		}
		return result;		
	}
	
	@RequestMapping(value = "/api/consent/student/{studentId}/add", method = RequestMethod.PUT)
	public @ResponseBody Consent addAuthorization(
			@PathVariable String studentId,
			HttpServletRequest request) throws Exception {
		if (!validateStudentAuthorization(studentId, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException(errorLabelManager.get("api.access.error"));
		}
		Consent result = dataManager.addAuthorization(studentId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("removeAuthorization[%s]: %s", "tenant", result.getId()));
		}
		return result;		
	}
	
	@RequestMapping(value = "/api/consent/student/{studentId}", method = RequestMethod.GET)
	public @ResponseBody Consent getConsentByStudent(
			@PathVariable String studentId,
			HttpServletRequest request) throws Exception {
		if (!validateStudentAuthorization(studentId, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException(errorLabelManager.get("api.access.error"));
		}
		Consent result = dataManager.getConsentByStudent(studentId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getConsentByStudent[%s]: %s", "tenant", studentId));
		}
		return result;
	}
	
	private boolean validateStudentAuthorization(String studentId, String action,
			HttpServletRequest request) throws Exception {
		String resourceName = "student";
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("student-studentId", studentId);
		AccountAttributeDTO account = getAccountByCF(request);
		RequestedAuthorizationDTO authorization = authorizationManager.getReqAuthorization(account, action, 
				resourceName, attributes);
		if(!authorizationManager.validateAuthorization(authorization)) {
			throw new UnauthorizedException(errorLabelManager.get("api.access.error"));
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
