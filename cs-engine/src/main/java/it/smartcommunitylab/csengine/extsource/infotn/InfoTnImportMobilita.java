package it.smartcommunitylab.csengine.extsource.infotn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
import it.smartcommunitylab.csengine.storage.MetaInfoRepository;
import it.smartcommunitylab.csengine.storage.StudentExperienceRepository;
import it.smartcommunitylab.csengine.storage.StudentRepository;

@Component
public class InfoTnImportMobilita {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportMobilita.class);

	@Autowired
	@Value("${infotn.source.folder}")
	private String sourceFolder;

	@Value("${infotn.api.url}")
	private String infoTNAPIUrl;

	@Value("${infotn.api.user}")
	private String user;

	@Value("${infotn.api.pass}")
	private String password;

	private String metaInfoName = "Mobility";
	private String metaInfoIstituzioni = "Istituzioni";

	@Autowired
	ExperienceRepository experienceRepository;

	@Autowired
	InstituteRepository instituteRepository;

	@Autowired
	StudentExperienceRepository studentExperienceRepository;

	@Autowired
	StudentRepository studentRepository;

	@Autowired
	MetaInfoRepository metaInfoRepository;

	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);

	// public String importMobilitaFromEmpty() throws Exception {
	// logger.info("start importMobilitaFromEmpty");
	// int total = 0;
	// int stored = 0;
	// FileReader fileReader = new FileReader(sourceFolder + "FBK_MOBILITA
	// ESTERO triennio v.02.json");
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
	// Mobilita mob = jp.readValueAs(Mobilita.class);
	// logger.info("converting " + mob.getExtid());
	// Experience experienceDb = experienceRepository.findByExtId(
	// mob.getOrigin(), mob.getExtid());
	// if(experienceDb != null) {
	// logger.warn(String.format("Experience already exists: %s - %s",
	// mob.getOrigin(), mob.getExtid()));
	// continue;
	// }
	// StudentExperience studentExperienceDb =
	// studentExperienceRepository.findByExtId(
	// mob.getOrigin(), mob.getExtid());
	// if(studentExperienceDb != null) {
	// logger.warn(String.format("StudentExperience already exists: %s - %s",
	// mob.getOrigin(), mob.getExtid()));
	// continue;
	// }
	// Student student = studentRepository.findByExtId(mob.getOrigin_student(),
	// mob.getExtid_student());
	// if(student == null) {
	// logger.warn(String.format("Student not found: %s - %s",
	// mob.getOrigin_student(), mob.getExtid_student()));
	// continue;
	// }
	// Experience experience = convertToExperience(mob);
	// experience = experienceRepository.save(experience);
	// StudentExperience studentExperience = convertToStudentExperience(mob,
	// experience, student);
	// studentExperienceRepository.save(studentExperience);
	// stored += 1;
	// logger.info(String.format("Save Experience: %s - %s - %s",
	// mob.getOrigin(),
	// mob.getExtid(), studentExperience.getId()));
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

