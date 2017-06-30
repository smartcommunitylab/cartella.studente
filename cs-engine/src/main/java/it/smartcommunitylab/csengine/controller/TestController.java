package it.smartcommunitylab.csengine.controller;

import java.util.Map;

import it.smartcommunitylab.aac.authorization.beans.AuthorizationDTO;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.exception.EntityNotFoundException;
import it.smartcommunitylab.csengine.exception.StorageException;
import it.smartcommunitylab.csengine.exception.UnauthorizedException;
import it.smartcommunitylab.csengine.security.AuthorizationManager;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class TestController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(TestController.class);
	@Autowired
	AuthorizationManager authorizationManager;

	@RequestMapping(value = "/test/auth/schema/upload", method = RequestMethod.POST)
	public @ResponseBody void uploadAuthSchema(
			@RequestBody String json,
			HttpServletRequest request) throws Exception {
		authorizationManager.loadAuthSchema(json);
	}
	
	@RequestMapping(value = "/test/auth", method = RequestMethod.POST)
	public @ResponseBody AuthorizationDTO addAuthorization(
			@RequestBody AuthorizationDTO auth,
			HttpServletRequest request) throws Exception {
		return authorizationManager.insertAuthorization(auth);
	}
	
	@RequestMapping(value = "/test/auth/{authId}", method = RequestMethod.DELETE)
	public @ResponseBody void deleteAuthorization(
			@PathVariable String authId,
			HttpServletRequest request) throws Exception {
		authorizationManager.deleteAuthorization(authId);
	}
	
	@RequestMapping(value = "/test/auth/validate", method = RequestMethod.POST)
	public @ResponseBody String validateAuth(
			@RequestBody AuthorizationDTO auth,
			HttpServletRequest request) throws Exception {
		return Boolean.toString(authorizationManager.validateAuthorization(auth));
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
