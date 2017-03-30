package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.Certificate;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

public class DocumentManager {
	private static final transient Logger logger = LoggerFactory.getLogger(DocumentManager.class);
	
	@Autowired
	private RepositoryManager dataManager;

	public Certificate addFileToCertificate(String certificateId, Map<String, MultipartFile> fileMap) {
		// TODO Auto-generated method stub
		return null;
	}

	public Certificate removeFrileFromCertificate(String certificateId) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
