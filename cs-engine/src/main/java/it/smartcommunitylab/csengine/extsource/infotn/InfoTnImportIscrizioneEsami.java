package it.smartcommunitylab.csengine.extsource.infotn;

import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.model.Certificate;
import it.smartcommunitylab.csengine.model.Experience;
import it.smartcommunitylab.csengine.model.Student;
import it.smartcommunitylab.csengine.model.StudentExperience;
import it.smartcommunitylab.csengine.storage.ExperienceRepository;
import it.smartcommunitylab.csengine.storage.InstituteRepository;
import it.smartcommunitylab.csengine.storage.StudentExperienceRepository;
import it.smartcommunitylab.csengine.storage.StudentRepository;

import java.io.FileReader;
import java.text.ParseException;

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
public class InfoTnImportIscrizioneEsami {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportIscrizioneEsami.class);
	
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
	
	public String importIscrizioneEsamiFromEmpty() throws Exception {
		logger.info("start importIscrizioneEsamiFromEmpty");
		int total = 0;
		int stored = 0;
		FileReader fileReader = new FileReader(sourceFolder + "FBK_iscrizioniesami triennio v.02.json");
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
						IscrizioneEsame iscrizione = jp.readValueAs(IscrizioneEsame.class);
						logger.info("converting " + iscrizione.getExtid());
						StudentExperience experienceDb = studentExperienceRepository.findByExtId(iscrizione.getOrigin(), 
								iscrizione.getExtid());
						if(experienceDb != null) {
							logger.warn(String.format("Experience already exists: %s - %s", 
									iscrizione.getOrigin(), iscrizione.getExtid()));
							continue;
						}
						Student student = studentRepository.findByExtId(iscrizione.getOrigin_student(), 
								iscrizione.getExtid_studente());
						if(student == null) {
							logger.warn(String.format("Student not found: %s - %s", 
									iscrizione.getOrigin_student(), iscrizione.getExtid_studente()));
							continue;
						}
						Experience experience = experienceRepository.findByExtId(
								iscrizione.getOrigin_exam(), iscrizione.getExtid_exam());
						if(experience ==  null) {
							logger.warn(String.format("Experience not found: %s - %s", 
									iscrizione.getOrigin_exam(), iscrizione.getExtid_exam()));
							continue;
						}
						StudentExperience studentExperience = convertToExperience(iscrizione, 
								experience, student);
						studentExperienceRepository.save(studentExperience);
						stored += 1;
						logger.info(String.format("Save Experience: %s - %s - %s", iscrizione.getOrigin(), 
								iscrizione.getExtid(), studentExperience.getId()));						
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
	
	private StudentExperience convertToExperience(IscrizioneEsame iscrizione, 
			Experience experience, Student student) 
			throws ParseException {
		StudentExperience result = new StudentExperience();
		result.setOrigin(iscrizione.getOrigin());
		result.setExtId(iscrizione.getExtid());
		result.setId(Utils.getUUID());
		
		experience.getAttributes().put(Const.ATTR_HONOUR, getHonour(iscrizione));
		experience.getAttributes().put(Const.ATTR_GRADE, getJudgement(iscrizione));
		experience.getAttributes().put(Const.ATTR_RESULT, iscrizione.getResult());
		experience.getAttributes().put(Const.ATTR_EXTERNALCANDIDATE, getExternalCandidate(iscrizione));
		
		result.setStudentId(student.getId());
		result.setStudent(student);
		result.setExperienceId(experience.getId());
		result.setExperience(experience);
		
		Certificate certificate = new Certificate();
		certificate.setStudentId(student.getId());
		certificate.setExperienceId(experience.getId());
		certificate.getAttributes().put(Const.ATTR_JUDGEMENT, getJudgement(iscrizione));
		result.setCertificate(certificate);
		
		return result;
	}
	
	private boolean getHonour(IscrizioneEsame iscrizione) {
		boolean result = false;
		if(Utils.isNotEmpty(iscrizione.getHonour())) {
			result = iscrizione.getHonour().equals("1");
		}
		return result;
	}
	
	private boolean getExternalCandidate(IscrizioneEsame iscrizione) {
		boolean result = false;
		if(Utils.isNotEmpty(iscrizione.getExternalcandidate())) {
			result = iscrizione.getExternalcandidate().equals("1");
		}
		return result;
	}
	
	private String getJudgement(IscrizioneEsame iscrizione) {
		String result = null;
		if(Utils.isNotEmpty(iscrizione.getGrade())) {
			result = iscrizione.getGrade();
		} else if(Utils.isNotEmpty(iscrizione.getJudgement())) {
			result = iscrizione.getJudgement();
		}
		return result;
	}
	
}
