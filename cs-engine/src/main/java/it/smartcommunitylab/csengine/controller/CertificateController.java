package it.smartcommunitylab.csengine.controller;

import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.exception.EntityNotFoundException;
import it.smartcommunitylab.csengine.exception.UnauthorizedException;
import it.smartcommunitylab.csengine.model.Certificate;
import it.smartcommunitylab.csengine.storage.DocumentManager;
import it.smartcommunitylab.csengine.storage.RepositoryManager;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

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
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getCertificateByExperienceAndStudent[%s]: %s", "tenant", result.getId()));
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
			logger.info(String.format("addCertificateToExperience[%s]: %s", "tenant", result.getId()));
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
			logger.info(String.format("updateCertificateAttributes[%s]: %s", "tenant", result.getId()));
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
		Certificate result = dataManager.removeCertificate(experienceId, studentId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteCertificate[%s]: %s", "tenant", result.getId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/certificate/{certificateId}/file", method = RequestMethod.POST)
	public @ResponseBody Certificate uploadFile(
			@PathVariable String certificateId,
			MultipartHttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Map<String, MultipartFile> fileMap = request.getFileMap();
		Certificate result = documentManager.addFileToCertificate(certificateId, fileMap);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("uploadFile[%s]: %s", "tenant", result.getId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/certificate/{certificateId}", method = RequestMethod.DELETE)
	public @ResponseBody Certificate deleteFileFromCertificate(
			@PathVariable String certificateId,
			HttpServletRequest request) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Certificate result = documentManager.removeFrileFromCertificate(certificateId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteFileFromCertificate[%s]: %s", "tenant", result.getId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/certificate/{certificateId}/file", method = RequestMethod.GET)
	public void downloadFile(
			@PathVariable String certificateId,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (!Utils.validateAPIRequest(request, apiToken)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
		response.getOutputStream().write(outputBuffer.toByteArray());
		response.getOutputStream().flush();
		outputBuffer.close();
		if(logger.isInfoEnabled()) {
			logger.info(String.format("downloadFile[%s]: %s", "tenant", certificateId));
		}
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
