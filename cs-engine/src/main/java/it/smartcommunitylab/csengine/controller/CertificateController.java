package it.smartcommunitylab.csengine.controller;

import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.exception.EntityNotFoundException;
import it.smartcommunitylab.csengine.exception.StorageException;
import it.smartcommunitylab.csengine.exception.UnauthorizedException;
import it.smartcommunitylab.csengine.model.Certificate;
import it.smartcommunitylab.csengine.storage.DocumentManager;
import it.smartcommunitylab.csengine.storage.RepositoryManager;

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
import org.springframework.web.multipart.MultipartFile;

@Controller
public class CertificateController {
	private static final transient Logger logger = LoggerFactory.getLogger(CertificateController.class);
	
	@Autowired
	@Value("${apiToken}")	
	private String apiToken;
	
	@Autowired
	private RepositoryManager dataManager;
	
	@Autowired
	private DocumentManager documentManager;

	@RequestMapping(value = "/api/certificate/experience/{experienceId}/student/{studentId}", method = RequestMethod.GET)
	public @ResponseBody Certificate getCertificateByExperienceAndStudent(
			@PathVariable String experienceId,
			@PathVariable String studentId,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Certificate result = dataManager.getCertificate(experienceId, studentId);
		documentManager.setSignedUrl(result);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getCertificateByExperienceAndStudent[%s]: %s", "tenant", result.getStorageId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/certificate/experience/{experienceId}/student/{studentId}", method = RequestMethod.POST)
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
	
	@RequestMapping(value = "/api/certificate/experience/{experienceId}/student/{studentId}/attributes", 
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
	
	@RequestMapping(value = "/api/certificate/experience/{experienceId}/student/{studentId}", method = RequestMethod.DELETE)
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
	
	@RequestMapping(value = "/api/certificate/experience/{experienceId}/student/{studentId}/file", method = RequestMethod.POST)
	public @ResponseBody Certificate uploadFile(
			@PathVariable String experienceId,
			@PathVariable String studentId,
			@RequestParam("file") MultipartFile file,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Certificate result = documentManager.addFileToCertificate(experienceId, studentId, file);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("uploadFile[%s]: %s", "tenant", result.getStorageId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/certificate/experience/{experienceId}/student/{studentId}/file", method = RequestMethod.DELETE)
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
