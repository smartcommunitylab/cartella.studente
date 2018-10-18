package it.smartcommunitylab.csengine.storage;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import it.smartcommunitylab.csengine.exception.StorageException;
import it.smartcommunitylab.csengine.model.Document;
import it.smartcommunitylab.csengine.model.Student;
import it.smartcommunitylab.csengine.model.StudentExperience;

@Component
public class LocalDocumentManager {

	public static final String DOCUMENT = "DOCUMENT";
	public static final String PHOTO = "PHOTO";
	private static final String DOWNLOAD = "download/file?key=";
	
	private static final transient Logger logger = LoggerFactory.getLogger(LocalDocumentManager.class);

	@Value("${storage.local.dir}")
	private String storageDir;		
	
	@Autowired
	@Value("${docUrlExpiration}")	
	private Long docUrlExpiration;
	
	@Value("${encrypt.key}")
	private String encryptKey;	
	
	@Autowired
	private RepositoryManager dataManager;
	
    private IvParameterSpec ivParameterSpec;
    private SecretKeySpec secretKeySpec;
    private Cipher cipher;	

	@PostConstruct
	public void init() throws Exception {
        ivParameterSpec = new IvParameterSpec(encryptKey.getBytes("UTF-8"));
        secretKeySpec = new SecretKeySpec(encryptKey.getBytes("UTF-8"), "AES");
        cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");		
	}

	public Document addFileToDocument(String experienceId, String studentId, 
			String storageId, String filename, MultipartFile file) throws Exception {
		Document document = dataManager.getDocument(experienceId, studentId, storageId);
		if(document == null) {
			throw new StorageException("certificate not present");
		}
		
		StudentExperience studentExperience = dataManager.getStudentExperience(experienceId, studentId);
		
		String contentType = file.getContentType();
		
		String id = storageId + "@" + studentExperience.getId();
		String url = getDocumentSignedUrl(studentExperience, document);
		
		saveFile(file, DOCUMENT, id);
		
		Document result = dataManager.addFileToDocument(experienceId, studentId, storageId,
				contentType, filename, url);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addFileToCertificate: %s - %s - %s", experienceId, studentId, result.getStorageId()));
		}
		return result;
	}

	public Document removeFileFromDocument(String experienceId, String studentId,
			String storageId) throws Exception {
		Document document = dataManager.getDocument(experienceId, studentId, storageId);
		if(document == null) {
			throw new StorageException("document not present");
		}

		Document result = dataManager.removeFileToDocument(experienceId, studentId, storageId);
		
		deleteFile(DOCUMENT, storageId);
		
		return result;
	}
	
	public void addFileToProfile(String studentId, MultipartFile file) throws Exception {
		Student student = dataManager.getStudent(studentId);
		if(student == null) {
			throw new StorageException("certificate not present");
		}
		String contentType = file.getContentType();
		
		saveFile(file, PHOTO, studentId);
		
		dataManager.updateStudentContentType(studentId, contentType);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addFileToProfile: %s", studentId));
		}
	}
	
	public String getPhotoSignedUrl(String studentId) throws Exception {
		String url = DOWNLOAD + URLEncoder.encode(encode(PHOTO, studentId), "UTF-8");
		return url;
	}
	
	public String getDocumentSignedUrl(StudentExperience studentExperience, Document document) throws Exception {
		String url = DOWNLOAD + URLEncoder.encode(encode(DOCUMENT, document.getStorageId() + "@" + studentExperience.getId()), "UTF-8");
		return url;		
	}
	
	public String[] getFile(String key) throws Exception {
		String split[] = decode(key);
		
		String result[] = new String[4];
		
		result[0] = split[0] + "_" + split[1];
		result[2] = split[2];
		
		try {
		switch (split[0]) {
		case DOCUMENT:
			String ids[] = split[1].split("@");
			StudentExperience studentExperience = dataManager.getStudentExperience(ids[1]);
			if (studentExperience == null) {
				return null;
			}
			Document document = dataManager.getDocument(studentExperience.getExperienceId(), studentExperience.getStudentId(), ids[0]);
			result[1] = document.getContentType();
			result[3] = document.getFilename();
			break;
		case PHOTO:
			Student student = dataManager.getStudent(split[1]);
			if(student == null) {
				return null;
			}
			result[1] = student.getContentType();
		}
		
		return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/////
	
	public String encode(String type, String id) throws Exception {
		return encrypt(type + "?" + id + "?" + (System.currentTimeMillis() + 60 * 1000 * docUrlExpiration));
	}	
	
	public String[] decode(String key) throws Exception {
		String decrypted = decrypt(key);
		String split[] = decrypted.split("\\?");
		
		return split;
	}	
	
	private String encrypt(String toBeEncrypt) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException,
			IllegalBlockSizeException, UnsupportedEncodingException {
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
		byte[] encrypted = cipher.doFinal(toBeEncrypt.getBytes());
		return Base64.encodeBase64URLSafeString(encrypted);
	}

	public String decrypt(String encrypted) throws InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
		byte[] decryptedBytes = cipher.doFinal(Base64.decodeBase64(encrypted));
		return new String(decryptedBytes);
	}	
	
	private File saveFile(MultipartFile data, String type, String key) throws IOException {
		File file = new File(storageDir, type + "_" + key);
		data.transferTo(file);			
		
		return file;
	}	
	
	public File loadFile(String name) throws IOException {
		File file = new File(storageDir, name);
		
		return file;
	}		
	
	public boolean deleteFile(String type, String name) throws IOException {
		File file = new File(storageDir, type + "_" + name);
		return file.delete();
	}		
	
}
