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
import it.smartcommunitylab.csengine.model.CourseMetaInfo;
import it.smartcommunitylab.csengine.model.MetaInfo;
import it.smartcommunitylab.csengine.storage.CourseMetaInfoRepository;

@Service
public class InfoTnImportCourseMetaInfo {

	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportCourseMetaInfo.class);

	@Value("${infotn.source.folder}")
	private String sourceFolder;
	@Value("${infotn.api.url}")
	private String infoTNAPIUrl;
	@Value("${infotn.api.user}")
	private String user;
	@Value("${infotn.api.pass}")
	private String password;

	private String apiKey = Const.API_COURSE_METAINFO_KEY;

	@Autowired
	private APIUpdateManager apiUpdateManager;
	@Autowired
	CourseMetaInfoRepository courseMetaInfoRepository;

	public void updateCourseMetaInfo(MetaInfo metaInfo) throws Exception {
		logger.info("start importCourseMetaInfoFromRESTAPI");
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		String url = infoTNAPIUrl + "/corsi";
		int stored = 0;
		int total = 0;

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
				CorsoMetaInfo temp = jp.readValueAs(CorsoMetaInfo.class);
				logger.info("processing " + temp.getExtId());
				CourseMetaInfo courseMetaInfo = courseMetaInfoRepository.findByExtId(temp.getOrigin(), temp.getExtId());
				if (courseMetaInfo != null) {
					logger.warn(String.format("CMI already exists: %s - %s", courseMetaInfo.getOrigin(),
							courseMetaInfo.getExtId()));
					continue;
				}
				CourseMetaInfo tobeSaved = convertToCourse(temp);
				courseMetaInfoRepository.save(tobeSaved);
				stored += 1;

			}

			// update time stamp (if all works fine).
			metaInfo.setEpocTimestamp(System.currentTimeMillis() / 1000);
			metaInfo.setTotalRead(total);
			metaInfo.setTotalStore(stored);
		}

	}

	private CourseMetaInfo convertToCourse(CorsoMetaInfo corso) throws ParseException {
		CourseMetaInfo result = new CourseMetaInfo();
		Date now = new Date();
		result.setCreationDate(now);
		result.setLastUpdate(now);
		result.setOrigin(corso.getOrigin());
		result.setExtId(corso.getExtId());
		result.setCourse(corso.getCourse());
		result.setId(Utils.getUUID());
		if (corso.getCodMiur() != null)
			result.setCodMiur(corso.getCodMiur());

		return result;
	}

	public String importCourseMetaInfoFromRESTAPI() {
		try {
			List<MetaInfo> savedMetaInfoList = apiUpdateManager.fetchMetaInfoForAPI(apiKey);

			if (savedMetaInfoList == null || savedMetaInfoList.isEmpty()) {
				// call generic method to create metaInfos (apiKey, year?)
				savedMetaInfoList = apiUpdateManager.createMetaInfoForAPI(apiKey, false);
			}

			for (MetaInfo metaInfo : savedMetaInfoList) {
				if (!metaInfo.isBlocked()) {
					updateCourseMetaInfo(metaInfo);
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
