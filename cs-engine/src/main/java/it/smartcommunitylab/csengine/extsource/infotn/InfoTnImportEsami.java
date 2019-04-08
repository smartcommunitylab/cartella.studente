package it.smartcommunitylab.csengine.extsource.infotn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import it.smartcommunitylab.csengine.model.Experience;
import it.smartcommunitylab.csengine.model.Institute;
import it.smartcommunitylab.csengine.model.MetaInfo;
import it.smartcommunitylab.csengine.storage.ExperienceRepository;
import it.smartcommunitylab.csengine.storage.InstituteRepository;
import it.smartcommunitylab.csengine.storage.ScheduleUpdateRepository;

@Service
public class InfoTnImportEsami {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportEsami.class);

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

	private String apiKey = Const.API_ESAMI_KEY;

	@Autowired
	private APIUpdateManager apiUpdateManager;
	@Autowired
	ExperienceRepository experienceRepository;
	@Autowired
	InstituteRepository instituteRepository;
	@Autowired
	ScheduleUpdateRepository metaInfoRepository;
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);

	private void updateEsami(MetaInfo metaInfo) throws Exception {

		int total = 0;
		int stored = 0;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;
		int nextYear = metaInfo.getSchoolYear() + 1;
		String schoolYear = metaInfo.getSchoolYear() + "/" + String.valueOf(nextYear).substring(2);

		// read epoc timestamp from db(if exist)
		if (metaInfo.getEpocTimestamp() > 0) {
			url = infoTNAPIUrl + "/esami?schoolYear=" + schoolYear + "&timestamp=" + metaInfo.getEpocTimestamp();
		} else {
			metaInfo.setEpocTimestamp(System.currentTimeMillis()); //set it for the first time.
			url = infoTNAPIUrl + "/esami?schoolYear=" + schoolYear;
		}
		logger.info("start importEsamiFromRESTAPI");

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
				Esame esame = jp.readValueAs(Esame.class);
				logger.info("converting " + esame.getExtId());
				Experience experienceDb = experienceRepository.findByExtId(esame.getOrigin(), esame.getExtId());
				if (experienceDb != null) {
					logger.warn(
							String.format("Experience already exists: %s - %s", esame.getOrigin(), esame.getExtId()));
					continue;
				}
				Experience experience = convertToExperience(esame, schoolYear);
				Institute institute = instituteRepository.findByExtId(esame.getInstituteRef().getOrigin(),
						esame.getInstituteRef().getExtId());
				if (institute != null) {
					experience.getAttributes().put(Const.ATTR_INSTITUTEID, institute.getId());
				}
				experienceRepository.save(experience);
				stored += 1;
				logger.info(String.format("Save Esame: %s - %s - %s", esame.getOrigin(), esame.getExtId(),
						experience.getId()));
			}
			// update time stamp (if all works fine).
			metaInfo.setEpocTimestamp(metaInfo.getEpocTimestamp() + 1);
			metaInfo.setTotalRead(total);
			metaInfo.setTotalStore(stored);
		}

	}

	private Experience convertToExperience(Esame esame, String schoolYear) throws ParseException {
		Experience result = new Experience();
		Date now = new Date();
		result.setCreationDate(now);
		result.setLastUpdate(now);
		result.setOrigin(esame.getOrigin());
		result.setExtId(esame.getExtId());
		result.setId(Utils.getUUID());
		result.setType(Const.EXP_TYPE_EXAM);
		result.getAttributes().put(Const.ATTR_DATEFROM, sdf.parse(esame.getDateFrom()).getTime());
		result.getAttributes().put(Const.ATTR_DATETO, sdf.parse(esame.getDateTo()).getTime());
		result.getAttributes().put(Const.ATTR_EDUCATIONAL, Boolean.TRUE);
		result.getAttributes().put(Const.ATTR_INSTITUTIONAL, Boolean.TRUE);
		result.getAttributes().put(Const.ATTR_SCHOOLYEAR, getSchoolYear(schoolYear));
		result.getAttributes().put(Const.ATTR_QUALIFICATION, esame.getQualification());
		result.getAttributes().put(Const.ATTR_TYPE, esame.getType());
		return result;
	}

	private String getSchoolYear(String annoScolastico) {
		return annoScolastico.replace("/", "-");
	}

	public String importEsamiFromRESTAPI() {
		try {
			List<MetaInfo> savedMetaInfoList = apiUpdateManager.fetchMetaInfoForAPI(apiKey);

			if (savedMetaInfoList == null || savedMetaInfoList.isEmpty()) {
				// call generic method to create metaInfos (apiKey, year?)
				savedMetaInfoList = apiUpdateManager.createMetaInfoForAPI(apiKey, true);
			}

			for (MetaInfo metaInfo : savedMetaInfoList) {
				if (!metaInfo.isBlocked()) {
					updateEsami(metaInfo);
				}
			}

			apiUpdateManager.saveMetaInfoList(apiKey, savedMetaInfoList);

			return "OK";

		} catch (Exception e) {
			logger.error(e.getMessage());
			return e.getMessage();
		}
	}

}

// public String importEsamiFromEmpty() throws Exception {
// logger.info("start importEsamiFromEmpty");
// int total = 0;
// int stored = 0;
// FileReader fileReader = new FileReader(sourceFolder + "FBK_Sessioni esame
// tutte v.01.json");
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
// Esame esame = jp.readValueAs(Esame.class);
// logger.info("converting " + esame.getExtid());
// Experience experienceDb =
// experienceRepository.findByExtId(esame.getOrigin(), esame.getExtid());
// if (experienceDb != null) {
// logger.warn(String.format("Experience already exists: %s - %s",
// esame.getOrigin(),
// esame.getExtid()));
// continue;
// }
// Experience experience = convertToExperience(esame);
// Institute institute =
// instituteRepository.findByExtId(esame.getOrigin_institute(),
// esame.getExtid_institute());
// if (institute != null) {
// experience.getAttributes().put(Const.ATTR_INSTITUTEID,
// institute.getId());
// }
// experienceRepository.save(experience);
// stored += 1;
// logger.info(String.format("Save Esame: %s - %s - %s", esame.getOrigin(),
// esame.getExtid(),
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