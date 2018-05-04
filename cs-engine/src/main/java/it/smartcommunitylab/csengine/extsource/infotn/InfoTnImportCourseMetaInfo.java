package it.smartcommunitylab.csengine.extsource.infotn;

import java.text.ParseException;

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
import it.smartcommunitylab.csengine.model.Course;
import it.smartcommunitylab.csengine.model.CourseMetaInfo;
import it.smartcommunitylab.csengine.storage.CourseMetaInfoRepository;

@Component
public class InfoTnImportCourseMetaInfo {

	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportCourseMetaInfo.class);

	@Autowired
	@Value("${infotn.source.folder}")
	private String sourceFolder;

	@Value("${infotn.api.url}")
	private String infoTNAPIUrl;

	@Value("${infotn.api.user}")
	private String user;

	@Value("${infotn.api.pass}")
	private String password;

	@Autowired
	CourseMetaInfoRepository courseMetaInfoRepository;

	// Order 2.
	@Scheduled(cron = "0 20 23 * * ?")
	public String importCourseMetaInfoFromRESTAPI() throws Exception {
		logger.info("start importCourseMetaInfoFromRESTAPI");
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url = infoTNAPIUrl + "/corsi";
		int stored = 0;

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
		}

		return "stored (" + stored + ")";

	}
	
	private CourseMetaInfo convertToCourse(CorsoMetaInfo corso) throws ParseException {
		CourseMetaInfo result = new CourseMetaInfo();
		result.setOrigin(corso.getOrigin());
		result.setExtId(corso.getExtId());
		result.setCourse(corso.getCourse());
		result.setId(Utils.getUUID());
		return result;
	}

}
