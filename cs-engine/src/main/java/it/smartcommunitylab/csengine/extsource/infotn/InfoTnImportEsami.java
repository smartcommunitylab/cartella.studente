package it.smartcommunitylab.csengine.extsource.infotn;

import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.model.Experience;
import it.smartcommunitylab.csengine.model.Institute;
import it.smartcommunitylab.csengine.storage.ExperienceRepository;
import it.smartcommunitylab.csengine.storage.InstituteRepository;

import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

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
public class InfoTnImportEsami {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportEsami.class);
	
	@Autowired
	@Value("${infotn.source.folder}")	
	private String sourceFolder;
	
	@Autowired
	ExperienceRepository experienceRepository;
	
	@Autowired
	InstituteRepository instituteRepository;
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", Locale.ITALY);
	
	public String importEsamiFromEmpty() throws Exception {
		logger.info("start importEsamiFromEmpty");
		int total = 0;
		int stored = 0;
		FileReader fileReader = new FileReader(sourceFolder + "FBK_Sessioni esame tutte v.01.json");
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
						Esame esame = jp.readValueAs(Esame.class);
						logger.info("converting " + esame.getExtid());
						Experience experienceDb = experienceRepository.findByExtId(esame.getOrigin(), 
								esame.getExtid());
						if(experienceDb != null) {
							logger.warn(String.format("Experience already exists: %s - %s", 
									esame.getOrigin(), esame.getExtid()));
							continue;
						}
						Experience experience = convertToExperience(esame);
						Institute institute = instituteRepository.findByExtId(esame.getOrigin_institute(), 
								esame.getExtid_institute());
						if(institute != null) {
							experience.getAttributes().put(Const.ATTR_INSTITUTEID, institute.getId());
						}
						experienceRepository.save(experience);
						stored += 1;
						logger.info(String.format("Save Experience: %s - %s - %s", esame.getOrigin(), 
								esame.getExtid(), experience.getId()));						
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
	
	private Experience convertToExperience(Esame esame) throws ParseException {
		Experience result = new Experience();
		result.setOrigin(esame.getOrigin());
		result.setExtId(esame.getExtid());
		result.setId(Utils.getUUID());
		result.setType(Const.EXP_TYPE_EXAM);
		result.getAttributes().put(Const.ATTR_DATEFROM, sdf.parse(esame.getDatefrom()));
		result.getAttributes().put(Const.ATTR_DATETO, sdf.parse(esame.getDateto()));
		result.getAttributes().put(Const.ATTR_EDUCATIONAL, Boolean.TRUE);
		result.getAttributes().put(Const.ATTR_INSTITUTIONAL, Boolean.TRUE);
		result.getAttributes().put(Const.ATTR_SCHOOLYEAR, getSchoolYear(esame.getSchoolyear()));
		result.getAttributes().put(Const.ATTR_QUALIFICATION, esame.getQualification());
		return result;
	}
	
	private String getSchoolYear(String annoScolastico) {
		return annoScolastico.replace("/", "-");
	}

}
