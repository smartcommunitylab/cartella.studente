package it.smartcommunitylab.csengine.extsource.infotn;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("${infotn.starting.year}")
	private int startingYear;

	@Value("${infotn.api.user}")
	private String user;

	@Value("${infotn.api.pass}")
	private String password;

	private String metaInfoName = "Istituzioni";

	@Autowired
	private InstituteRepository instituteRepository;

	@Autowired
	private MetaInfoRepository metaInfoRepository;
	
	@Autowired
	private InfoTnSchools infoTnSchools;

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
		String response = HTTPUtils.get(url, null, user, password);
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

			// save school years for import purpose, used as parameter in REST
			// APIs.
			Map<String, String> schoolYears = new HashMap<String, String>();
			if (startingYear < 0) {
				startingYear = 2017; // default year in case missing in
										// configuration.
			}
			for (int i = startingYear; i < Calendar.getInstance().get(Calendar.YEAR); i++) {
				int nextYear = i + 1;
				String schoolYear = i + "/" + String.valueOf(nextYear).substring(2);
				schoolYears.put(String.valueOf(i), schoolYear);
			}
			metaInfo.setSchoolYears(schoolYears);
			metaInfoRepository.save(metaInfo);

		}

		return stored + "/" + total + "(" + metaInfo.getEpocTimestamp() + ")";
	}

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
		
		Scuola scuola = infoTnSchools.getScuola(istituzione.getExtId());
		if(scuola != null) {
			Double[] geocode = new Double[2];
			try {
				geocode[0] = Double.valueOf(scuola.getLongitude());
				geocode[1] = Double.valueOf(scuola.getLatitude());
				result.setGeocode(geocode);
			} catch (Exception e) {
				logger.warn("error converting geocode:" + e.getMessage());
			}
		}
		
		return result;
	}

}