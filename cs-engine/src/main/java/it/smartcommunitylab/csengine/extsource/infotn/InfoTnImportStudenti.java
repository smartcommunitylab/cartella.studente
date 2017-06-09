package it.smartcommunitylab.csengine.extsource.infotn;

import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.model.Student;
import it.smartcommunitylab.csengine.storage.StudentRepository;

import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
public class InfoTnImportStudenti {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportStudenti.class);
	
	@Autowired
	@Value("${infotn.source.folder}")	
	private String sourceFolder;
	
	@Autowired
	StudentRepository studentRepository;
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", Locale.ITALY);
	SimpleDateFormat sdfStandard = new SimpleDateFormat("dd/MM/yyyy");
	
	public String importStudentiFromEmpty() throws Exception {
		logger.info("start importStudentiFromEmpty");
		int total = 0;
		int stored = 0;
		FileReader fileReader = new FileReader(sourceFolder + "ANAGRAFE_STUD_TUTTI_triennio.json");
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
						Studente studente = jp.readValueAs(Studente.class);
						logger.info("converting " + studente.getExtid());
						Student studentDb = studentRepository.findByExtId(studente.getOrigin(), 
								studente.getExtid());
						if(studentDb != null) {
							logger.warn(String.format("Student already exists: %s - %s", 
									studente.getOrigin(), studente.getExtid()));
							continue;
						}
						Student student = convertToStudent(studente);
						studentRepository.save(student);
						stored += 1;
						logger.info(String.format("Save Student: %s - %s - %s", studente.getOrigin(), 
								studente.getExtid(), student.getId()));
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
	
	private Student convertToStudent(Studente studente) throws ParseException {
		Student result = new Student();
		result.setOrigin(studente.getOrigin());
		result.setExtId(studente.getExtid());
		result.setId(Utils.getUUID());
		result.setCf(studente.getCf());
		result.setName(studente.getName());
		result.setSurname(studente.getSurname());
		result.setBirthdate(getBirthdate(studente.getBirthdate()));
		result.setAddress(getAddress(studente));
		result.setPhone(studente.getPhone());
		result.setMobilePhone(studente.getMobilephone());
		result.setEmail(studente.getEmail());
		result.setNationality(studente.getNazione());
		return result;
	}
	
	private String getAddress(Studente studente) {
		StringBuffer sb = new StringBuffer(studente.getIndirizzo());
		sb.append(", ");
		sb.append(studente.getCap());
		sb.append(" ");
		sb.append(studente.getComune());
		return sb.toString();
	}
	
	private String getBirthdate(String dataNascita) throws ParseException {
		Date date = sdf.parse(dataNascita);
		String result = sdfStandard.format(date);
		return result;
	}
}
