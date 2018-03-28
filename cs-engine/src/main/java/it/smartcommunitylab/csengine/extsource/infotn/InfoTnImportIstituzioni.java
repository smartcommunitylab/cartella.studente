package it.smartcommunitylab.csengine.extsource.infotn;

import java.util.Calendar;
import java.util.HashMap;
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

import it.smartcommunitylab.csengine.common.HTTPUtils;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.model.Institute;
import it.smartcommunitylab.csengine.model.MetaInfo;
import it.smartcommunitylab.csengine.storage.InstituteRepository;
import it.smartcommunitylab.csengine.storage.MetaInfoRepository;

@Component
public class InfoTnImportIstituzioni {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportIstituzioni.class);

	@Autowired
	@Value("${infotn.source.folder}")
	private String sourceFolder;

	@Value("${infotn.api.url}")
	private String infoTNAPIUrl;

	private String metaInfoName = "Istituzioni";

	@Autowired
	InstituteRepository instituteRepository;

	@Autowired
	MetaInfoRepository metaInfoRepository;

//	 Order 1.
	@Scheduled(cron = "0 15 23 * * ?")
	public String importIstituzioniFromRESTAPI() throws Exception {
		logger.info("start importIstituzioniFromRESTAPI");
		int total = 0;
		int stored = 0;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;

		// read epoc timestamp from db(if exist)
		MetaInfo metaInfo = metaInfoRepository.findOne(metaInfoName);
		if (metaInfo != null) {
			url = infoTNAPIUrl + "/istituti?timestamp=" + metaInfo.getEpocTimestamp();
		} else {
			metaInfo = new MetaInfo();
			metaInfo.setName(metaInfoName);
			url = infoTNAPIUrl + "/istituti";
		}
		// call api.
		String response = HTTPUtils.get(url, null, null, null);
		if (response != null && !response.isEmpty()) {
			JsonFactory jsonFactory = new JsonFactory();
			jsonFactory.setCodec(objectMapper);
			JsonParser jp = jsonFactory.createParser(response);
			JsonToken current;
			current = jp.nextToken();
			if (current != JsonToken.START_ARRAY) {
				logger.error("Error: root should be array: quiting.");
				return "Error: root should be array: quiting.";
			}

			while (jp.nextToken() != JsonToken.END_ARRAY) {
				total += 1;
				Istituzione istituzione = jp.readValueAs(Istituzione.class);
				logger.info("converting " + istituzione.getExtId());
				Institute instituteDb = instituteRepository.findByExtId(istituzione.getOrigin(),
						istituzione.getExtId());
				if (instituteDb != null) {
					logger.warn(String.format("Institute already exists: %s - %s", istituzione.getOrigin(),
							istituzione.getExtId()));
					continue;
				}
				Institute institute = convertToInstitute(istituzione);
				instituteRepository.save(institute);
				stored += 1;
				logger.info(String.format("Save Institute: %s - %s - %s", istituzione.getOrigin(),
						istituzione.getExtId(), institute.getId()));
			}
			// update time stamp (if all works fine).
			metaInfo.setEpocTimestamp(System.currentTimeMillis() / 1000);
			metaInfo.setTotalRead(total);
			metaInfo.setTotalStore(stored);

			Map<String, String> schoolYears = new HashMap<String, String>();
			for (int i = 1999; i <= Calendar.getInstance().get(Calendar.YEAR); i++) {
				int nextYear = i + 1;
				String schoolYear = i + "/" + String.valueOf(nextYear).substring(2);
				schoolYears.put(String.valueOf(i), schoolYear);
				metaInfo.setSchoolYears(schoolYears);
			}
			metaInfoRepository.save(metaInfo);

		}

		return stored + "/" + total + "(" + metaInfo.getEpocTimestamp() + ")";
	}

	// public String importIstituzioniFromEmpty() throws Exception {
	// logger.info("start importIstituzioniFromEmpty");
	// int total = 0;
	// int stored = 0;
	// FileReader fileReader = new FileReader(sourceFolder + "FBK_Istituzioni
	// v.01.json");
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
	// Istituzione istituzione = jp.readValueAs(Istituzione.class);
	// logger.info("converting " + istituzione.getExtId());
	// Institute instituteDb =
	// instituteRepository.findByExtId(istituzione.getOrigin(),
	// istituzione.getExtId());
	// if (instituteDb != null) {
	// logger.warn(String.format("Institute already exists: %s - %s",
	// istituzione.getOrigin(),
	// istituzione.getExtId()));
	// continue;
	// }
	// Institute institute = convertToInstitute(istituzione);
	// instituteRepository.save(institute);
	// stored += 1;
	// logger.info(String.format("Save Institute: %s - %s - %s",
	// istituzione.getOrigin(),
	// istituzione.getExtId(), institute.getId()));
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

	private Institute convertToInstitute(Istituzione istituzione) {
		Institute result = new Institute();
		result.setOrigin(istituzione.getOrigin());
		result.setExtId(istituzione.getExtId());
		result.setId(Utils.getUUID());
		result.setName(istituzione.getName());
		result.setDescription(istituzione.getDescription());
		result.setAddress(istituzione.getAddress());
		result.setPhone(istituzione.getPhone());
		result.setPec(istituzione.getPec());
		result.setEmail(istituzione.getEmail());
		return result;
	}

}