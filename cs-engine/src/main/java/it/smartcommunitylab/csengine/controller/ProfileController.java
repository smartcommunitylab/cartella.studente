package it.smartcommunitylab.csengine.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import it.smartcommunitylab.aac.model.AccountProfile;
import it.smartcommunitylab.csengine.common.ErrorLabelManager;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.exception.EntityNotFoundException;
import it.smartcommunitylab.csengine.exception.StorageException;
import it.smartcommunitylab.csengine.exception.UnauthorizedException;
import it.smartcommunitylab.csengine.model.Consent;
import it.smartcommunitylab.csengine.model.PersonInCharge;
import it.smartcommunitylab.csengine.model.Student;
import it.smartcommunitylab.csengine.storage.RepositoryManager;
import it.smartcommunitylab.csengine.ui.Profile;

@Controller
public class ProfileController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(ProfileController.class);
	
	@Autowired
	@Value("${profile.serverUrl}")	
	private String profileServerUrl;
	
	@Autowired
	@Value("${profile.account}")	
	private String profileAccount;
	
	@Autowired
	@Value("${profile.attribute}")	
	private String profileAttribute;
	
	@Autowired
	private RepositoryManager dataManager;
	
	@Autowired
	private ErrorLabelManager errorLabelManager;
	
	@RequestMapping(value = "/api/profile", method = RequestMethod.GET)
	public @ResponseBody Profile getProfileByToken(HttpServletRequest request) throws Exception {
		AccountProfile accoutProfile = getAccoutProfile(request);
		String cf = getCF(accoutProfile);  
		if(Utils.isEmpty(cf)) {
			if (accoutProfile != null) {
				throw new UnauthorizedException(String.format(errorLabelManager.get("user.notfound"), accoutProfile.getName(), accoutProfile.getSurname()));	
			} else {
				throw new UnauthorizedException(errorLabelManager.get("api.access.error"));
			}
			
		}
		Profile result = new Profile();
		result.setSubject(cf);
		Student student = dataManager.getStudentByCF(cf);
		if(student != null) {
			result.setStudentId(student.getId());
		}
		PersonInCharge personInCharge = dataManager.getPersonInChargeByCF(cf);
		if(personInCharge != null) {
			result.setPersonInChargeId(personInCharge.getId());
			result.setStudentIds(personInCharge.getStudentIds());
		}
		if((student == null) && (personInCharge == null)) {
			throw new UnauthorizedException(String.format(errorLabelManager.get("user.notfound"), accoutProfile.getName(), accoutProfile.getSurname()));
		}
		Consent consent = dataManager.getConsentByStudent(student.getId());
		if(consent != null) {
			result.setAuthorized(consent.getAuthorized());
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getProfileByToken[%s]: %s", "tenant", cf));
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
