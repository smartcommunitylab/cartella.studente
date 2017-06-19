package it.smartcommunitylab.csengine.extsource.infotn;

import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.model.Institute;
import it.smartcommunitylab.csengine.storage.InstituteRepository;

import java.io.FileReader;

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

@Component
public class InfoTnImportIstituzioni {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportIstituzioni.class);
	
	@Autowired
	@Value("${infotn.source.folder}")	
	private String sourceFolder;
	
	@Autowired
	InstituteRepository instituteRepository;
	
	public String importIstituzioniFromEmpty() throws Exception {
		logger.info("start importIstituzioniFromEmpty");
		int total = 0;
		int stored = 0;
		FileReader fileReader = new FileReader(sourceFolder + "FBK_Istituzioni v.01.json");
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JsonFactory jsonFactory = new JsonFactory();
		jsonFactory.setCodec(objectMapper);
		JsonParser jp = jsonFactory.createParser(fileReader);
		JsonToken current;
		current = jp.nextToken();
		if (current != JsonToken.START_OBJECT) {
      logger.error("Error: root should be object: quiting.");
      return "Error: root should be object: quiting.";
    }
		while (jp.nextToken() != JsonToken.END_OBJECT) {
			String fieldName = jp.getCurrentName();
			current = jp.nextToken();
			if (fieldName.equals("items")) {
				if (current == JsonToken.START_ARRAY) {
					while (jp.nextToken() != JsonToken.END_ARRAY) {
						total += 1;
						Istituzione istituzione = jp.readValueAs(Istituzione.class);
						logger.info("converting " + istituzione.getExtid());
						Institute instituteDb = instituteRepository.findByExtId(istituzione.getOrigin(), 
								istituzione.getExtid());
						if(instituteDb != null) {
							logger.warn(String.format("Institute already exists: %s - %s", 
									istituzione.getOrigin(), istituzione.getExtid()));
							continue;
						}
						Institute institute = convertToInstitute(istituzione);
						instituteRepository.save(institute);
						stored += 1;
						logger.info(String.format("Save Institute: %s - %s - %s", istituzione.getOrigin(), 
								istituzione.getExtid(), institute.getId()));						
					}
				} else {
          logger.warn("Error: records should be an array: skipping.");
          jp.skipChildren();
        }
			} else {
        logger.warn("Unprocessed property: " + fieldName);
        jp.skipChildren();
      }
		}
		return stored + "/" + total;
	}
	
	private Institute convertToInstitute(Istituzione istituzione) {
		Institute result = new Institute();
		result.setOrigin(istituzione.getOrigin());
		result.setExtId(istituzione.getExtid());
		result.setId(Utils.getUUID());
		result.setName(istituzione.getName());
		result.setDescription(istituzione.getDescription());
		result.setAddress(istituzione.getAddress());
		result.setPhone(istituzione.getPhone());
		result.setPec(istituzione.getPec());
		result.setEmail(istituzione.getEmailsegreteria());
		return result;
	}
}
