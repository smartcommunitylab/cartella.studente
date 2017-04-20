package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.exception.StorageException;
import it.smartcommunitylab.csengine.model.Certificate;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class DocumentManager {
	private static final transient Logger logger = LoggerFactory.getLogger(DocumentManager.class);

	private AmazonS3 s3;
	
	@Autowired
	@Value("${bucketName}")	
	private String bucketName;

	@Autowired
	@Value("${docUrlExpiration}")	
	private Long docUrlExpiration;
	
	@Autowired
	private RepositoryManager dataManager;

	@SuppressWarnings("deprecation")
	public DocumentManager() {
		this.s3 = new AmazonS3Client(new ProfileCredentialsProvider());
	}

	public Certificate addFileToCertificate(String experienceId, String studentId, MultipartFile file) 
			throws Exception {
		Certificate certificate = dataManager.getCertificate(experienceId, studentId);
		if(certificate == null) {
			throw new StorageException("certificate not present");
		}
		String contentType = file.getContentType();
		s3.putObject(new PutObjectRequest(bucketName, certificate.getStorageId(), createTmpFile(file)));
		URL signedUrl = generateSignedUrl(bucketName, certificate.getStorageId());
		String documentUri = signedUrl.toString();
		Certificate result = dataManager.updateCertificateUri(experienceId, studentId, documentUri, contentType);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addFileToCertificate: %s - %s - %s", experienceId, studentId, result.getStorageId()));
		}
		return result;
	}

	public Certificate removeFileFromCertificate(String experienceId, String studentId) 
			throws Exception {
		Certificate certificate = dataManager.getCertificate(experienceId, studentId);
		if(certificate == null) {
			throw new StorageException("certificate not present");
		}
		s3.deleteObject(new DeleteObjectRequest(bucketName, certificate.getStorageId()));
		Certificate result = dataManager.removeCertificateUri(experienceId, studentId);
		return result;
	}
	
	public void setSignedUrl(Certificate certificate) {
		if(certificate != null) {
			if(certificate.getDocumentPresent()) {
				URL signedUrl = generateSignedUrl(bucketName, certificate.getStorageId());
				certificate.setDocumentUri(signedUrl.toString());
			} else {
				certificate.setDocumentUri(null);
			}
		}
	}
	
	private URL generateSignedUrl(String bucketName, String key) {
		Date expiration = new java.util.Date();
		long milliSeconds = expiration.getTime();
		milliSeconds += 1000 * 60 * docUrlExpiration; 
		expiration.setTime(milliSeconds);
		GeneratePresignedUrlRequest generatePresignedUrlRequest = 
		    new GeneratePresignedUrlRequest(bucketName, key);
		generatePresignedUrlRequest.setMethod(HttpMethod.GET); 
		generatePresignedUrlRequest.setExpiration(expiration);
		URL url = s3.generatePresignedUrl(generatePresignedUrlRequest);
		return url;
	}

	private File createTmpFile(MultipartFile file) throws IOException {
		Path tempFile = Files.createTempFile("cs-doc", ".tmp");
		tempFile.toFile().deleteOnExit();
		Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
		return tempFile.toFile();
	}
	
}
