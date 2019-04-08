package it.smartcommunitylab.csengine.extsource.infotn;

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
import it.smartcommunitylab.csengine.model.Institute;
import it.smartcommunitylab.csengine.model.MetaInfo;
import it.smartcommunitylab.csengine.storage.InstituteRepository;

@Service
public class InfoTnImportIstituzioni {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportIstituzioni.class);

	@Autowired
	private APIUpdateManager apiUpdateManager;

	@Autowired
	private InstituteRepository instituteRepository;

	@Autowired
	private InfoTnSchools infoTnSchools;

	@Value("${infotn.api.url}")
	private String infoTNAPIUrl;

	@Value("${infotn.api.user}")
	private String user;

	@Value("${infotn.api.pass}")
	private String password;

	private String apiKey = Const.API_ISTITUTI_KEY;

	private void updateIstitute(MetaInfo metaInfo) throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;
		int total = 0;
		int stored = 0;

		if (metaInfo.getEpocTimestamp() > 0) {
			url = infoTNAPIUrl + "/istituti?timestamp=" + metaInfo.getEpocTimestamp();
		} else {
			metaInfo.setEpocTimestamp(System.currentTimeMillis()); //set it for the first time.
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
			metaInfo.setEpocTimestamp(metaInfo.getEpocTimestamp() + 1);
			metaInfo.setTotalRead(total);
			metaInfo.setTotalStore(stored);

		}

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
		Date now = new Date();
		result.setCreationDate(now);
		result.setLastUpdate(now);

		Scuola scuola = infoTnSchools.getScuola(istituzione.getExtId());
		if (scuola != null) {
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

	public String importIstituzioniFromRESTAPI() {
		// chedere a APIUpdateManager i propri metadati
		// - scorrere i metadati
		// - se blocked è false:
		// - se richiesto setta schoolYear =
		// "duedigit(MetaInfo.schoolYear)/duedigit((MetaInfo.schoolYear + 1))"
		// // here i need to put year4d/year2d
		// - se richiesto e se epocTimestamp > 0 usare epocTimestamp
		// - invoca API
		// - aggiorna epocTimestamp di MetaInfo (metodo in APIUpdateManager)
		try {
			List<MetaInfo> savedMetaInfoList = apiUpdateManager.fetchMetaInfoForAPI(apiKey);

			if (savedMetaInfoList == null || savedMetaInfoList.isEmpty()) {
				// call generic method to create metaInfos (apiKey, year?)
				savedMetaInfoList = apiUpdateManager.createMetaInfoForAPI(apiKey, false);
			}

			for (MetaInfo metaInfo : savedMetaInfoList) {
				if (!metaInfo.isBlocked()) {
					updateIstitute(metaInfo);
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