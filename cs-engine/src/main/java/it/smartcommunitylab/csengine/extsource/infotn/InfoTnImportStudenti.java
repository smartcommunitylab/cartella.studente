package it.smartcommunitylab.csengine.extsource.infotn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.smartcommunitylab.aac.authorization.beans.AccountAttributeDTO;
import it.smartcommunitylab.aac.authorization.beans.AuthorizationDTO;
import it.smartcommunitylab.aac.authorization.beans.AuthorizationUserDTO;
import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.HTTPUtils;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.exception.StorageException;
import it.smartcommunitylab.csengine.model.Consent;
import it.smartcommunitylab.csengine.model.MetaInfo;
import it.smartcommunitylab.csengine.model.ScheduleUpdate;
import it.smartcommunitylab.csengine.model.Student;
import it.smartcommunitylab.csengine.security.AuthorizationManager;
import it.smartcommunitylab.csengine.storage.RepositoryManager;
import it.smartcommunitylab.csengine.storage.ScheduleUpdateRepository;
import it.smartcommunitylab.csengine.storage.StudentRepository;

@Service
public class InfoTnImportStudenti {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportStudenti.class);

	@Value("${infotn.source.folder}")
	private String sourceFolder;
	@Value("${infotn.starting.year}")
	private int startingYear;
	@Value("${profile.account}")
	private String profileAccount;
	@Value("${profile.attribute}")
	private String profileAttribute;
	@Value("${authorization.userType}")
	private String userType;
	@Value("${infotn.api.url}")
	private String infoTNAPIUrl;
	@Value("${infotn.api.user}")
	private String user;
	@Value("${infotn.api.pass}")
	private String password;

	private String apiKey = Const.API_STUDENTI_KEY;

	@Autowired
	private APIUpdateManager apiUpdateManager;
	@Autowired
	StudentRepository studentRepository;
	@Autowired
	private RepositoryManager dataManager;
	@Autowired
	AuthorizationManager authorizationManager;
	@Autowired
	ScheduleUpdateRepository metaInfoRepository;

	SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", Locale.ITALY);
	SimpleDateFormat sdfStandard = new SimpleDateFormat("dd/MM/yyyy");

	public void initStudenti(ScheduleUpdate scheduleUpdate) throws Exception {
		logger.info("start initStudenti");
		List<MetaInfo> metaInfosStudenti = scheduleUpdate.getUpdateMap().get(apiKey);

		if (metaInfosStudenti == null) {
			metaInfosStudenti = new ArrayList<MetaInfo>();
		}
		for (int i = startingYear; i <= Calendar.getInstance().get(Calendar.YEAR); i++) {
			MetaInfo metaInfo = new MetaInfo();
			metaInfo.setName(apiKey);
			metaInfo.setSchoolYear(i);
			updateStudenti(metaInfo);
			metaInfosStudenti.add(metaInfo);
		}
		scheduleUpdate.getUpdateMap().put(apiKey, metaInfosStudenti);

	}

	private void updateStudenti(MetaInfo metaInfo) throws Exception {

		int total = 0;
		int stored = 0;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;
		int nextYear = metaInfo.getSchoolYear() + 1;
		String year = metaInfo.getSchoolYear() + "/" + String.valueOf(nextYear).substring(2);

		// read epoc timestamp from db(if exist)
		if (metaInfo.getEpocTimestamp() > 0) {
			url = infoTNAPIUrl + "/studenti?schoolYear=" + year + "&timestamp=" + metaInfo.getEpocTimestamp();
		} else {
			url = infoTNAPIUrl + "/studenti?schoolYear=" + year;
		}
		logger.info("start importStudentiUsingRESTAPI for year " + year);

		// call api.
		String response = HTTPUtils.get(url, null, user, password);
		if (response != null && !response.isEmpty()) {
			JsonFactory jsonFactory = new JsonFactory();
			jsonFactory.setCodec(objectMapper);
			JsonParser jp = jsonFactory.createParser(response);
			JsonToken current;
			current = jp.nextToken();
			if (current != JsonToken.START_ARRAY) {
				logger.error("Error: root should be array: quiting.");
				throw new Exception("Error: root should be array: quiting.");
			}
			while (jp.nextToken() != JsonToken.END_ARRAY) {
				total += 1;
				Studente studente = jp.readValueAs(Studente.class);
				logger.info("converting " + studente.getExtId());
				Student student = null;
				Student studentDb = studentRepository.findByExtId(studente.getOrigin(), studente.getExtId());
				if (studentDb == null) {
					student = convertToStudent(studente);
					studentRepository.save(student);
					stored += 1;
					logger.info(String.format("Save Student: %s - %s - %s", studente.getOrigin(), studente.getExtId(),
							student.getId()));
				} else {
					logger.warn(String.format("Student already exists: %s - %s", studente.getOrigin(),
							studente.getExtId()));
					student = studentDb;
				}
				// save consent
				// Consent consent =
				// dataManager.getConsentByStudent(student.getId());
				// if (consent == null) {
				// consent = new Consent();
				// consent.setStudentId(student.getId());
				// consent.setSubject(student.getCf());
				// consent.setAuthorized(Boolean.FALSE);
				// dataManager.addConsent(consent);
				// // set autorizhation
				// AccountAttributeDTO account = new AccountAttributeDTO();
				// account.setAccountName(profileAccount);
				// account.setAttributeName(profileAttribute);
				// account.setAttributeValue(student.getCf());
				// AuthorizationUserDTO user = new AuthorizationUserDTO();
				// user.setAccountAttribute(account);
				// user.setType(userType);
				// List<String> actions = new ArrayList<String>();
				// actions.add(Const.AUTH_ACTION_ADD);
				// actions.add(Const.AUTH_ACTION_DELETE);
				// actions.add(Const.AUTH_ACTION_READ);
				// actions.add(Const.AUTH_ACTION_UPDATE);
				// Map<String, String> attributes = new HashMap<String,
				// String>();
				// attributes.put("student-studentId", student.getId());
				// AuthorizationDTO authorization =
				// authorizationManager.getNewAuthorization(user, user, actions,
				// "student", attributes);
				// try {
				// authorizationManager.insertAuthorization(authorization);
				// } catch (Exception e) {
				// logger.warn(String.format("Error creating authorization: %s
				// -%s - %s", studente.getOrigin(),
				// studente.getExtId(), e.getMessage()));
				// }
				// }
			}
			// update time stamp (if all works fine).
			metaInfo.setEpocTimestamp(System.currentTimeMillis() / 1000);
			metaInfo.setTotalRead(total);
			metaInfo.setTotalStore(stored);
		}

	}

	private Student convertToStudent(Studente studente) throws ParseException {
		Student result = new Student();
		result.setOrigin(studente.getOrigin());
		result.setExtId(studente.getExtId());
		result.setId(Utils.getUUID());
		result.setCf(studente.getCf());
		result.setName(studente.getName());
		result.setSurname(studente.getSurname());
		// result.setBirthdate(getBirthdate(studente.getBirthdate()));
		result.setBirthdate(studente.getBirthdate());
		result.setAddress(getAddress(studente));
		result.setPhone(studente.getPhone());
		result.setMobilePhone(studente.getMobilephone());
		result.setEmail(studente.getEmail());
		result.setNationality(studente.getNazione());
		return result;
	}

	private String getAddress(Studente studente) {
		StringBuffer sb = new StringBuffer(studente.getAddress());
		sb.append(", ");
		sb.append(studente.getCap());
		sb.append(" ");
		sb.append(studente.getComune());
		return sb.toString();
	}

	private String getBirthdate(String dataNascita) throws ParseException {
		Date date = sdf.parse(dataNascita);
		String result = sdfStandard.format(date);
		return result;
	}

	public void addConsent(String cf) throws StorageException {

		Student studentDb = studentRepository.findByCF(cf);
		Consent consent = dataManager.getConsentByStudent(studentDb.getId());
		if (consent == null) {
			consent = new Consent();
			consent.setStudentId(studentDb.getId());
			consent.setSubject(studentDb.getCf());
			consent.setAuthorized(Boolean.FALSE);
			dataManager.addConsent(consent);
			// set autorizhation
			AccountAttributeDTO account = new AccountAttributeDTO();
			account.setAccountName(profileAccount);
			account.setAttributeName(profileAttribute);
			account.setAttributeValue(studentDb.getCf());
			AuthorizationUserDTO user = new AuthorizationUserDTO();
			user.setAccountAttribute(account);
			user.setType(userType);
			List<String> actions = new ArrayList<String>();
			actions.add(Const.AUTH_ACTION_ADD);
			actions.add(Const.AUTH_ACTION_DELETE);
			actions.add(Const.AUTH_ACTION_READ);
			actions.add(Const.AUTH_ACTION_UPDATE);
			Map<String, String> attributes = new HashMap<String, String>();
			attributes.put("student-studentId", studentDb.getId());
			AuthorizationDTO authorization = authorizationManager.getNewAuthorization(user, user, actions, "student",
					attributes);
			try {
				authorizationManager.insertAuthorization(authorization);
			} catch (Exception e) {
				logger.warn(String.format("Error creating authorization: %s -%s - %s", studentDb.getOrigin(),
						studentDb.getExtId(), e.getMessage()));
			}
		}
	}

	public String importStudentiFromRESTAPI() {
		try {
			List<MetaInfo> savedMetaInfoList = apiUpdateManager.fetchMetaInfoForAPI(apiKey);
			for (MetaInfo metaInfo : savedMetaInfoList) {
				if (!metaInfo.isBlocked()) {
					updateStudenti(metaInfo);
				}
			}
			apiUpdateManager.saveMetaInfoList(apiKey, savedMetaInfoList);
			return "OK";

		} catch (Exception e) {
			logger.error(e.getMessage());
			return e.getMessage();
		}

	}

	// public String importStudentiFromEmpty() throws Exception {
	// logger.info("start importStudentiFromEmpty");
	// int total = 0;
	// int stored = 0;
	// FileReader fileReader = new FileReader(sourceFolder + "FBKstudenti
	// v.02.json");
	// ObjectMapper objectMapper = new ObjectMapper();
	// objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
	// false);
	// JsonFactory jsonFactory = new JsonFactory();
	// jsonFactory.setCodec(objectMapper);
	// JsonParser jp = jsonFactory.createParser(fileReader);
	// JsonToken current;
	// current = jp.nextToken();
	// if (current != JsonToken.START_OBJECT) {
	// logger.error("Error: root should be object: quiting.");
	// return "Error: root should be object: quiting.";
	// }
	// while (jp.nextToken() != JsonToken.END_OBJECT) {
	// String fieldName = jp.getCurrentName();
	// current = jp.nextToken();
	// if (fieldName.equals("items")) {
	// if (current == JsonToken.START_ARRAY) {
	// while (jp.nextToken() != JsonToken.END_ARRAY) {
	// total += 1;
	// Studente studente = jp.readValueAs(Studente.class);
	// logger.info("converting " + studente.getExtid());
	// Student student = null;
	// Student studentDb = studentRepository.findByExtId(studente.getOrigin(),
	// studente.getExtid());
	// if (studentDb == null) {
	// student = convertToStudent(studente);
	// studentRepository.save(student);
	// stored += 1;
	// logger.info(String.format("Save Student: %s - %s - %s",
	// studente.getOrigin(),
	// studente.getExtid(), student.getId()));
	// } else {
	// logger.warn(String.format("Student already exists: %s - %s",
	// studente.getOrigin(),
	// studente.getExtid()));
	// student = studentDb;
	// }
	// // save consent
	// Consent consent = dataManager.getConsentByStudent(student.getId());
	// if (consent == null) {
	// consent = new Consent();
	// consent.setStudentId(student.getId());
	// consent.setSubject(student.getCf());
	// consent.setAuthorized(Boolean.FALSE);
	// dataManager.addConsent(consent);
	// // set autorizhation
	// AccountAttributeDTO account = new AccountAttributeDTO();
	// account.setAccountName(profileAccount);
	// account.setAttributeName(profileAttribute);
	// account.setAttributeValue(student.getCf());
	// AuthorizationUserDTO user = new AuthorizationUserDTO();
	// user.setAccountAttribute(account);
	// user.setType(userType);
	// List<String> actions = new ArrayList<String>();
	// actions.add(Const.AUTH_ACTION_ADD);
	// actions.add(Const.AUTH_ACTION_DELETE);
	// actions.add(Const.AUTH_ACTION_READ);
	// actions.add(Const.AUTH_ACTION_UPDATE);
	// Map<String, String> attributes = new HashMap<String, String>();
	// attributes.put("student-studentId", student.getId());
	// AuthorizationDTO authorization =
	// authorizationManager.getNewAuthorization(user, user,
	// actions, "student", attributes);
	// try {
	// authorizationManager.insertAuthorization(authorization);
	// } catch (Exception e) {
	// logger.warn(String.format("Error creating authorization: %s - %s - %s",
	// studente.getOrigin(), studente.getExtid(), e.getMessage()));
	// }
	// }
	// }
	// } else {
	// logger.warn("Error: records should be an array: skipping.");
	// jp.skipChildren();
	// }
	// } else {
	// logger.warn("Unprocessed property: " + fieldName);
	// jp.skipChildren();
	// }
	// }
	// return stored + "/" + total;
	// }

}
