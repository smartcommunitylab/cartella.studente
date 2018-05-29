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
import it.smartcommunitylab.csengine.model.Certifier;
import it.smartcommunitylab.csengine.model.Experience;
import it.smartcommunitylab.csengine.model.Institute;
import it.smartcommunitylab.csengine.model.MetaInfo;
import it.smartcommunitylab.csengine.storage.CertifierRepository;
import it.smartcommunitylab.csengine.storage.ExperienceRepository;
import it.smartcommunitylab.csengine.storage.InstituteRepository;
import it.smartcommunitylab.csengine.storage.MetaInfoRepository;

@Component
public class InfoTnImportStage {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportStage.class);

	@Autowired
	@Value("${infotn.source.folder}")
	private String sourceFolder;

	@Value("${infotn.api.url}")
	private String infoTNAPIUrl;

	@Value("${infotn.api.user}")
	private String user;

	@Value("${infotn.api.pass}")
	private String password;

	private String metaInfoName = "Stage";
	private String metaInfoIstituzioni = "Istituzioni";

	@Autowired
	ExperienceRepository experienceRepository;

	@Autowired
	InstituteRepository instituteRepository;

	@Autowired
	CertifierRepository certifierRepository;

	@Autowired
	MetaInfoRepository metaInfoRepository;

	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);

	// public String importStageFromEmpty() throws Exception {
	// logger.info("start importStageFromEmpty");
	// int total = 0;
	// int stored = 0;
	// FileReader fileReader = new FileReader(sourceFolder + "FBK_STAGE triennio
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
	// Stage stage = jp.readValueAs(Stage.class);
	// logger.info("converting " + stage.getExtid());
	// Experience experienceDb =
	// experienceRepository.findByExtId(stage.getOrigin(), stage.getExtid());
	// if (experienceDb != null) {
	// logger.warn(String.format("Experience already exists: %s - %s",
	// stage.getOrigin(),
	// stage.getExtid()));
	// continue;
	// }
	// Institute institute = instituteRepository.findByExtId(stage.getOrigin(),
	// stage.getExtid_institute());
	// Certifier certifier =
	// certifierRepository.findByExtId(stage.getOrigin_company(),
	// stage.getExtid_company());
	// Experience experience = convertToExperience(stage, institute, certifier);
	// experienceRepository.save(experience);
	// stored += 1;
	// logger.info(String.format("Save Stage: %s - %s - %s", stage.getOrigin(),
	// stage.getExtid(),
	// experience.getId()));
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

	// Order 5.
	@Scheduled(cron = "0 45 01 * * ?")
	public String importStageFromRESTAPI() throws Exception {
		logger.info("start importIscrizioneCorsiFromRESTAPI");
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
				String url = infoTNAPIUrl + "/stage?schoolYear=" + schoolYear + "&timestamp="
						+ metaInfo.getEpocTimestamp();
				try {

					importStageUsingRESTAPI(url, schoolYear, metaInfo);
					return metaInfo.getTotalStore() + "/" + metaInfo.getTotalRead();

				} catch (Exception e) {
					return e.getMessage();
				}

			} else {
				metaInfo = new MetaInfo();
				metaInfo.setName(metaInfoName);
				try {

					for (Map.Entry<String, String> entry : schoolYears.entrySet()) {
						String url = infoTNAPIUrl + "/stage?schoolYear=" + entry.getValue();
						importStageUsingRESTAPI(url, entry.getValue(), metaInfo);
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

	private void importStageUsingRESTAPI(String url, String schoolYear, MetaInfo metaInfo) throws Exception {
		logger.info("start importIscirzioneCorsiUsingRESTAPI for year " + schoolYear);
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

				Stage stage = jp.readValueAs(Stage.class);
				logger.info("converting " + stage.getExtId());
				Experience experienceDb = experienceRepository.findByExtId(stage.getOrigin(), stage.getExtId());
				if (experienceDb != null) {
					logger.warn(
							String.format("Experience already exists: %s - %s", stage.getOrigin(), stage.getExtId()));
					continue;
				}
				// Institute institute = instituteRepository.findByExtId(stage.getOrigin(), stage.getExtid_institute());
				Certifier certifier = certifierRepository.findByExtId(stage.getCompanyRef().getOrigin(),
						stage.getCompanyRef().getExtId());
				Experience experience = convertToExperience(stage, null, certifier, schoolYear);
				experienceRepository.save(experience);
				stored += 1;
				logger.info(String.format("Save Stage: %s - %s - %s", stage.getOrigin(), stage.getExtId(),
						experience.getId()));
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

	private Experience convertToExperience(Stage stage, Institute institute, Certifier certifier, String schoolYear)
			throws ParseException {
		Experience result = new Experience();
		result.setOrigin(stage.getOrigin());
		result.setExtId(stage.getExtId());
		result.setId(Utils.getUUID());
		result.setType(Const.EXP_TYPE_STAGE);
		result.getAttributes().put(Const.ATTR_DATEFROM, sdf.parse(stage.getDateFrom()).getTime());
		result.getAttributes().put(Const.ATTR_DATETO, sdf.parse(stage.getDateTo()).getTime());
		result.getAttributes().put(Const.ATTR_EDUCATIONAL, Boolean.TRUE);
		result.getAttributes().put(Const.ATTR_INSTITUTIONAL, Boolean.TRUE);
		result.getAttributes().put(Const.ATTR_CERTIFIED, Boolean.TRUE);
		result.getAttributes().put(Const.ATTR_SCHOOLYEAR, getSchoolYear(schoolYear));
		result.getAttributes().put(Const.ATTR_TYPE, "Stage");
		result.getAttributes().put(Const.ATTR_DURATION, stage.getDuration());
		result.getAttributes().put(Const.ATTR_LOCATION, getLocation(stage, certifier));
		result.getAttributes().put(Const.ATTR_CONTACT, stage.getTutor());
		result.getAttributes().put(Const.ATTR_TITLE, getTitle(stage, certifier));
		result.getAttributes().put(Const.COURSE_REF_ORIGIN, stage.getCourseRef().getOrigin());
		result.getAttributes().put(Const.COURSE_REF_EXTID, stage.getCourseRef().getExtId());
		if (institute != null) {
			result.getAttributes().put(Const.ATTR_INSTITUTEID, institute.getId());
		}
		if (certifier != null) {
			result.getAttributes().put(Const.ATTR_CERTIFIERID, certifier.getId());
		}
		return result;
	}

	private String getSchoolYear(String annoScolastico) {
		return annoScolastico.replace("/", "-");
	}

	private String getTitle(Stage stage, Certifier certifier) {
		String result = null;
		if (Utils.isNotEmpty(stage.getTitle())) {
			result = stage.getTitle();
		} else {
			if (certifier != null) {
				result = certifier.getName();
			}
		}
		return result;
	}

	private String getLocation(Stage stage, Certifier certifier) {
		String result = stage.getLocation();
		if (certifier != null) {
			result = certifier.getName() + " - " + result;
		}
		return result;
	}

}
