package it.smartcommunitylab.csengine.extsource.infotn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.HTTPUtils;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.model.Course;
import it.smartcommunitylab.csengine.model.CourseMetaInfo;
import it.smartcommunitylab.csengine.model.Institute;
import it.smartcommunitylab.csengine.model.MetaInfo;
import it.smartcommunitylab.csengine.model.TeachingUnit;
import it.smartcommunitylab.csengine.storage.CourseMetaInfoRepository;
import it.smartcommunitylab.csengine.storage.CourseRepository;
import it.smartcommunitylab.csengine.storage.InstituteRepository;
import it.smartcommunitylab.csengine.storage.ScheduleUpdateRepository;
import it.smartcommunitylab.csengine.storage.TeachingUnitRepository;

@Service
public class InfoTnImportCorsi {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportCorsi.class);

	@Autowired
	@Value("${infotn.source.folder}")
	private String sourceFolder;
	@Value("${infotn.starting.year}")
	private int startingYear;
	@Value("${infotn.api.url}")
	private String infoTNAPIUrl;
	@Value("${infotn.api.user}")
	private String user;
	@Value("${infotn.api.pass}")
	private String password;

	private String apiKey = Const.API_CORSI_KEY;

	@Autowired
	private APIUpdateManager apiUpdateManager;
	@Autowired
	InstituteRepository instituteRepository;
	@Autowired
	TeachingUnitRepository teachingUnitRepository;
	@Autowired
	CourseRepository courseRepository;
	@Autowired
	ScheduleUpdateRepository metaInfoRepository;
	@Autowired
	CourseMetaInfoRepository courseMetaInfoRepository;

	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);

	private void updateCorsi(MetaInfo metaInfo) throws Exception {
		int total = 0;
		int stored = 0;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;
		int nextYear = metaInfo.getSchoolYear() + 1;
		String year = metaInfo.getSchoolYear() + "/" + String.valueOf(nextYear).substring(2);

		// read epoc timestamp from db(if exist)
		if (metaInfo.getEpocTimestamp() > 0) {
			url = infoTNAPIUrl + "/offerte?schoolYear=" + year + "&timestamp=" + metaInfo.getEpocTimestamp();
		} else {
			url = infoTNAPIUrl + "/offerte?schoolYear=" + year;
		}

		logger.info("start importCorsiUsingRESTAPI for year " + year);

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
				Corso corso = jp.readValueAs(Corso.class);
				logger.info("converting " + corso.getExtId());
				// check from corso meta info repository and take the name
				Course courseDb = courseRepository.findByExtId(corso.getOrigin(), corso.getExtId());
				if (courseDb != null) {
					logger.warn(String.format("Course(Offerte) already exists: %s - %s", corso.getOrigin(),
							corso.getExtId()));
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
				CourseMetaInfo courseMetaInfoDb = courseMetaInfoRepository.findByExtId(corso.getCorsoRef().getOrigin(),
						corso.getCorsoRef().getExtId());
				if (courseMetaInfoDb == null) {
					logger.warn(String.format("CourseMetaInfo not found: %s - %s", corso.getCorsoRef().getOrigin(),
							corso.getCorsoRef().getExtId()));
					continue;
				}

				try {
					Course course = convertToCourse(corso);
					course.setInstituteId(instituteDb.getId());
					course.setTeachingUnitId(teachingUnitDb.getId());
					course.setTeachingUnit(teachingUnitDb.getName());
					course.setCourseMetaInfoId(courseMetaInfoDb.getId());
					courseRepository.save(course);
					stored += 1;
					logger.info(String.format("Save Course(Offerte): %s - %s - %s", corso.getOrigin(), corso.getExtId(),
							course.getId()));
				} catch (ParseException e) {
					logger.warn("Parse error:" + e.getMessage());
				}
			}
			// update time stamp (if all works fine).
			metaInfo.setEpocTimestamp(metaInfo.getEpocTimestamp() + 1);
			metaInfo.setTotalRead(total);
			metaInfo.setTotalStore(stored);
		}

	}

	private Course convertToCourse(Corso corso) throws ParseException {
		Course result = new Course();
		Date now = new Date();
		result.setCreationDate(now);
		result.setLastUpdate(now);
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

	public String importCorsiFromRESTAPI() {
		// chedere a APIUpdateManager i propri metadati
		// - scorrere i metadati
		// - se blocked è false:
		// - se richiesto setta schoolYear =
		// "duedigit(MetaInfo.schoolYear)/duedigit((MetaInfo.schoolYear + 1))"
		// // here i need to put year4d/year2d
		// - se richiesto e se epocTimestamp > 0 usare epocTimestamp
		// - invoca API
		// - aggiorna epocTimestamp di MetaInfo (metodo in APIUpdateManager)
		try {
			List<MetaInfo> savedMetaInfoList = apiUpdateManager.fetchMetaInfoForAPI(apiKey);

			if (savedMetaInfoList == null || savedMetaInfoList.isEmpty()) {
				// call generic method to create metaInfos (apiKey, year?)
				savedMetaInfoList = apiUpdateManager.createMetaInfoForAPI(apiKey, true);
			}

			for (MetaInfo metaInfo : savedMetaInfoList) {
				if (!metaInfo.isBlocked()) {
					updateCorsi(metaInfo);
				}
			}

			apiUpdateManager.saveMetaInfoList(apiKey, savedMetaInfoList);

			return "OK";

		} catch (Exception e) {
			logger.error(e.getMessage());
			return e.getMessage();
		}

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
