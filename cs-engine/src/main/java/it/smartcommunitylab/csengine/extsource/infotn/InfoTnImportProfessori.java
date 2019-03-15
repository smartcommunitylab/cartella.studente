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
import it.smartcommunitylab.csengine.model.MetaInfo;
import it.smartcommunitylab.csengine.model.Professor;
import it.smartcommunitylab.csengine.storage.ProfessoriRepository;
import it.smartcommunitylab.csengine.storage.ScheduleUpdateRepository;

@Service
public class InfoTnImportProfessori {

	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportProfessori.class);

	@Autowired
	@Value("${infotn.source.folder}")
	private String sourceFolder;

	@Value("${infotn.api.url}")
	private String infoTNAPIUrl;

	@Value("${infotn.api.user}")
	private String user;

	@Value("${infotn.api.pass}")
	private String password;

	private String apiKey = Const.API_PROFESSORI_KEY;

	@Autowired
	private APIUpdateManager apiUpdateManager;
	@Autowired
	private ProfessoriRepository professoriRepository;

	@Autowired
	ScheduleUpdateRepository metaInfoRepository;

	public void updateProfessori(MetaInfo metaInfo) throws Exception {
		logger.info("start importProfessoriFromRESTAPI");
		int total = 0;
		int stored = 0;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;

		if (metaInfo.getEpocTimestamp() > 0) {
			url = infoTNAPIUrl + "/professori?timestamp=" + metaInfo.getEpocTimestamp();
		} else {
			url = infoTNAPIUrl + "/professori";
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
				Professor professorExt = jp.readValueAs(Professor.class);
				Professor professoriDb = professoriRepository.findByExtId(professorExt.getOrigin(),
						professorExt.getExtId());
				if (professoriDb != null) {
					logger.warn(String.format("Professori already exists: %s - %s", professorExt.getOrigin(),
							professorExt.getExtId()));
					continue;
				}
				logger.info("converting " + professorExt.getExtId());
				Professor professor = convertToProfessor(professorExt);
				professoriRepository.save(professor);
				stored += 1;
				logger.info(String.format("Save Professori: %s - %s - %s", professorExt.getOrigin(),
						professorExt.getExtId(), professorExt.getId()));
			}

			// update time stamp (if all works fine).
			metaInfo.setEpocTimestamp(System.currentTimeMillis());
			metaInfo.setTotalRead(total);
			metaInfo.setTotalStore(stored);

		}

	}

	private Professor convertToProfessor(Professor professorExt) {
		Professor result = new Professor();
		Date now = new Date();
		result.setCreationDate(now);
		result.setLastUpdate(now);
		result.setId(Utils.getUUID());
		result.setCf(professorExt.getCf());
		if (professorExt.getEmail() != null && !professorExt.getEmail().isEmpty())
			result.setEmail(professorExt.getEmail().trim());
		result.setName(professorExt.getName());
		result.setSurname(professorExt.getSurname());
		result.setOrigin(professorExt.getOrigin());
		result.setExtId(professorExt.getExtId());
		return result;
	}

	public String importProfessoriFromRESTAPI() {
		try {
			List<MetaInfo> savedMetaInfoList = apiUpdateManager.fetchMetaInfoForAPI(apiKey);

			if (savedMetaInfoList == null || savedMetaInfoList.isEmpty()) {
				// call generic method to create metaInfos (apiKey, year?)
				savedMetaInfoList = apiUpdateManager.createMetaInfoForAPI(apiKey, false);
			}

			for (MetaInfo metaInfo : savedMetaInfoList) {
				if (!metaInfo.isBlocked()) {
					updateProfessori(metaInfo);
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
