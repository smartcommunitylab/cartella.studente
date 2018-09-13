package it.smartcommunitylab.csengine.extsource.infotn;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import it.smartcommunitylab.csengine.model.Course;
import it.smartcommunitylab.csengine.model.Experience;
import it.smartcommunitylab.csengine.model.MetaInfo;
import it.smartcommunitylab.csengine.model.ScheduleUpdate;
import it.smartcommunitylab.csengine.model.Student;
import it.smartcommunitylab.csengine.model.StudentExperience;
import it.smartcommunitylab.csengine.storage.CertifierRepository;
import it.smartcommunitylab.csengine.storage.CourseRepository;
import it.smartcommunitylab.csengine.storage.ExperienceRepository;
import it.smartcommunitylab.csengine.storage.InstituteRepository;
import it.smartcommunitylab.csengine.storage.ScheduleUpdateRepository;
import it.smartcommunitylab.csengine.storage.StudentExperienceRepository;
import it.smartcommunitylab.csengine.storage.StudentRepository;

@Service
public class InfoTnImportIscrizioneStage {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportIscrizioneStage.class);

	@Autowired
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

	private String apiKey = Const.API_PARTICIPAZIONI_STAGE_KEY;

	@Autowired
	private APIUpdateManager apiUpdateManager;
	@Autowired
	ExperienceRepository experienceRepository;
	@Autowired
	StudentExperienceRepository studentExperienceRepository;
	@Autowired
	InstituteRepository instituteRepository;
	@Autowired
	CertifierRepository certifierRepository;
	@Autowired
	StudentRepository studentRepository;
	@Autowired
	CourseRepository courseRepository;
	@Autowired
	ScheduleUpdateRepository metaInfoRepository;

	SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", Locale.ITALY);

	public void initIscrStage(ScheduleUpdate scheduleUpdate) throws Exception {
		logger.info("start initIscrStage");
		List<MetaInfo> metaInfosIscrizioneStage = scheduleUpdate.getUpdateMap().get(apiKey);

		if (metaInfosIscrizioneStage == null) {
			metaInfosIscrizioneStage = new ArrayList<MetaInfo>();
		}
		for (int i = startingYear; i <= Calendar.getInstance().get(Calendar.YEAR); i++) {
			MetaInfo metaInfo = new MetaInfo();
			metaInfo.setName(apiKey);
			metaInfo.setSchoolYear(i);
			updateIscirzioneStage(metaInfo);
			metaInfosIscrizioneStage.add(metaInfo);
		}
		scheduleUpdate.getUpdateMap().put(apiKey, metaInfosIscrizioneStage);

	}

	private void updateIscirzioneStage(MetaInfo metaInfo) throws Exception {

		int total = 0;
		int stored = 0;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;
		int nextYear = metaInfo.getSchoolYear() + 1;
		String schoolYear = metaInfo.getSchoolYear() + "/" + String.valueOf(nextYear).substring(2);

		// read epoc timestamp from db(if exist)
		if (metaInfo.getEpocTimestamp() > 0) {
			url = infoTNAPIUrl + "/partecipazionestage?schoolYear=" + schoolYear + "&timestamp="
					+ metaInfo.getEpocTimestamp();
		} else {
			url = infoTNAPIUrl + "/partecipazionestage?schoolYear=" + schoolYear;
		}
		logger.info("start importIscirzioneStageUsingRESTAPI for year " + schoolYear);

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
				IscrizioneStage iscrizione = jp.readValueAs(IscrizioneStage.class);
				logger.info("converting " + iscrizione.getExtId());
				StudentExperience experienceDb = studentExperienceRepository.findByExtId(iscrizione.getOrigin(),
						iscrizione.getExtId());
				if (experienceDb != null) {
					logger.warn(String.format("StudentExperience already exists: %s - %s", iscrizione.getOrigin(),
							iscrizione.getExtId()));
					continue;
				}
				Experience experience = experienceRepository.findByExtId(iscrizione.getStageRef().getOrigin(),
						iscrizione.getStageRef().getExtId());
				if (experience == null) {
					logger.warn(String.format("Experience not found: %s - %s", iscrizione.getStageRef().getOrigin(),
							iscrizione.getStageRef().getExtId()));
					continue;
				}
				Student student = studentRepository.findByExtId(iscrizione.getStudent().getOrigin(),
						iscrizione.getStudent().getExtId());
				if (student == null) {
					logger.warn(String.format("Student not found: %s - %s", iscrizione.getStudent().getOrigin(),
							iscrizione.getStudent().getExtId()));
					continue;
				}

				Course course = courseRepository.findByExtId(
						experience.getAttributes().get(Const.COURSE_REF_ORIGIN).toString(),
						experience.getAttributes().get(Const.COURSE_REF_EXTID).toString());

				StudentExperience studentExperience = convertToExperience(iscrizione, experience, student, course);
				studentExperienceRepository.save(studentExperience);
				stored += 1;
				logger.info(String.format("Save Stage experience: %s - %s - %s", iscrizione.getOrigin(),
						iscrizione.getExtId(), studentExperience.getId()));
			}

			// update time stamp (if all works fine).
			metaInfo.setEpocTimestamp(System.currentTimeMillis() / 1000);
			metaInfo.setTotalRead(total);
			metaInfo.setTotalStore(stored);

		}

	}

	private StudentExperience convertToExperience(IscrizioneStage iscrizione, Experience experience, Student student,
			Course course) {
		StudentExperience result = new StudentExperience();
		Date now = new Date();
		result.setCreationDate(now);
		result.setLastUpdate(now);
		result.setOrigin(iscrizione.getOrigin());
		result.setExtId(iscrizione.getExtId());
		result.setId(Utils.getUUID());

		if (course != null) {
			experience.getAttributes().put(Const.ATTR_INSTITUTEID, course.getInstituteId());
			experience.getAttributes().put(Const.ATTR_TUID, course.getTeachingUnitId());
		}

		result.setStudentId(student.getId());
		result.setStudent(student);
		result.setExperienceId(experience.getId());
		result.setExperience(experience);

		return result;
	}

	public String importPartecipazioneStageFromRESTAPI() {
		try {
			List<MetaInfo> savedMetaInfoList = apiUpdateManager.fetchMetaInfoForAPI(apiKey);
			for (MetaInfo metaInfo : savedMetaInfoList) {
				if (!metaInfo.isBlocked()) {
					updateIscirzioneStage(metaInfo);
				}
			}
			apiUpdateManager.saveMetaInfoList(apiKey, savedMetaInfoList);
			return "OK";

		} catch (Exception e) {
			logger.error(e.getMessage());
			return e.getMessage();
		}

	}

	// public String importIscrizioneStageFromEmpty() throws Exception {
	// logger.info("start importIscrizioneStageFromEmpty");
	// int total = 0;
	// int stored = 0;
	// FileReader fileReader = new FileReader(sourceFolder + "FBK_ISCRIZIONI
	// STAGE triennio v.01.json");
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
	// IscrizioneStage iscrizione = jp.readValueAs(IscrizioneStage.class);
	// logger.info("converting " + iscrizione.getExtid());
	// StudentExperience experienceDb =
	// studentExperienceRepository.findByExtId(iscrizione.getOrigin(),
	// iscrizione.getExtid());
	// if (experienceDb != null) {
	// logger.warn(String.format("StudentExperience already exists: %s - %s",
	// iscrizione.getOrigin(), iscrizione.getExtid()));
	// continue;
	// }
	// Experience experience =
	// experienceRepository.findByExtId(iscrizione.getOrigin_stage(),
	// iscrizione.getExtid_stage());
	// if (experience == null) {
	// logger.warn(String.format("Experience not found: %s - %s",
	// iscrizione.getOrigin_stage(),
	// iscrizione.getExtid_stage()));
	// continue;
	// }
	// Student student =
	// studentRepository.findByExtId(iscrizione.getOrigin_student(),
	// iscrizione.getExtid_student());
	// if (student == null) {
	// logger.warn(String.format("Student not found: %s - %s",
	// iscrizione.getOrigin_student(),
	// iscrizione.getExtid_student()));
	// continue;
	// }
	// Course course =
	// courseRepository.findByExtId(iscrizione.getOrigin_course(),
	// iscrizione.getExtid_course());
	// StudentExperience studentExperience = convertToExperience(iscrizione,
	// experience, student,
	// course);
	// studentExperienceRepository.save(studentExperience);
	// stored += 1;
	// logger.info(String.format("Save Stage: %s - %s - %s",
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
