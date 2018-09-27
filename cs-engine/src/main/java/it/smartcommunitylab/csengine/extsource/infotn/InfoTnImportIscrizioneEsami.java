package it.smartcommunitylab.csengine.extsource.infotn;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

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

import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.HTTPUtils;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.model.Experience;
import it.smartcommunitylab.csengine.model.MetaInfo;
import it.smartcommunitylab.csengine.model.Student;
import it.smartcommunitylab.csengine.model.StudentExperience;
import it.smartcommunitylab.csengine.storage.ExperienceRepository;
import it.smartcommunitylab.csengine.storage.InstituteRepository;
import it.smartcommunitylab.csengine.storage.ScheduleUpdateRepository;
import it.smartcommunitylab.csengine.storage.StudentExperienceRepository;
import it.smartcommunitylab.csengine.storage.StudentRepository;

@Service
public class InfoTnImportIscrizioneEsami {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportIscrizioneEsami.class);

	@Value("${infotn.source.folder}")
	private String sourceFolder;
	@Value("${infotn.starting.year}")
	private int startingYear;
	@Value("${infotn.api.url}")
	private String infoTNAPIUrl;
	@Value("${infotn.api.user}")
	private String user;
	@Value("${infotn.api.pass}")
	private String password;

	private String apiKey = Const.API_ISCRIZIONI_ESAMI_KEY;

	@Autowired
	private APIUpdateManager apiUpdateManager;
	@Autowired
	ExperienceRepository experienceRepository;
	@Autowired
	InstituteRepository instituteRepository;
	@Autowired
	StudentExperienceRepository studentExperienceRepository;
	@Autowired
	StudentRepository studentRepository;
	@Autowired
	ScheduleUpdateRepository metaInfoRepository;

	private void updateIscrizioneEsami(MetaInfo metaInfo) throws Exception {

		int total = 0;
		int stored = 0;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;
		int nextYear = metaInfo.getSchoolYear() + 1;
		String schoolYear = metaInfo.getSchoolYear() + "/" + String.valueOf(nextYear).substring(2);

		// read epoc timestamp from db(if exist)
		if (metaInfo.getEpocTimestamp() > 0) {
			url = infoTNAPIUrl + "/esitiesami?schoolYear=" + schoolYear + "&timestamp=" + metaInfo.getEpocTimestamp();
		} else {
			url = infoTNAPIUrl + "/esitiesami?schoolYear=" + schoolYear;
		}
		logger.info("start importIscrizioneEsamiRESTAPI for year " + schoolYear);

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
				IscrizioneEsame iscrizione = jp.readValueAs(IscrizioneEsame.class);
				logger.info("converting " + iscrizione.getExtId());
				StudentExperience experienceDb = studentExperienceRepository.findByExtId(iscrizione.getOrigin(),
						iscrizione.getExtId());
				if (experienceDb != null) {
					logger.warn(String.format("StudentExperience already exists: %s - %s", iscrizione.getOrigin(),
							iscrizione.getExtId()));
					continue;
				}
				Student student = studentRepository.findByExtId(iscrizione.getStudents().getOrigin(),
						iscrizione.getStudents().getExtId());
				if (student == null) {
					logger.warn(String.format("Student not found: %s - %s", iscrizione.getStudents().getOrigin(),
							iscrizione.getStudents().getExtId()));
					continue;
				}
				Experience experience = experienceRepository.findByExtId(iscrizione.getExamRef().getOrigin(),
						iscrizione.getExamRef().getExtId());
				if (experience == null) {
					logger.warn(String.format("Experience not found: %s - %s", iscrizione.getExamRef().getOrigin(),
							iscrizione.getExamRef().getExtId()));
					continue;
				}
				StudentExperience studentExperience = convertToExperience(iscrizione, experience, student);
				studentExperienceRepository.save(studentExperience);
				stored += 1;
				logger.info(String.format("Save Experience: %s - %s - %s", iscrizione.getOrigin(),
						iscrizione.getExtId(), studentExperience.getId()));

			}

			// update time stamp (if all works fine).
			metaInfo.setEpocTimestamp(System.currentTimeMillis() / 1000);
			metaInfo.setTotalRead(total);
			metaInfo.setTotalStore(stored);

		}

	}

	private StudentExperience convertToExperience(IscrizioneEsame iscrizione, Experience experience, Student student)
			throws ParseException {
		StudentExperience result = new StudentExperience();
		Date now = new Date();
		result.setCreationDate(now);
		result.setLastUpdate(now);
		result.setOrigin(iscrizione.getOrigin());
		result.setExtId(iscrizione.getExtId());
		result.setId(Utils.getUUID());

		experience.getAttributes().put(Const.ATTR_HONOUR, iscrizione.getStudents().getHonour());
		experience.getAttributes().put(Const.ATTR_GRADE, getJudgement(iscrizione));
		experience.getAttributes().put(Const.ATTR_RESULT, iscrizione.getStudents().getPositiveResult());
		experience.getAttributes().put(Const.ATTR_EXTERNALCANDIDATE, iscrizione.getStudents().getExternalCandidate());

		result.setStudentId(student.getId());
		result.setStudent(student);
		result.setExperienceId(experience.getId());
		result.setExperience(experience);

		return result;
	}

	// private boolean getHonour(IscrizioneEsame iscrizione) {
	// boolean result = false;
	// if (Utils.isNotEmpty(iscrizione.getgetHonour())) {
	// result = iscrizione.getHonour().equals("1");
	// }
	// return result;
	// }

	// private boolean getExternalCandidate(IscrizioneEsame iscrizione) {
	// boolean result = false;
	// if (Utils.isNotEmpty(iscrizione.getExternalcandidate())) {
	// result = iscrizione.getExternalcandidate().equals("1");
	// }
	// return result;
	// }

	private String getJudgement(IscrizioneEsame iscrizione) {
		String result = null;
		if (Utils.isNotEmpty(iscrizione.getStudents().getGrade())) {
			result = iscrizione.getStudents().getGrade();
		} else if (Utils.isNotEmpty(iscrizione.getStudents().getJudgement())) {
			result = iscrizione.getStudents().getJudgement();
		}
		return result;
	}

	public String importIscrizioneEsamiFromRESTAPI() {
		try {
			List<MetaInfo> savedMetaInfoList = apiUpdateManager.fetchMetaInfoForAPI(apiKey);

			if (savedMetaInfoList == null || savedMetaInfoList.isEmpty()) {
				// call generic method to create metaInfos (apiKey, year?)
				savedMetaInfoList = apiUpdateManager.createMetaInfoForAPI(apiKey, true);
			}

			for (MetaInfo metaInfo : savedMetaInfoList) {
				if (!metaInfo.isBlocked()) {
					updateIscrizioneEsami(metaInfo);
				}
			}

			apiUpdateManager.saveMetaInfoList(apiKey, savedMetaInfoList);

			return "OK";

		} catch (Exception e) {
			logger.error(e.getMessage());
			return e.getMessage();
		}
	}

	// public String importIscrizioneEsamiFromEmpty() throws Exception {
	// logger.info("start importIscrizioneEsamiFromEmpty");
	// int total = 0;
	// int stored = 0;
	// FileReader fileReader = new FileReader(sourceFolder +
	// "FBK_iscrizioniesami triennio v.02.json");
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
	// IscrizioneEsame iscrizione = jp.readValueAs(IscrizioneEsame.class);
	// logger.info("converting " + iscrizione.getExtid());
	// StudentExperience experienceDb =
	// studentExperienceRepository.findByExtId(iscrizione.getOrigin(),
	// iscrizione.getExtid());
	// if (experienceDb != null) {
	// logger.warn(String.format("StudentExperience already exists: %s - %s",
	// iscrizione.getOrigin(), iscrizione.getExtid()));
	// continue;
	// }
	// Student student =
	// studentRepository.findByExtId(iscrizione.getOrigin_student(),
	// iscrizione.getExtid_studente());
	// if (student == null) {
	// logger.warn(String.format("Student not found: %s - %s",
	// iscrizione.getOrigin_student(),
	// iscrizione.getExtid_studente()));
	// continue;
	// }
	// Experience experience =
	// experienceRepository.findByExtId(iscrizione.getOrigin_exam(),
	// iscrizione.getExtid_exam());
	// if (experience == null) {
	// logger.warn(String.format("Experience not found: %s - %s",
	// iscrizione.getOrigin_exam(),
	// iscrizione.getExtid_exam()));
	// continue;
	// }
	// StudentExperience studentExperience = convertToExperience(iscrizione,
	// experience, student);
	// studentExperienceRepository.save(studentExperience);
	// stored += 1;
	// logger.info(String.format("Save Experience: %s - %s - %s",
	// iscrizione.getOrigin(),
	// iscrizione.getExtid(), studentExperience.getId()));
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
