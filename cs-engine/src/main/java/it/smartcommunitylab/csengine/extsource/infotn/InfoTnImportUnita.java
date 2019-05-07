package it.smartcommunitylab.csengine.extsource.infotn;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import it.smartcommunitylab.csengine.model.Institute;
import it.smartcommunitylab.csengine.model.MetaInfo;
import it.smartcommunitylab.csengine.model.TeachingUnit;
import it.smartcommunitylab.csengine.model.Typology;
import it.smartcommunitylab.csengine.storage.InstituteRepository;
import it.smartcommunitylab.csengine.storage.ScheduleUpdateRepository;
import it.smartcommunitylab.csengine.storage.TeachingUnitRepository;

@Service
public class InfoTnImportUnita {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportUnita.class);

	@Autowired
	@Value("${infotn.source.folder}")
	private String sourceFolder;

	@Value("${infotn.api.url}")
	private String infoTNAPIUrl;

	@Value("${infotn.api.user}")
	private String user;

	@Value("${infotn.api.pass}")
	private String password;

	private String apiKey = Const.API_TEACHING_UNIT_KEY;

	@Autowired
	private APIUpdateManager apiUpdateManager;

	@Autowired
	InstituteRepository instituteRepository;

	@Autowired
	TeachingUnitRepository teachingUnitRepository;

	@Autowired
	ScheduleUpdateRepository metaInfoRepository;

	@Autowired
	private InfoTnSchools infoTnSchools;

	public void updateUnita(MetaInfo metaInfo) throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;
		int total = 0;
		int stored = 0;

		if (metaInfo.getEpocTimestamp() > 0) {
			url = infoTNAPIUrl + "/unita?timestamp=" + metaInfo.getEpocTimestamp();
		} else {
			metaInfo.setEpocTimestamp(System.currentTimeMillis()); //set it for the first time.
			url = infoTNAPIUrl + "/unita";
		}

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
			}
			while (jp.nextToken() != JsonToken.END_ARRAY) {
				total += 1;
				Unita unita = jp.readValueAs(Unita.class);
				logger.info("converting " + unita.getExtId());
				if (Utils.isNotEmpty(unita.getDateTo())) {
					logger.warn(String.format("TU with date to: %s - %s", unita.getExtId(), unita.getDateTo()));
				}
				TeachingUnit teachingUnitDb = teachingUnitRepository.findByExtId(unita.getOrigin(), unita.getExtId());
				if (teachingUnitDb != null) {
					logger.warn(String.format("TU already exists: %s - %s", unita.getOrigin(), unita.getExtId()));
					if (Utils.isNotEmpty(unita.getTeachingUnit().getCodiceMiur())) {
						teachingUnitDb.setCodiceMiur(unita.getTeachingUnit().getCodiceMiur());
					}
					continue;
				}
				Institute instituteDb = instituteRepository.findByExtId(unita.getInstituteRef().getOrigin(),
						unita.getInstituteRef().getExtId());
				if (instituteDb == null) {
					logger.warn(String.format("Institute not found: %s - %s", unita.getInstituteRef().getOrigin(),
							unita.getInstituteRef().getExtId()));
					continue;
				}
				TeachingUnit teachingUnit = convertToTeachingUnit(unita);
				teachingUnit.setInstituteId(instituteDb.getId());
				teachingUnitRepository.save(teachingUnit);
				stored += 1;
				logger.info(String.format("Save TeachingUnit: %s - %s - %s", unita.getOrigin(), unita.getExtId(),
						teachingUnit.getId()));

			}

			// update time stamp (if all works fine).
			metaInfo.setEpocTimestamp(metaInfo.getEpocTimestamp() + 1);
			metaInfo.setTotalRead(total);
			metaInfo.setTotalStore(stored);
		}

	}

	private TeachingUnit convertToTeachingUnit(Unita unita) {
		TeachingUnit result = new TeachingUnit();
		result.setOrigin(unita.getOrigin());
		result.setExtId(unita.getExtId());
		result.setId(Utils.getUUID());
		result.setName(unita.getTeachingUnit().getName());
		result.setDescription(unita.getTeachingUnit().getDescription());
		result.setAddress(unita.getTeachingUnit().getAddress());
		Map<String, Typology> classifications = new HashMap<String, Typology>();
		if (Utils.isNotEmpty(unita.getTeachingUnit().getOrdineScuola())) {
			Typology typology = new Typology();
			typology.setQualifiedName(Const.TYPOLOGY_QNAME_ORDINE);
			typology.setName(unita.getTeachingUnit().getOrdineScuola());
			classifications.put(Const.TYPOLOGY_QNAME_ORDINE, typology);
		}
		if (Utils.isNotEmpty(unita.getTeachingUnit().getTipoScuola())) {
			Typology typology = new Typology();
			typology.setQualifiedName(Const.TYPOLOGY_QNAME_TIPOLOGIA);
			typology.setName(unita.getTeachingUnit().getTipoScuola());
			classifications.put(Const.TYPOLOGY_QNAME_TIPOLOGIA, typology);
		}
		if (Utils.isNotEmpty(unita.getTeachingUnit().getMgIndirizzoDidattico())) {
			Typology typology = new Typology();
			typology.setQualifiedName(Const.TYPOLOGY_QNAME_INDIRIZZO);
			typology.setName(unita.getTeachingUnit().getMgIndirizzoDidattico());
			classifications.put(Const.TYPOLOGY_QNAME_INDIRIZZO, typology);
		}
		if (Utils.isNotEmpty(unita.getTeachingUnit().getCodiceMiur())) {
			result.setCodiceMiur(unita.getTeachingUnit().getCodiceMiur());
		}
		if (classifications.size() > 0) {
			result.setClassifications(classifications);
		}

		Scuola scuola = infoTnSchools.getScuola(unita.getExtId());
		if (scuola != null) {
			Double[] geocode = new Double[2];
			try {
				geocode[0] = Double.valueOf(scuola.getLongitude());
				geocode[1] = Double.valueOf(scuola.getLatitude());
				result.setGeocode(geocode);
			} catch (Exception e) {
				logger.warn("error converting geocode:" + e.getMessage());
			}
		}

		Date now = new Date();
		result.setCreationDate(now);
		result.setLastUpdate(now);

		return result;
	}

	public String importUnitaFromRESTAPI() {
	
		try {
			List<MetaInfo> savedMetaInfoList = apiUpdateManager.fetchMetaInfoForAPI(apiKey);
			
			if (savedMetaInfoList == null || savedMetaInfoList.isEmpty()) {
				// call generic method to create metaInfos (apiKey, year?)
				savedMetaInfoList = apiUpdateManager.createMetaInfoForAPI(apiKey, false);
			}
			
			for (MetaInfo metaInfo : savedMetaInfoList) {
				if (!metaInfo.isBlocked()) {
					updateUnita(metaInfo);
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
