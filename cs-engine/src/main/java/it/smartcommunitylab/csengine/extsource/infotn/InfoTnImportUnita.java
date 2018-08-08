package it.smartcommunitylab.csengine.extsource.infotn;

import java.util.HashMap;
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

import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.HTTPUtils;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.model.Institute;
import it.smartcommunitylab.csengine.model.MetaInfo;
import it.smartcommunitylab.csengine.model.TeachingUnit;
import it.smartcommunitylab.csengine.model.Typology;
import it.smartcommunitylab.csengine.storage.InstituteRepository;
import it.smartcommunitylab.csengine.storage.MetaInfoRepository;
import it.smartcommunitylab.csengine.storage.TeachingUnitRepository;

@Component
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

	private String metaInfoName = "Unita";

	@Autowired
	InstituteRepository instituteRepository;

	@Autowired
	TeachingUnitRepository teachingUnitRepository;

	@Autowired
	MetaInfoRepository metaInfoRepository;

	@Autowired
	private InfoTnSchools infoTnSchools;
	
	public String importUnitaFromRESTAPI() throws Exception {
		logger.info("start importUnitaFromRESTAPI");
		int total = 0;
		int stored = 0;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;

		// read epoc timestamp from db(if exist)
		MetaInfo metaInfo = metaInfoRepository.findOne(metaInfoName);
		if (metaInfo != null) {
			url = infoTNAPIUrl + "/unita?timestamp=" + metaInfo.getEpocTimestamp();

		} else {
			metaInfo = new MetaInfo();
			metaInfo.setName(metaInfoName);
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
				return "Error: root should be array: quiting.";

			}
			while (jp.nextToken() != JsonToken.END_ARRAY) {
				total += 1;
				Unita unita = jp.readValueAs(Unita.class);
				logger.info("converting " + unita.getExtId());
				if(Utils.isNotEmpty(unita.getDateTo())) {
					logger.warn(String.format("TU with date to: %s - %s", unita.getExtId(), unita.getDateTo()));
				}
				TeachingUnit teachingUnitDb = teachingUnitRepository.findByExtId(unita.getOrigin(), unita.getExtId());
				if (teachingUnitDb != null) {
					logger.warn(String.format("TU already exists: %s - %s", unita.getOrigin(), unita.getExtId()));
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
			metaInfo.setEpocTimestamp(System.currentTimeMillis() / 1000);
			metaInfo.setTotalRead(total);
			metaInfo.setTotalStore(stored);
			metaInfoRepository.save(metaInfo);

		}

		return stored + "/" + total + "(" + metaInfo.getEpocTimestamp() + ")";
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
		if (classifications.size() > 0) {
			result.setClassifications(classifications);
		}
		
		Scuola scuola = infoTnSchools.getScuola(unita.getExtId());
		if(scuola != null) {
			Double[] geocode = new Double[2];
			try {
				geocode[0] = Double.valueOf(scuola.getLongitude());
				geocode[1] = Double.valueOf(scuola.getLatitude());
				result.setGeocode(geocode);
			} catch (Exception e) {
				logger.warn("error converting geocode:" + e.getMessage());
			}
		}
		
		return result;
	}
}
