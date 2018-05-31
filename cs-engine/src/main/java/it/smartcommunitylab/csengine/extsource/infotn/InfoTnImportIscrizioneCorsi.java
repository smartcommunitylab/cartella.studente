package it.smartcommunitylab.csengine.extsource.infotn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

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

import it.smartcommunitylab.csengine.common.HTTPUtils;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.model.CourseMetaInfo;
import it.smartcommunitylab.csengine.model.Institute;
import it.smartcommunitylab.csengine.model.MetaInfo;
import it.smartcommunitylab.csengine.model.Registration;
import it.smartcommunitylab.csengine.model.Student;
import it.smartcommunitylab.csengine.model.TeachingUnit;
import it.smartcommunitylab.csengine.storage.CourseMetaInfoRepository;
import it.smartcommunitylab.csengine.storage.CourseRepository;
import it.smartcommunitylab.csengine.storage.InstituteRepository;
import it.smartcommunitylab.csengine.storage.MetaInfoRepository;
import it.smartcommunitylab.csengine.storage.RegistrationRepository;
import it.smartcommunitylab.csengine.storage.StudentRepository;
import it.smartcommunitylab.csengine.storage.TeachingUnitRepository;

@Component
public class InfoTnImportIscrizioneCorsi {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportIscrizioneCorsi.class);

	@Autowired
	@Value("${infotn.source.folder}")
	private String sourceFolder;

	@Value("${infotn.api.url}")
	private String infoTNAPIUrl;

	@Value("${infotn.api.user}")
	private String user;

	@Value("${infotn.api.pass}")
	private String password;

	private String metaInfoName = "IscrizioneCorsi";
	private String metaInfoIstituzioni = "Istituzioni";

	boolean importAll = false;

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

	@Autowired
	MetaInfoRepository metaInfoRepository;

	@Autowired
	CourseMetaInfoRepository courseMetaInfoRepository;

	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);

	public String importIscrizioneCorsiFromRESTAPI() throws Exception {
		logger.info("start importIscrizioneCorsiFromRESTAPI");
		MetaInfo metaInfoIst = metaInfoRepository.findOne(metaInfoIstituzioni);
		if (metaInfoIst != null) {
			Map<String, String> schoolYears = metaInfoIst.getSchoolYears();
			// read registered time stamp.
			MetaInfo metaInfo = metaInfoRepository.findOne(metaInfoName);
			if (metaInfo != null) {
				// get currentYear.
				int currentYear = Calendar.getInstance().get(Calendar.YEAR);
				int nextYear = currentYear + 1;
				String schoolYear = currentYear + "/" + String.valueOf(nextYear).substring(2);
				String url = infoTNAPIUrl + "/iscrizioni?schoolYear=" + schoolYear + "&timestamp="
						+ metaInfo.getEpocTimestamp();
				try {

					importIscirzioneCorsiUsingRESTAPI(url, schoolYear, metaInfo);
					return metaInfo.getTotalStore() + "/" + metaInfo.getTotalRead();

				} catch (Exception e) {
					return e.getMessage();
				}

			} else {
				metaInfo = new MetaInfo();
				metaInfo.setName(metaInfoName);
				try {

					for (Map.Entry<String, String> entry : schoolYears.entrySet()) {
						String url = infoTNAPIUrl + "/iscrizioni?schoolYear=" + entry.getValue();
						importIscirzioneCorsiUsingRESTAPI(url, entry.getValue(), metaInfo);
					}
					return (metaInfo.getTotalStore() + "/" + metaInfo.getTotalRead());

				} catch (Exception e) {
					return e.getMessage();
				}
			}
		} else {
			return "Run /istituto import first.";
		}
	}

	private void importIscirzioneCorsiUsingRESTAPI(String url, String schoolYear, MetaInfo metaInfo) throws Exception {
		logger.info("start importIscirzioneCorsiUsingRESTAPI for year " + schoolYear);
		int total = 0;
		int stored = 0;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// call api.
		String response = HTTPUtils.get(url, null, user, password);
		if (response != null && !response.isEmpty()) {
			JsonFactory jsonFactory = new JsonFactory();
			jsonFactory.setCodec(objectMapper);
			JsonParser jp = jsonFactory.createParser(response);
			JsonToken current;
			current = jp.nextToken();
			if (current != JsonToken.START_ARRAY) {
				logger.error("Error: root should be array: quiting.");
				throw new Exception("Error: root should be array: quiting.");
			}
			while (jp.nextToken() != JsonToken.END_ARRAY) {
				total += 1;
				IscrizioneCorso iscrizione = jp.readValueAs(IscrizioneCorso.class);
				logger.info("converting " + iscrizione.getExtId());
				Registration registrationDb = registrationRepository.findByExtId(iscrizione.getOrigin(),
						iscrizione.getExtId());
				if (registrationDb != null) {
					logger.warn(String.format("Registration already exists: %s - %s", iscrizione.getOrigin(),
							iscrizione.getExtId()));
					continue;
				}
				CourseMetaInfo courseMetaInfoDb = courseMetaInfoRepository
						.findByExtId(iscrizione.getCourseRef().getOrigin(), iscrizione.getCourseRef().getExtId());
				if (courseMetaInfoDb == null) {
					logger.warn(String.format("CourseMetaInfo not found: %s - %s",
							iscrizione.getCourseRef().getOrigin(), iscrizione.getCourseRef().getExtId()));
					continue;
				}
				Institute instituteDb = instituteRepository.findByExtId(iscrizione.getInstituteRef().getOrigin(),
						iscrizione.getInstituteRef().getExtId());
				if (instituteDb == null) {
					logger.warn(String.format("Institute not found: %s - %s", iscrizione.getInstituteRef().getOrigin(),
							iscrizione.getInstituteRef().getExtId()));
					continue;
				}
				TeachingUnit teachingUnitDb = teachingUnitRepository.findByExtId(
						iscrizione.getTeachingUnitRef().getOrigin(), iscrizione.getTeachingUnitRef().getExtId());
				if (teachingUnitDb == null) {
					logger.warn(String.format("TeachingUnit not found: %s - %s",
							iscrizione.getTeachingUnitRef().getOrigin(), iscrizione.getTeachingUnitRef().getExtId()));
					continue;
				}
				Student student = studentRepository.findByExtId(iscrizione.getStudent().getOrigin(),
						iscrizione.getStudent().getExtId());
				if (student == null) {
					logger.warn(String.format("Student not found: %s", iscrizione.getStudent().getExtId()));
					continue;
				}

				Registration registration = convertToRegistration(iscrizione, schoolYear);
				registration.setInstituteId(instituteDb.getId());
				registration.setInstitute(instituteDb);
				registration.setTeachingUnitId(teachingUnitDb.getId());
				registration.setTeachingUnit(teachingUnitDb);
				registration.setCourseId(courseMetaInfoDb.getId());
				registration.setCourse(courseMetaInfoDb.getCourse());
				registration.setStudentId(student.getId());
				registration.setStudent(student);
				registrationRepository.save(registration);
				stored += 1;
				logger.info(String.format("Save Registration: %s - %s - %s", iscrizione.getOrigin(),
						iscrizione.getExtId(), registration.getId()));
			}

			if (metaInfo != null) {
				// update time stamp (if all works fine).
				metaInfo.setEpocTimestamp(System.currentTimeMillis() / 1000);
				// total = metaInfo.getTotalRead() + total;
				metaInfo.setTotalRead(total);
				// stored = metaInfo.getTotalStore() + stored;
				metaInfo.setTotalStore(stored);
				metaInfoRepository.save(metaInfo);
			}

		}

	}

	private Registration convertToRegistration(IscrizioneCorso iscrizioneCorso, String schoolYear)
			throws ParseException {
		Registration result = new Registration();
		result.setOrigin(iscrizioneCorso.getOrigin());
		result.setExtId(iscrizioneCorso.getExtId());
		result.setId(Utils.getUUID());
		result.setDateFrom(sdf.parse(iscrizioneCorso.getStudent().getDateFrom()));
		result.setDateTo(sdf.parse(iscrizioneCorso.getStudent().getDateTo()));
		result.setSchoolYear(getSchoolYear(schoolYear));
		result.setClassroom(iscrizioneCorso.getStudent().getClassRoom());
		return result;
	}

	private String getSchoolYear(String annoScolastico) {
		return annoScolastico.replace("/", "-");
	}

	public void importIscrizioneCorsiForYear(String schoolYear) throws Exception {
		String url = infoTNAPIUrl + "/iscrizioni?schoolYear=" + schoolYear;
		importIscirzioneCorsiUsingRESTAPI(url, schoolYear, null);
	}

	// public String importIscrizioneCorsiFromEmpty() throws Exception {
	// logger.info("start importIscrizioneCorsiFromEmpty");
	// int total = 0;
	// int stored = 0;
	// FileReader fileReader = new FileReader(sourceFolder + "FBK_iscrizioni
	// corsi quinto v.02.json");
	// ObjectMapper objectMapper = new ObjectMapper();
	// objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
	// false);
	// JsonFactory jsonFactory = new JsonFactory();
	// jsonFactory.setCodec(objectMapper);
	// JsonParser jp = jsonFactory.createParser(fileReader);
	// JsonToken current;
	// current = jp.nextToken();
	// if (current != JsonToken.START_OBJECT) {
	// logger.error("Error: root should be object: quiting.");
	// return "Error: root should be object: quiting.";
	// }
	// while (jp.nextToken() != JsonToken.END_OBJECT) {
	// String fieldName = jp.getCurrentName();
	// current = jp.nextToken();
	// if (fieldName.equals("items")) {
	// if (current == JsonToken.START_ARRAY) {
	// while (jp.nextToken() != JsonToken.END_ARRAY) {
	// total += 1;
	// IscrizioneCorso iscrizione = jp.readValueAs(IscrizioneCorso.class);
	// logger.info("converting " + iscrizione.getExtid());
	// Registration registrationDb =
	// registrationRepository.findByExtId(iscrizione.getOrigin(),
	// iscrizione.getExtid());
	// if(registrationDb != null) {
	// logger.warn(String.format("Student already exists: %s - %s",
	// iscrizione.getOrigin(), iscrizione.getExtid()));
	// continue;
	// }
	// Student student =
	// studentRepository.findByExtId(iscrizione.getOrigin_student(),
	// iscrizione.getExtid_studente());
	// if(student == null) {
	// logger.warn(String.format("Student not found: %s",
	// iscrizione.getExtid_studente()));
	// continue;
	// }
	// Course course =
	// courseRepository.findByExtId(iscrizione.getOrigin_course(),
	// iscrizione.getExtid_course());
	// if(course == null) {
	// logger.warn(String.format("Course not found: %s",
	// iscrizione.getExtid_course()));
	// continue;
	// }
	// TeachingUnit teachingUnit =
	// teachingUnitRepository.findOne(course.getTeachingUnitId());
	// Institute institute =
	// instituteRepository.findOne(course.getInstituteId());
	// Registration registration = convertToRegistration(iscrizione);
	// registration.setInstituteId(institute.getId());
	// registration.setInstitute(institute);
	// registration.setTeachingUnitId(teachingUnit.getId());
	// registration.setTeachingUnit(teachingUnit);
	// registration.setCourseId(course.getId());
	// registration.setCourse(course.getCourse());
	// registration.setStudentId(student.getId());
	// registration.setStudent(student);
	// registrationRepository.save(registration);
	// stored += 1;
	// logger.info(String.format("Save Registration: %s - %s - %s",
	// iscrizione.getOrigin(),
	// iscrizione.getExtid(), registration.getId()));
	// }
	// } else {
	// logger.warn("Error: records should be an array: skipping.");
	// jp.skipChildren();
	// }
	// } else {
	// logger.warn("Unprocessed property: " + fieldName);
	// jp.skipChildren();
	// }
	// }
	// return stored + "/" + total;
	// }
}
