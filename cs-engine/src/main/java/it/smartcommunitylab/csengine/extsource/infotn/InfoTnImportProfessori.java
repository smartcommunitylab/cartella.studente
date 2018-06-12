package it.smartcommunitylab.csengine.extsource.infotn;

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
import it.smartcommunitylab.csengine.model.Professor;
import it.smartcommunitylab.csengine.storage.MetaInfoRepository;
import it.smartcommunitylab.csengine.storage.ProfessoriRepository;

@Component
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

	private String metaInfoName = "Professori";

	@Autowired
	private ProfessoriRepository professoriRepository;

	@Autowired
	MetaInfoRepository metaInfoRepository;

	public String importProfessoriFromRESTAPI() throws Exception {
		logger.info("start importProfessoriFromRESTAPI");
		int total = 0;
		int stored = 0;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;

		// read epoc timestamp from db(if exist)
		MetaInfo metaInfo = metaInfoRepository.findOne(metaInfoName);
		if (metaInfo != null) {
			url = infoTNAPIUrl + "/professori?timestamp=" + metaInfo.getEpocTimestamp();
		} else {
			metaInfo = new MetaInfo();
			metaInfo.setName(metaInfoName);
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
				return "Error: root should be array: quiting.";
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
			metaInfo.setEpocTimestamp(System.currentTimeMillis() / 1000);
			metaInfo.setTotalRead(total);
			metaInfo.setTotalStore(stored);
			metaInfoRepository.save(metaInfo);

		}

		return stored + "/" + total + "(" + metaInfo.getEpocTimestamp() + ")";
	}

	private Professor convertToProfessor(Professor professorExt) {
		Professor result = new Professor();
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

}
