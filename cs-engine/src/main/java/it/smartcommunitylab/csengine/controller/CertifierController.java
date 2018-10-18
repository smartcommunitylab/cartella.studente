package it.smartcommunitylab.csengine.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
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

import io.swagger.annotations.ApiParam;
import it.smartcommunitylab.aac.authorization.beans.AccountAttributeDTO;
import it.smartcommunitylab.aac.authorization.beans.RequestedAuthorizationDTO;
import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.exception.EntityNotFoundException;
import it.smartcommunitylab.csengine.exception.StorageException;
import it.smartcommunitylab.csengine.exception.UnauthorizedException;
import it.smartcommunitylab.csengine.model.CertificationRequest;
import it.smartcommunitylab.csengine.model.Certifier;
import it.smartcommunitylab.csengine.model.Document;
import it.smartcommunitylab.csengine.model.Student;
import it.smartcommunitylab.csengine.model.StudentExperience;
import it.smartcommunitylab.csengine.storage.LocalDocumentManager;
import it.smartcommunitylab.csengine.storage.RepositoryManager;

@Controller
public class CertifierController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(CertifierController.class);

	@Autowired
	@Value("${apiToken}")
	private String apiToken;

	@Autowired
	private RepositoryManager dataManager;

	@Autowired
	private LocalDocumentManager documentManager;

	@RequestMapping(value = "/extsource/aziende", method = RequestMethod.GET)
	public @ResponseBody Page<Certifier> getAllAziende(@ApiParam Pageable pageable, @RequestParam(required = false) Long timestamp) {

		Page<Certifier> result;
		if (timestamp != null) {
			result = dataManager.fetchCertifierAfterTimestamp(pageable, timestamp);
		} else {
			result = dataManager.fetchCertifier(pageable);
		}
		if (logger.isInfoEnabled()) {
			logger.info(String.format("getAllAziende: %s", result.getNumberOfElements()));
		}
		return result;
	}

	@RequestMapping(value = "/api/certifier/{certifierId}/student", method = RequestMethod.GET)
	public @ResponseBody List<Student> getStudentsByCertifier(@PathVariable String certifierId,
			@ApiParam Pageable pageable, HttpServletRequest request) throws Exception {
		if (!validateCertifierAuthorization(certifierId, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<Student> result = dataManager.searchStudentByCertifier(certifierId, pageable);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("getStudentsByCertifier[%s]: %s", "tenant", result.size()));
		}
		return result;
	}

	@RequestMapping(value = "/api/certifier/{certifierId}/certification", method = RequestMethod.GET)
	public @ResponseBody List<CertificationRequest> getCertificationRequest(@PathVariable String certifierId,
			@ApiParam Pageable pageable, HttpServletRequest request) throws Exception {
		if (!validateCertifierAuthorization(certifierId, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<CertificationRequest> result = dataManager.getCertificationRequestByCertifier(certifierId, pageable);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("getCertificationRequest[%s]: %s - %s", "tenant", certifierId, result.size()));
		}
		return result;
	}

	@RequestMapping(value = "/api/certifier/{certifierId}/certification", method = RequestMethod.POST)
	public @ResponseBody CertificationRequest addCertificationRequest(@PathVariable String certifierId,
			@RequestBody CertificationRequest certificationRequest, HttpServletRequest request) throws Exception {
		if (!validateCertifierAuthorization(certifierId, Const.AUTH_ACTION_ADD, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		certificationRequest.setCertifierId(certifierId);
		CertificationRequest result = dataManager.addCertificationRequest(certificationRequest);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("addCertificationRequest[%s]: %s - %s", "tenant", certifierId, result.getId()));
		}
		return result;
	}

	@RequestMapping(value = "/api/certifier/{certifierId}/certification/{certificationId}", method = RequestMethod.DELETE)
	public @ResponseBody CertificationRequest deleteCertificationRequest(@PathVariable String certifierId,
			@PathVariable String certificationId, HttpServletRequest request) throws Exception {
		if (!validateCertifierAuthorization(certifierId, Const.AUTH_ACTION_DELETE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		CertificationRequest result = dataManager.removeCertificationRequest(certificationId);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("deleteCertificationRequest[%s]: %s", "tenant", result.getId()));
		}
		return result;
	}

	@RequestMapping(value = "/api/certifier/{certifierId}/student/{studentId}/experience/{experienceId}/certify", method = RequestMethod.PUT)
	public @ResponseBody StudentExperience certifyExperience(@PathVariable String studentId,
			@PathVariable String experienceId, @RequestParam String certifierId, HttpServletRequest request)
			throws Exception {
		if (!validateCertifierAuthorization(certifierId, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		StudentExperience result = dataManager.certifyMyExperience(experienceId, studentId, certifierId);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("certifyExperience[%s]: %s - %s - %s", "tenant", studentId, experienceId,
					certifierId));
		}
		return result;
	}

	@RequestMapping(value = "/api/certifier/{certifierId}/experience/{experienceId}/student/{studentId}/document/{storageId}/file", method = RequestMethod.POST)
	public @ResponseBody Document uploadFile(@PathVariable String certifierId, @PathVariable String experienceId,
			@PathVariable String studentId, @PathVariable String storageId, @RequestParam("file") MultipartFile file,
			@RequestParam("filename") String filename, HttpServletRequest request) throws Exception {
		if (!validateCertifierAuthorization(certifierId, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Document result = documentManager.addFileToDocument(experienceId, studentId, storageId, filename, file);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("uploadFile[%s]: %s", "tenant", result.getStorageId()));
		}
		return result;
	}

	@RequestMapping(value = "/api/certifier/{certifierId}/experience/{experienceId}/student/{studentId}/document/{storageId}/file", method = RequestMethod.DELETE)
	public @ResponseBody Document deleteFileFromCertificate(@PathVariable String certifierId,
			@PathVariable String experienceId, @PathVariable String studentId, @PathVariable String storageId,
			HttpServletRequest request) throws Exception {
		if (!validateCertifierAuthorization(certifierId, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Document result = documentManager.removeFileFromDocument(experienceId, studentId, storageId);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("deleteFileFromCertificate[%s]: %s", "tenant", result.getStorageId()));
		}
		return result;
	}

	private boolean validateCertifierAuthorization(String certifierId, String action, HttpServletRequest request)
			throws Exception {
		String resourceName = "certifier";
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("certifier-certifierId", certifierId);
		AccountAttributeDTO account = getAccountByCF(request);
		RequestedAuthorizationDTO authorization = authorizationManager.getReqAuthorization(account, action,
				resourceName, attributes);
		if (!authorizationManager.validateAuthorization(authorization)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid or call not authorized");
		}
		return true;
	}

	@ExceptionHandler({ EntityNotFoundException.class, StorageException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Map<String, String> handleEntityNotFoundError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}

	@ExceptionHandler(UnauthorizedException.class)
	@ResponseStatus(value = HttpStatus.FORBIDDEN)
	@ResponseBody
	public Map<String, String> handleUnauthorizedError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Map<String, String> handleGenericError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}
}
