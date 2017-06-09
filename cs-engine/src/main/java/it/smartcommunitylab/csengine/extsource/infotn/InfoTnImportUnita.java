package it.smartcommunitylab.csengine.extsource.infotn;

import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.model.Institute;
import it.smartcommunitylab.csengine.model.TeachingUnit;
import it.smartcommunitylab.csengine.storage.InstituteRepository;
import it.smartcommunitylab.csengine.storage.TeachingUnitRepository;

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
public class InfoTnImportUnita {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportUnita.class);
	
	@Autowired
	@Value("${infotn.source.folder}")	
	private String sourceFolder;
	
	@Autowired
	InstituteRepository instituteRepository;
	
	@Autowired
	TeachingUnitRepository teachingUnitRepository;
	
	public String importUnitaFromEmpty() throws Exception {
		logger.info("start importUnitaFromEmpty");
		int total = 0;
		int stored = 0;
		FileReader fileReader = new FileReader(sourceFolder + "NEW_FBK_Unità scolastiche senza materne.json");
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
						Unita unita = jp.readValueAs(Unita.class);
						logger.info("converting " + unita.getExtid());
						TeachingUnit teachingUnitDb = teachingUnitRepository.findByExtId(unita.getOrigin(), 
								unita.getExtid());
						if(teachingUnitDb != null) {
							logger.warn(String.format("TU already exists: %s - %s", 
									unita.getOrigin(), unita.getExtid()));
							continue;
						}
						Institute instituteDb = instituteRepository.findByExtId(unita.getOrigin_institute(), 
								unita.getExtid_institute());
						if(instituteDb == null) {
							logger.warn(String.format("Institute not found: %s - %s", 
									unita.getOrigin_institute(), unita.getExtid_institute()));
							continue;
						}
						TeachingUnit teachingUnit = convertToTeachingUnit(unita);
						teachingUnit.setInstituteId(instituteDb.getId());
						teachingUnitRepository.save(teachingUnit);
						stored += 1;
						logger.info(String.format("Save TeachingUnit: %s - %s - %s", unita.getOrigin(), 
								unita.getExtid(), teachingUnit.getId()));
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
	
	private TeachingUnit convertToTeachingUnit(Unita unita) {
		TeachingUnit result = new TeachingUnit();
		result.setOrigin(unita.getOrigin());
		result.setExtId(unita.getExtid());
		result.setId(Utils.getUUID());
		result.setName(unita.getName());
		result.setDescription(unita.getDescription());
		result.setAddress(unita.getAddress());
		return result;
	}
}
