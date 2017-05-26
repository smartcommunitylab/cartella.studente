package it.smartcommunitylab.csengine.controller;

import io.swagger.annotations.ApiParam;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.exception.EntityNotFoundException;
import it.smartcommunitylab.csengine.exception.StorageException;
import it.smartcommunitylab.csengine.exception.UnauthorizedException;
import it.smartcommunitylab.csengine.model.Certificate;
import it.smartcommunitylab.csengine.model.CertificationRequest;
import it.smartcommunitylab.csengine.model.Experience;
import it.smartcommunitylab.csengine.storage.DocumentManager;
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
import org.springframework.web.multipart.MultipartFile;

@Controller
public class CertifierController {
	private static final transient Logger logger = LoggerFactory.getLogger(CertifierController.class);
	
	@Autowired
	@Value("${apiToken}")	
	private String apiToken;
	
	@Autowired
	private RepositoryManager dataManager;
	
	@Autowired
	private DocumentManager documentManager;

	@RequestMapping(value = "/api/certifier/{certifierId}/certification/", method = RequestMethod.GET)
	public @ResponseBody List<CertificationRequest> getCertificationRequest(
			@PathVariable String certifierId,
			@ApiParam Pageable pageable,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<CertificationRequest> result = dataManager.getCertificationRequestByCertifier(certifierId, pageable);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getCertificationRequest[%s]: %s - %s", "tenant", certifierId, result.size()));
		}
		return result;		
	}
	
	@RequestMapping(value = "/api/certifier/{certifierId}/certification/", method = RequestMethod.POST)
	public @ResponseBody CertificationRequest addCertificationRequest(
			@PathVariable String certifierId,
			@RequestBody CertificationRequest certificationRequest,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		certificationRequest.setCertifierId(certifierId);
		CertificationRequest result = dataManager.addCertificationRequest(certificationRequest);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addCertificationRequest[%s]: %s - %s", "tenant", certifierId, result.getId()));
		}
		return result;		
	}
	
	@RequestMapping(value = "/api/certifier/{certifierId}/certification/{certificationId}", method = RequestMethod.DELETE)
	public @ResponseBody CertificationRequest deleteCertificationRequest(
			@PathVariable String certifierId,
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
	
	@RequestMapping(value = "/api/certifier/{certifierId}/student/{studentId}/experience/{experienceId}/certify", 
			method = RequestMethod.PUT)
	public @ResponseBody Experience certifyExperience(
			@PathVariable String studentId,
			@PathVariable String experienceId,
			@RequestParam String certifierId,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Experience result = dataManager.certifyMyExperience(experienceId, studentId, certifierId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("certifyExperience[%s]: %s - %s - %s", "tenant", studentId, experienceId, certifierId));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/certifier/{certifierId}/certificate/experience/{experienceId}/student/{studentId}/file", 
			method = RequestMethod.POST)
	public @ResponseBody Certificate uploadFile(
			@PathVariable String certifierId,
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
	
	@RequestMapping(value = "/api/certifier/{certifierId}/certificate/experience/{experienceId}/student/{studentId}/file", 
			method = RequestMethod.DELETE)
	public @ResponseBody Certificate deleteFileFromCertificate(
			@PathVariable String certifierId,
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
