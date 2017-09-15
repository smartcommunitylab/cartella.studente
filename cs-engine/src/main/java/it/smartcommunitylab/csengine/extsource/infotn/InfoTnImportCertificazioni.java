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
public class InfoTnImportCertificazioni {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportCertificazioni.class);
	
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
	
	public String importCertificazioniFromEmpty() throws Exception {
		logger.info("start importCertificazioniFromEmpty");
		int total = 0;
		int stored = 0;
		FileReader fileReader = new FileReader(sourceFolder + "FBK_Certificazioni V.03.json");
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
						Certificazione cert = jp.readValueAs(Certificazione.class);
						logger.info("converting " + cert.getExtid());
						Experience experienceDb = experienceRepository.findByExtId(
								cert.getOrigin(), cert.getExtid());
						if(experienceDb != null) {
							logger.warn(String.format("Experience already exists: %s - %s", 
									cert.getOrigin(), cert.getExtid()));
							continue;
						}
						StudentExperience studentExperienceDb = studentExperienceRepository.findByExtId(
								cert.getOrigin(), cert.getExtid());
						if(studentExperienceDb != null) {
							logger.warn(String.format("StudentExperience already exists: %s - %s", 
									cert.getOrigin(), cert.getExtid()));
							continue;
						}
						Student student = studentRepository.findByExtId(cert.getOrigin_student(), 
								cert.getExtid_student());
						if(student == null) {
							logger.warn(String.format("Student not found: %s - %s", 
									cert.getOrigin_student(), cert.getExtid_student()));
							continue;
						}
						Experience experience = convertToExperience(cert);
						experience = experienceRepository.save(experience);
						StudentExperience studentExperience = convertToStudentExperience(cert, 
								experience, student);
						studentExperienceRepository.save(studentExperience);
						stored += 1;
						logger.info(String.format("Save Experience: %s - %s - %s", cert.getOrigin(), 
								cert.getExtid(), studentExperience.getId()));						
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
	
	private Experience convertToExperience(Certificazione cert) throws ParseException {
		Experience result = new Experience();
		result.setOrigin(cert.getOrigin());
		result.setExtId(cert.getExtid());
		result.setId(Utils.getUUID());
		result.setType(Const.EXP_TYPE_CERT);
		result.getAttributes().put(Const.ATTR_DATEFROM, sdf.parse(cert.getDate_certificate()).getTime());
		result.getAttributes().put(Const.ATTR_DATETO, sdf.parse(cert.getDate_certificate()).getTime());
		result.getAttributes().put(Const.ATTR_EDUCATIONAL, Boolean.TRUE);
		result.getAttributes().put(Const.ATTR_INSTITUTIONAL, Boolean.TRUE);
		result.getAttributes().put(Const.ATTR_CERTIFIED, Boolean.TRUE);
		result.getAttributes().put(Const.ATTR_TYPE, Const.CERT_TYPE_LANG);
		result.getAttributes().put(Const.ATTR_CERTIFIER, cert.getCertifier());
		result.getAttributes().put(Const.ATTR_TITLE, cert.getDescription());
		result.getAttributes().put(Const.ATTR_LANG, cert.getLanguage());
		result.getAttributes().put(Const.ATTR_LEVEL, cert.getTitle());
		
		return result;
	}
	
	private StudentExperience convertToStudentExperience(Certificazione cert, 
			Experience experience, Student student) throws ParseException {
		StudentExperience result = new StudentExperience();
		result.setOrigin(cert.getOrigin());
		result.setExtId(cert.getExtid());
		result.setId(Utils.getUUID());
		
		result.setStudentId(student.getId());
		result.setStudent(student);
		
		experience.getAttributes().put(Const.ATTR_JUDGEMENT, getJudgement(cert));
		result.setExperienceId(experience.getId());
		result.setExperience(experience);
		
		return result;
	}

	private String getJudgement(Certificazione cert) {
		String result = "";
		if(Utils.isNotEmpty(cert.getJudgment())) {
			result = cert.getJudgment();
		}
		if(Utils.isNotEmpty(cert.getJudgmentmax())) {
			result = result + "/" + cert.getJudgmentmax();
		}
		return result;
	}
	
}
