package it.smartcommunitylab.csengine.extsource.infotn;

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
import it.smartcommunitylab.csengine.model.MetaInfo;
import it.smartcommunitylab.csengine.model.ProfessoriClassi;
import it.smartcommunitylab.csengine.storage.InstituteRepository;
import it.smartcommunitylab.csengine.storage.MetaInfoRepository;
import it.smartcommunitylab.csengine.storage.ProfessoriClassiRepository;

@Component
public class InfoTnImportProfessoriClassi {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportProfessoriClassi.class);

	@Autowired
	@Value("${infotn.source.folder}")
	private String sourceFolder;

	@Value("${infotn.api.url}")
	private String infoTNAPIUrl;

	@Value("${infotn.api.user}")
	private String user;

	@Value("${infotn.api.pass}")
	private String password;

	private String metaInfoName = "ProfessoriClassi";
	private String metaInfoIstituzioni = "Istituzioni";

	@Autowired
	private ProfessoriClassiRepository professoriClassiRepository;

	@Autowired
	InstituteRepository instituteRepository;

	@Autowired
	MetaInfoRepository metaInfoRepository;

	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);

	public String importProfessoriClassiFromRESTAPI() throws Exception {
		logger.info("start importProfessoriClassiFromRESTAPI");
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
				String url = infoTNAPIUrl + "/professoriclassi?schoolYear=" + schoolYear + "&timestamp="
						+ metaInfo.getEpocTimestamp();
				try {

					importProfessoriClassiUsingRESTAPI(url, schoolYear, metaInfo);
					return metaInfo.getTotalStore() + "/" + metaInfo.getTotalRead();

				} catch (Exception e) {
					return e.getMessage();
				}

			} else {
				metaInfo = new MetaInfo();
				metaInfo.setName(metaInfoName);
				try {

					for (Map.Entry<String, String> entry : schoolYears.entrySet()) {
						String url = infoTNAPIUrl + "/professoriclassi?schoolYear=" + entry.getValue();
						importProfessoriClassiUsingRESTAPI(url, entry.getValue(), metaInfo);
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

	private void importProfessoriClassiUsingRESTAPI(String url, String schoolYear, MetaInfo metaInfo) throws Exception {
		logger.info("start importProfessoriClassiUsingRESTAPI for year " + schoolYear);
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
				ProfessoriClassi professoriClassiExt = jp.readValueAs(ProfessoriClassi.class);
				ProfessoriClassi saved = professoriClassiRepository.findByExtId(professoriClassiExt.getOrigin(),
						professoriClassiExt.getExtId());
				if (saved != null) {
					logger.warn(String.format("ProfessoriClassi already exists: %s - %s",
							professoriClassiExt.getOrigin(), professoriClassiExt.getExtId()));
					continue;
				}
				logger.info("converting " + professoriClassiExt.getExtId());

				ProfessoriClassi profClassi = convertToLocalProfessorClassBean(professoriClassiExt);
				professoriClassiRepository.save(profClassi);
				stored += 1;
				logger.info(String.format("Save ProfessoriClassi: %s - %s - %s", profClassi.getOrigin(),
						profClassi.getExtId(), profClassi.getId()));
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

	private ProfessoriClassi convertToLocalProfessorClassBean(ProfessoriClassi professorClassExt) {
		ProfessoriClassi result = new ProfessoriClassi();
		result.setId(Utils.getUUID());
		result.setClassroom(professorClassExt.getClassroom());
		result.setCourse(professorClassExt.getCourse());
		result.setDatefrom(professorClassExt.getDatefrom().replace("/", "-"));
		result.setDateto(professorClassExt.getDateto().replace("/", "-"));
		result.setSchoolyear(professorClassExt.getSchoolyear().replace("/", "-"));
		result.setTeacher(professorClassExt.getTeacher());
		result.setOrigin(professorClassExt.getOrigin());
		result.setExtId(professorClassExt.getExtId());
		return result;
	}

}
