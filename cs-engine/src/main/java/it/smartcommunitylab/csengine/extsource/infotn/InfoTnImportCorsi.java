package it.smartcommunitylab.csengine.extsource.infotn;

import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.model.Course;
import it.smartcommunitylab.csengine.model.Institute;
import it.smartcommunitylab.csengine.model.TeachingUnit;
import it.smartcommunitylab.csengine.storage.CourseRepository;
import it.smartcommunitylab.csengine.storage.InstituteRepository;
import it.smartcommunitylab.csengine.storage.TeachingUnitRepository;

import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
public class InfoTnImportCorsi {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportCorsi.class);
	
	@Autowired
	@Value("${infotn.source.folder}")	
	private String sourceFolder;
	
	@Autowired
	InstituteRepository instituteRepository;
	
	@Autowired
	TeachingUnitRepository teachingUnitRepository;
	
	@Autowired
	CourseRepository courseRepository;
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", Locale.ITALY);
	
	public String importCorsiFromEmpty() throws Exception {
		logger.info("start importCorsiFromEmpty");
		int total = 0;
		int stored = 0;
		FileReader fileReader = new FileReader(sourceFolder + "FBK_CORSISTUDIO_v.01.json");
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
						Corso corso = jp.readValueAs(Corso.class);
						logger.info("converting " + corso.getExtid());
						Institute instituteDb = instituteRepository.findByExtId(corso.getOrigin_institute(), 
								corso.getExtid_institute());
						if(instituteDb == null) {
							logger.warn(String.format("Institute not found: %s - %s", 
									corso.getOrigin_institute(), corso.getExtid_institute()));
							continue;
						}
						TeachingUnit teachingUnitDb = teachingUnitRepository.findByExtId(corso.getOrigin_teachingunit(), 
								corso.getExtid_teachingunit());
						if(teachingUnitDb == null) {
							logger.warn(String.format("TeachingUnit not found: %s - %s", 
									corso.getOrigin_teachingunit(), corso.getExtid_teachingunit()));
							continue;
						}
						Course courseDb = courseRepository.findByExtId(corso.getOrigin(), corso.getExtid());
						if(courseDb != null) {
							logger.warn(String.format("Course already exists: %s - %s", 
									corso.getOrigin(), corso.getExtid()));
							continue;
						}
						try {
							Course course = convertToCourse(corso);
							course.setInstituteId(instituteDb.getId());
							course.setTeachingUnitId(teachingUnitDb.getId());
							course.setTeachingUnit(teachingUnitDb.getName());
							courseRepository.save(course);
							stored += 1;
							logger.info(String.format("Save Course: %s - %s - %s", corso.getOrigin(), 
									corso.getExtid(), course.getId()));
						} catch (ParseException e) {
							logger.warn("Parse error:" + e.getMessage());
						}
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
	
	private Course convertToCourse(Corso corso) throws ParseException {
		Course result = new Course();
		result.setOrigin(corso.getOrigin());
		result.setExtId(corso.getExtid());
		result.setId(Utils.getUUID());
		result.setSchoolYear(getSchoolYear(corso.getSchoolyear()));
		result.setDateFrom(getDate(corso.getDatefrom()));
		result.setDateTo(getDate(corso.getDateto()));
		result.setCourse(corso.getCourse());
		return result;
	}
	
	private Date getDate(String originDate) throws ParseException {
		Date newDate = sdf.parse(originDate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(newDate);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    return calendar.getTime();
	}
	
	private String getSchoolYear(String annoScolastico) {
		return annoScolastico.replace("/", "-");
	}
}
