package it.smartcommunitylab.csengine.extsource.infotn;

import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.model.Certifier;
import it.smartcommunitylab.csengine.model.Experience;
import it.smartcommunitylab.csengine.model.Institute;
import it.smartcommunitylab.csengine.storage.CertifierRepository;
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
public class InfoTnImportStage {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportStage.class);
	
	@Autowired
	@Value("${infotn.source.folder}")	
	private String sourceFolder;
	
	@Autowired
	ExperienceRepository experienceRepository;
	
	@Autowired
	InstituteRepository instituteRepository;
	
	@Autowired
	CertifierRepository certifierRepository;
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", Locale.ITALY);
	
	public String importStageFromEmpty() throws Exception {
		logger.info("start importStageFromEmpty");
		int total = 0;
		int stored = 0;
		FileReader fileReader = new FileReader(sourceFolder + "FBK_STAGE triennio v.02.json");
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
						Stage stage = jp.readValueAs(Stage.class);
						logger.info("converting " + stage.getExtid());
						Experience experienceDb = experienceRepository.findByExtId(stage.getOrigin(), 
								stage.getExtid());
						if(experienceDb != null) {
							logger.warn(String.format("Experience already exists: %s - %s", 
									stage.getOrigin(), stage.getExtid()));
							continue;
						}
						Institute institute = instituteRepository.findByExtId(stage.getOrigin(), 
								stage.getExtid_institute());
						Certifier certifier = certifierRepository.findByExtId(stage.getOrigin_company(), 
								stage.getExtid_company());
						Experience experience = convertToExperience(stage, institute, certifier);
						experienceRepository.save(experience);
						stored += 1;
						logger.info(String.format("Save Stage: %s - %s - %s", stage.getOrigin(), 
								stage.getExtid(), experience.getId()));						
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
	
	private Experience convertToExperience(Stage stage, Institute institute, 
			Certifier certifier) throws ParseException {
		Experience result = new Experience();
		result.setOrigin(stage.getOrigin());
		result.setExtId(stage.getExtid());
		result.setId(Utils.getUUID());
		result.setType(Const.EXP_TYPE_STAGE);
		result.getAttributes().put(Const.ATTR_DATEFROM, sdf.parse(stage.getDatefrom()));
		result.getAttributes().put(Const.ATTR_DATETO, sdf.parse(stage.getDateto()));
		result.getAttributes().put(Const.ATTR_EDUCATIONAL, Boolean.TRUE);
		result.getAttributes().put(Const.ATTR_INSTITUTIONAL, Boolean.TRUE);
		result.getAttributes().put(Const.ATTR_CERTIFIED, Boolean.TRUE);
		result.getAttributes().put(Const.ATTR_SCHOOLYEAR, getSchoolYear(stage.getSchoolyear()));
		result.getAttributes().put(Const.ATTR_TYPE, "Stage");
		result.getAttributes().put(Const.ATTR_DURATION, stage.getDuration());
		result.getAttributes().put(Const.ATTR_LOCATION, getLocation(stage, certifier));
		result.getAttributes().put(Const.ATTR_CONTACT, stage.getTutor());
		result.getAttributes().put(Const.ATTR_TITLE, getTitle(stage, certifier));
		if(institute != null) {
			result.getAttributes().put(Const.ATTR_INSTITUTEID, institute.getId());
		}
		if(certifier != null) {
			result.getAttributes().put(Const.ATTR_CERTIFIERID, certifier.getId());
		}
		return result;
	}
	
	private String getSchoolYear(String annoScolastico) {
		return annoScolastico.replace("/", "-");
	}
	
	private String getTitle(Stage stage, Certifier certifier) {
		String result = null;
		if(Utils.isNotEmpty(stage.getTitle())) {
			result = stage.getTitle();
		} else {
			if(certifier != null) {
				result = certifier.getName();
			}
		}
		return result;
	}
	
	private String getLocation(Stage stage, Certifier certifier) {
		String result = stage.getLocation();
		if(certifier != null) {
			result = certifier.getName() + " - " + result;
		}
		return result;
	}

}
