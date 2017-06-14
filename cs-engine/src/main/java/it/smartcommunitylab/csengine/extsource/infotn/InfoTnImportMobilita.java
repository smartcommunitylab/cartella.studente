package it.smartcommunitylab.csengine.extsource.infotn;

import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.model.Experience;
import it.smartcommunitylab.csengine.model.Student;
import it.smartcommunitylab.csengine.model.StudentExperience;
import it.smartcommunitylab.csengine.storage.ExperienceRepository;
import it.smartcommunitylab.csengine.storage.InstituteRepository;
import it.smartcommunitylab.csengine.storage.StudentExperienceRepository;
import it.smartcommunitylab.csengine.storage.StudentRepository;

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
public class InfoTnImportMobilita {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportMobilita.class);
	
	@Autowired
	@Value("${infotn.source.folder}")	
	private String sourceFolder;
	
	@Autowired
	ExperienceRepository experienceRepository;
	
	@Autowired
	InstituteRepository instituteRepository;
	
	@Autowired
	StudentExperienceRepository studentExperienceRepository;
	
	@Autowired
	StudentRepository studentRepository;
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", Locale.ITALY);
	
	public String importMobilitaFromEmpty() throws Exception {
		logger.info("start importMobilitaFromEmpty");
		int total = 0;
		int stored = 0;
		FileReader fileReader = new FileReader(sourceFolder + "FBK_MOBILITA ESTERO triennio v.02.json");
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
						Mobilita mob = jp.readValueAs(Mobilita.class);
						logger.info("converting " + mob.getExtid());
						Experience experienceDb = experienceRepository.findByExtId(
								mob.getOrigin(), mob.getExtid());
						if(experienceDb != null) {
							logger.warn(String.format("Experience already exists: %s - %s", 
									mob.getOrigin(), mob.getExtid()));
							continue;
						}
						StudentExperience studentExperienceDb = studentExperienceRepository.findByExtId(
								mob.getOrigin(), mob.getExtid());
						if(studentExperienceDb != null) {
							logger.warn(String.format("StudentExperience already exists: %s - %s", 
									mob.getOrigin(), mob.getExtid()));
							continue;
						}
						Student student = studentRepository.findByExtId(mob.getOrigin_student(), 
								mob.getExtid_student());
						if(student == null) {
							logger.warn(String.format("Student not found: %s - %s", 
									mob.getOrigin_student(), mob.getExtid_student()));
							continue;
						}
						Experience experience = convertToExperience(mob);
						experience = experienceRepository.save(experience);
						StudentExperience studentExperience = convertToStudentExperience(mob, 
								experience, student);
						studentExperienceRepository.save(studentExperience);
						stored += 1;
						logger.info(String.format("Save Experience: %s - %s - %s", mob.getOrigin(), 
								mob.getExtid(), studentExperience.getId()));						
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
	
	private Experience convertToExperience(Mobilita mob) throws ParseException {
		Experience result = new Experience();
		result.setOrigin(mob.getOrigin());
		result.setExtId(mob.getExtid());
		result.setId(Utils.getUUID());
		result.setType(Const.EXP_TYPE_MOBILITY);
		result.getAttributes().put(Const.ATTR_DATEFROM, sdf.parse(mob.getDatefrom()));
		result.getAttributes().put(Const.ATTR_DATETO, sdf.parse(mob.getDateto()));
		result.getAttributes().put(Const.ATTR_EDUCATIONAL, Boolean.TRUE);
		result.getAttributes().put(Const.ATTR_INSTITUTIONAL, Boolean.TRUE);
		result.getAttributes().put(Const.ATTR_CERTIFIED, Boolean.TRUE);
		result.getAttributes().put(Const.ATTR_TYPE, mob.getLanguage());
		result.getAttributes().put(Const.ATTR_TITLE, mob.getDescription());
		result.getAttributes().put(Const.ATTR_LOCATION, mob.getLocation());
		result.getAttributes().put(Const.ATTR_LANG, mob.getLanguage());
		return result;
	}
	
	private StudentExperience convertToStudentExperience(Mobilita mob, 
			Experience experience, Student student) throws ParseException {
		StudentExperience result = new StudentExperience();
		result.setOrigin(mob.getOrigin());
		result.setExtId(mob.getExtid());
		result.setId(Utils.getUUID());
		result.setStudentId(student.getId());
		result.setStudent(student);
		result.setExperienceId(experience.getId());
		result.setExperience(experience);
		return result;
	}
	
}
