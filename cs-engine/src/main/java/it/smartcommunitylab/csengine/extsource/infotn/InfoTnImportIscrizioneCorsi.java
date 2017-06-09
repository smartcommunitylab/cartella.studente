package it.smartcommunitylab.csengine.extsource.infotn;

import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.model.Course;
import it.smartcommunitylab.csengine.model.Institute;
import it.smartcommunitylab.csengine.model.Registration;
import it.smartcommunitylab.csengine.model.Student;
import it.smartcommunitylab.csengine.model.TeachingUnit;
import it.smartcommunitylab.csengine.storage.CourseRepository;
import it.smartcommunitylab.csengine.storage.InstituteRepository;
import it.smartcommunitylab.csengine.storage.RegistrationRepository;
import it.smartcommunitylab.csengine.storage.StudentRepository;
import it.smartcommunitylab.csengine.storage.TeachingUnitRepository;

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
public class InfoTnImportIscrizioneCorsi {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportIscrizioneCorsi.class);
	
	@Autowired
	@Value("${infotn.source.folder}")	
	private String sourceFolder;
	
	@Autowired
	StudentRepository studentRepository;
	
	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	RegistrationRepository registrationRepository;
	
	@Autowired
	InstituteRepository instituteRepository;
	
	@Autowired
	TeachingUnitRepository teachingUnitRepository;
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", Locale.ITALY);
	
	public String importIscrizioneCorsiFromEmpty() throws Exception {
		logger.info("start importIscrizioneCorsiFromEmpty");
		int total = 0;
		int stored = 0;
		FileReader fileReader = new FileReader(sourceFolder + "ISCRIZIONICORSI_STUD_quintoanno.json");
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
						IscrizioneCorso iscrizione = jp.readValueAs(IscrizioneCorso.class);
						logger.info("converting " + iscrizione.getExtid());
						Registration registrationDb = registrationRepository.findByExtId(iscrizione.getOrigin(), 
								iscrizione.getExtid());
						if(registrationDb != null) {
							logger.warn(String.format("Student already exists: %s - %s", 
									iscrizione.getOrigin(), iscrizione.getExtid()));
							continue;
						}
						Student student = studentRepository.findByExtId(iscrizione.getOrigin_student(), 
								iscrizione.getExtid_studente());
						if(student == null) {
							logger.warn(String.format("Student not found: %s", iscrizione.getExtid_studente()));
							continue;
						}
						Course course = courseRepository.findByExtId(iscrizione.getOrigin_course(), 
								iscrizione.getExtid_course());
						if(course == null) {
							logger.warn(String.format("Course not found: %s", iscrizione.getExtid_course()));
							continue;
						}
						TeachingUnit teachingUnit = teachingUnitRepository.findOne(course.getTeachingUnitId());
						Institute institute = instituteRepository.findOne(course.getInstituteId());
						Registration registration = convertToRegistration(iscrizione);
						registration.setInstituteId(institute.getId());
						registration.setInstitute(institute);
						registration.setTeachingUnitId(teachingUnit.getId());
						registration.setTeachingUnit(teachingUnit);
						registration.setCourseId(course.getId());
						registration.setCourse(course.getCourse());
						registration.setStudentId(student.getId());
						registration.setStudent(student);
						registrationRepository.save(registration);
						stored += 1;
						logger.info(String.format("Save Registration: %s - %s - %s", iscrizione.getOrigin(), 
								iscrizione.getExtid(), registration.getId()));
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
	
	private Registration convertToRegistration(IscrizioneCorso iscrizioneCorso) throws ParseException {
		Registration result = new Registration();
		result.setOrigin(iscrizioneCorso.getOrigin());
		result.setExtId(iscrizioneCorso.getExtid());
		result.setId(Utils.getUUID());
		result.setDateFrom(sdf.parse(iscrizioneCorso.getDatefrom()));
		result.setDateTo(sdf.parse(iscrizioneCorso.getDateto()));
		result.setSchoolYear(getSchoolYear(iscrizioneCorso.getSchoolyear()));
		result.setClassroom(iscrizioneCorso.getClassroom());
		return result;
	}
	
	private String getSchoolYear(String annoScolastico) {
		return annoScolastico.replace("/", "-");
	}

}