//	@Scheduled(cron = "0 45 01 * * ?")
	public String importIscrizioneMobilitaFromRESTAPI() throws Exception {
		logger.info("start importIscrizioneMobilitaFromRESTAPI");
		MetaInfo metaInfoIst = metaInfoRepository.findOne(metaInfoIstituzioni);
		if (metaInfoIst != null) {
			Map<String, String> schoolYears = metaInfoIst.getSchoolYears();
			// read registered time stamp.
			MetaInfo metaInfo = metaInfoRepository.findOne(metaInfoName);
			if (metaInfo != null) {
				// get currentYear.
				int currentYear = Calendar.getInstance().get(Calendar.YEAR);
				int nextYear = currentYear + 1;
				String schoolYear = currentYear + "/" + String.valueOf(nextYear).substring(2);
				String url = infoTNAPIUrl + "/mobilita?schoolYear=" + schoolYear + "&timestamp="
						+ metaInfo.getEpocTimestamp();
				try {

					importIscrizioneMobilitaUsingRESTAPI(url, schoolYear, metaInfo);
					return metaInfo.getTotalStore() + "/" + metaInfo.getTotalRead();

				} catch (Exception e) {
					return e.getMessage();
				}

			} else {
				metaInfo = new MetaInfo();
				metaInfo.setName(metaInfoName);
				try {

					for (Map.Entry<String, String> entry : schoolYears.entrySet()) {
						String url = infoTNAPIUrl + "/mobilita?schoolYear=" + entry.getValue();
						importIscrizioneMobilitaUsingRESTAPI(url, entry.getValue(), metaInfo);
					}
					return (metaInfo.getTotalStore() + "/" + metaInfo.getTotalRead());

				} catch (Exception e) {
					return e.getMessage();
				}
			}
		} else {
			return "Run /istituto import first.";
		}
	}

	private void importIscrizioneMobilitaUsingRESTAPI(String url, String schoolYear, MetaInfo metaInfo)
			throws Exception {
		logger.info("start importIscrizioneMobilitaRESTAPI for year " + schoolYear);
		int total = 0;
		int stored = 0;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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
				Mobilita mob = jp.readValueAs(Mobilita.class);
				logger.info("converting " + mob.getExtId());
				Experience experienceDb = experienceRepository.findByExtId(mob.getOrigin(), mob.getExtId());
				if (experienceDb != null) {
					logger.warn(String.format("Experience already exists: %s - %s", mob.getOrigin(), mob.getExtId()));
					continue;
				}
				StudentExperience studentExperienceDb = studentExperienceRepository.findByExtId(mob.getOrigin(),
						mob.getExtId());
				if (studentExperienceDb != null) {
					logger.warn(String.format("StudentExperience already exists: %s - %s", mob.getOrigin(),
							mob.getExtId()));
					continue;
				}
				Student student = studentRepository.findByExtId(mob.getStudentRef().getOrigin(), mob.getStudentRef().getExtId());
				if (student == null) {
					logger.warn(String.format("Student not found: %s - %s", mob.getStudentRef().getOrigin(),
							mob.getStudentRef().getExtId()));
					continue;
				}
				Experience experience = convertToExperience(mob);
				experience = experienceRepository.save(experience);
				StudentExperience studentExperience = convertToStudentExperience(mob, experience, student);
				studentExperienceRepository.save(studentExperience);
				stored += 1;
				logger.info(String.format("Save Experience: %s - %s - %s", mob.getOrigin(), mob.getExtId(),
						studentExperience.getId()));

			}

			// update time stamp (if all works fine).
			metaInfo.setEpocTimestamp(System.currentTimeMillis() / 1000);
			// total = metaInfo.getTotalRead() + total;
			metaInfo.setTotalRead(total);
			// stored = metaInfo.getTotalStore() + stored;
			metaInfo.setTotalStore(stored);
			metaInfoRepository.save(metaInfo);

		}

	}

	private Experience convertToExperience(Mobilita mob) throws ParseException {
		Experience result = new Experience();
		result.setOrigin(mob.getOrigin());
		result.setExtId(mob.getExtId());
		result.setId(Utils.getUUID());
		result.setType(Const.EXP_TYPE_MOBILITY);
		result.getAttributes().put(Const.ATTR_DATEFROM, sdf.parse(mob.getDateFrom()).getTime());
		result.getAttributes().put(Const.ATTR_DATETO, sdf.parse(mob.getDateTo()).getTime());
		result.getAttributes().put(Const.ATTR_EDUCATIONAL, Boolean.TRUE);
		result.getAttributes().put(Const.ATTR_INSTITUTIONAL, Boolean.TRUE);
		result.getAttributes().put(Const.ATTR_CERTIFIED, Boolean.TRUE);
		result.getAttributes().put(Const.ATTR_TYPE, mob.getType());
		result.getAttributes().put(Const.ATTR_TITLE, mob.getDescription());
		result.getAttributes().put(Const.ATTR_LOCATION, mob.getLocation());
//		result.getAttributes().put(Const.ATTR_LANG, mob.getLanguage());
		return result;
	}

	private StudentExperience convertToStudentExperience(Mobilita mob, Experience experience, Student student)
			throws ParseException {
		StudentExperience result = new StudentExperience();
		result.setOrigin(mob.getOrigin());
		result.setExtId(mob.getExtId());
		result.setId(Utils.getUUID());
		result.setStudentId(student.getId());
		result.setStudent(student);
		result.setExperienceId(experience.getId());
		result.setExperience(experience);
		return result;
	}

}
