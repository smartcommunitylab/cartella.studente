package it.smartcommunitylab.csengine.extsource.infotn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.smartcommunitylab.csengine.common.HTTPUtils;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.model.Course;
import it.smartcommunitylab.csengine.model.Institute;
import it.smartcommunitylab.csengine.model.MetaInfo;
import it.smartcommunitylab.csengine.model.TeachingUnit;
import it.smartcommunitylab.csengine.storage.CourseRepository;
import it.smartcommunitylab.csengine.storage.InstituteRepository;
import it.smartcommunitylab.csengine.storage.MetaInfoRepository;
import it.smartcommunitylab.csengine.storage.TeachingUnitRepository;

@Component
public class InfoTnImportCorsi {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportCorsi.class);

	@Autowired
	@Value("${infotn.source.folder}")
	private String sourceFolder;

	@Value("${infotn.api.url}")
	private String infoTNAPIUrl;

	private String metaInfoName = "Corsi";
	private String metaInfoIstituzioni = "Istituzioni";

	@Autowired
	InstituteRepository instituteRepository;

	@Autowired
	TeachingUnitRepository teachingUnitRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	MetaInfoRepository metaInfoRepository;

	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);

//	order 3
	@Scheduled(cron = "0 30 23 * * ?")
	public String importCorsiFromRESTAPI() throws Exception {
		logger.info("start import procedure for courses");
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
				String url = infoTNAPIUrl + "/corsi?schoolYear=" + schoolYear + "&timestamp="
						+ metaInfo.getEpocTimestamp();
				try {

					importCorsiUsingRESTAPI(url, schoolYear, metaInfo);
					return metaInfo.getTotalStore() + "/" + metaInfo.getTotalRead();

				} catch (Exception e) {
					return e.getMessage();
				}

			} else {
				metaInfo = new MetaInfo();
				metaInfo.setName(metaInfoName);
				try {

					for (Map.Entry<String, String> entry : schoolYears.entrySet()) {
						String url = infoTNAPIUrl + "/corsi?schoolYear=" + entry.getValue();
						importCorsiUsingRESTAPI(url, entry.getValue(), metaInfo);
					}
					return (metaInfo.getTotalStore() + "/" + metaInfo.getTotalRead());

				} catch (Exception e) {
					return e.getMessage();
				}
			}
		} else {
			return "Run /istituti import first.";
		}

	}

	private void importCorsiUsingRESTAPI(String url, String schoolYear, MetaInfo metaInfo) throws Exception {
		logger.info("start importCorsiUsingRESTAPI for year " + schoolYear);
		int total = 0;
		int stored = 0;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// call api.
		String response = HTTPUtils.get(url, null, null, null);
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
				Corso corso = jp.readValueAs(Corso.class);
				logger.info("converting " + corso.getExtId());
				Course courseDb = courseRepository.findByExtId(corso.getOrigin(), corso.getExtId());
				if (courseDb != null) {
					logger.warn(String.format("Course already exists: %s - %s", corso.getOrigin(), corso.getExtId()));
					continue;
				}
				Institute instituteDb = instituteRepository.findByExtId(corso.getInstituteRef().getOrigin(),
						corso.getInstituteRef().getExtId());
				if (instituteDb == null) {
					logger.warn(String.format("Institute not found: %s - %s", corso.getInstituteRef().getOrigin(),
							corso.getInstituteRef().getExtId()));
					continue;
				}
				TeachingUnit teachingUnitDb = teachingUnitRepository.findByExtId(corso.getTeachingUnitRef().getOrigin(),
						corso.getTeachingUnitRef().getExtId());
				if (teachingUnitDb == null) {
					logger.warn(String.format("TeachingUnit not found: %s - %s", corso.getTeachingUnitRef().getOrigin(),
							corso.getTeachingUnitRef().getExtId()));
					continue;
				}
				try {
					Course course = convertToCourse(corso);
					course.setInstituteId(instituteDb.getId());
					course.setTeachingUnitId(teachingUnitDb.getId());
					course.setTeachingUnit(teachingUnitDb.getName());
					courseRepository.save(course);
					stored += 1;
					logger.info(String.format("Save Course: %s - %s - %s", corso.getOrigin(), corso.getExtId(),
							course.getId()));
				} catch (ParseException e) {
					logger.warn("Parse error:" + e.getMessage());
				}
			}
			// update time stamp (if all works fine).
			metaInfo.setEpocTimestamp(System.currentTimeMillis() / 1000);
			total = metaInfo.getTotalRead() + total;
			metaInfo.setTotalRead(total);
			stored = metaInfo.getTotalStore() + stored;
			metaInfo.setTotalStore(stored);
			metaInfoRepository.save(metaInfo);
		}
		
	}

	private Course convertToCourse(Corso corso) throws ParseException {
		Course result = new Course();
		result.setOrigin(corso.getOrigin());
		result.setExtId(corso.getExtId());
		result.setId(Utils.getUUID());
		result.setSchoolYear(getSchoolYear(corso.getSchoolYear()));
		result.setDateFrom(getDate(corso.getDateFrom()));
		result.setDateTo(getDate(corso.getDateTo()));
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

	// public String importCorsiFromEmpty() throws Exception {
	// logger.info("start importCorsiFromEmpty");
	// int total = 0;
	// int stored = 0;
	// FileReader fileReader = new FileReader(sourceFolder +
	// "FBK_CORSISTUDIO_v.01.json");
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
	// Corso corso = jp.readValueAs(Corso.class);
	// logger.info("converting " + corso.getExtid());
	// Course courseDb = courseRepository.findByExtId(corso.getOrigin(),
	// corso.getExtid());
	// if(courseDb != null) {
	// logger.warn(String.format("Course already exists: %s - %s",
	// corso.getOrigin(), corso.getExtid()));
	// continue;
	// }
	// Institute instituteDb =
	// instituteRepository.findByExtId(corso.getOrigin_institute(),
	// corso.getExtid_institute());
	// if(instituteDb == null) {
	// logger.warn(String.format("Institute not found: %s - %s",
	// corso.getOrigin_institute(), corso.getExtid_institute()));
	// continue;
	// }
	// TeachingUnit teachingUnitDb =
	// teachingUnitRepository.findByExtId(corso.getOrigin_teachingunit(),
	// corso.getExtid_teachingunit());
	// if(teachingUnitDb == null) {
	// logger.warn(String.format("TeachingUnit not found: %s - %s",
	// corso.getOrigin_teachingunit(), corso.getExtid_teachingunit()));
	// continue;
	// }
	// try {
	// Course course = convertToCourse(corso);
	// course.setInstituteId(instituteDb.getId());
	// course.setTeachingUnitId(teachingUnitDb.getId());
	// course.setTeachingUnit(teachingUnitDb.getName());
	// courseRepository.save(course);
	// stored += 1;
	// logger.info(String.format("Save Course: %s - %s - %s", corso.getOrigin(),
	// corso.getExtid(), course.getId()));
	// } catch (ParseException e) {
	// logger.warn("Parse error:" + e.getMessage());
	// }
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
