package it.smartcommunitylab.csengine.extsource.infotn;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import it.smartcommunitylab.csengine.model.MetaInfo;
import it.smartcommunitylab.csengine.model.ProfessoriClassi;
import it.smartcommunitylab.csengine.model.ScheduleUpdate;
import it.smartcommunitylab.csengine.storage.InstituteRepository;
import it.smartcommunitylab.csengine.storage.ProfessoriClassiRepository;
import it.smartcommunitylab.csengine.storage.ScheduleUpdateRepository;

@Service
public class InfoTnImportProfessoriClassi {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportProfessoriClassi.class);

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

	private String apiKey = Const.API_PROFESSORI_CLASSI_KEY;

	@Autowired
	private APIUpdateManager apiUpdateManager;
	@Autowired
	private ProfessoriClassiRepository professoriClassiRepository;
	@Autowired
	InstituteRepository instituteRepository;
	@Autowired
	ScheduleUpdateRepository metaInfoRepository;

	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);

	public void initProfessoriClassi(ScheduleUpdate scheduleUpdate) throws Exception {
		logger.info("start initProfessoriClassi");
		List<MetaInfo> metaInfosProfClassi = scheduleUpdate.getUpdateMap().get(apiKey);

		if (metaInfosProfClassi == null) {
			metaInfosProfClassi = new ArrayList<MetaInfo>();
		}
		for (int i = startingYear; i <= Calendar.getInstance().get(Calendar.YEAR); i++) {
			MetaInfo metaInfo = new MetaInfo();
			metaInfo.setName(apiKey);
			metaInfo.setSchoolYear(i);
			updateProfessoriClassi(metaInfo);
			metaInfosProfClassi.add(metaInfo);
		}
		scheduleUpdate.getUpdateMap().put(apiKey, metaInfosProfClassi);

	}

	private void updateProfessoriClassi(MetaInfo metaInfo) throws Exception {

		int total = 0;
		int stored = 0;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;
		int nextYear = metaInfo.getSchoolYear() + 1;
		String schoolYear = metaInfo.getSchoolYear() + "/" + String.valueOf(nextYear).substring(2);

		// read epoc timestamp from db(if exist)
		if (metaInfo.getEpocTimestamp() > 0) {
			url = infoTNAPIUrl + "/professoriclassi?schoolYear=" + schoolYear + "&timestamp="
					+ metaInfo.getEpocTimestamp();
		} else {
			url = infoTNAPIUrl + "/professoriclassi?schoolYear=" + schoolYear;
		}
		logger.info("start importProfessoriClassiUsingRESTAPI for year " + schoolYear);

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
			metaInfo.setTotalRead(total);
			metaInfo.setTotalStore(stored);
		}
	}

	private ProfessoriClassi convertToLocalProfessorClassBean(ProfessoriClassi professorClassExt) {
		ProfessoriClassi result = new ProfessoriClassi();
		Date now = new Date();
		result.setCreationDate(now);
		result.setLastUpdate(now);
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

	public String importProfessoriClassiFromRESTAPI() {
		try {
			List<MetaInfo> savedMetaInfoList = apiUpdateManager.fetchMetaInfoForAPI(apiKey);
			for (MetaInfo metaInfo : savedMetaInfoList) {
				if (!metaInfo.isBlocked()) {
					updateProfessoriClassi(metaInfo);
				}
			}
			apiUpdateManager.saveMetaInfoList(apiKey, savedMetaInfoList);
			return "OK";

		} catch (Exception e) {
			logger.error(e.getMessage());
			return e.getMessage();
		}
	}

}
