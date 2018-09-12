//package it.smartcommunitylab.csengine.extsource.infotn;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import com.fasterxml.jackson.core.JsonFactory;
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.core.JsonToken;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import it.smartcommunitylab.csengine.common.Const;
//import it.smartcommunitylab.csengine.common.HTTPUtils;
//import it.smartcommunitylab.csengine.common.Utils;
//import it.smartcommunitylab.csengine.model.MetaInfo;
//import it.smartcommunitylab.csengine.model.TeachingUnit;
//import it.smartcommunitylab.csengine.model.Typology;
//import it.smartcommunitylab.csengine.storage.ScheduleUpdateRepository;
//import it.smartcommunitylab.csengine.storage.TeachingUnitRepository;
//
//@Component
//public class InfoTnUpdateUnita {
//	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnUpdateUnita.class);
//
//	@Autowired
//	@Value("${infotn.source.folder}")
//	private String sourceFolder;
//
//	@Value("${infotn.api.url}")
//	private String infoTNAPIUrl;
//
//	private String metaInfoName = "Unita";
//
//	@Autowired
//	TeachingUnitRepository teachingUnitRepository;
//
//	@Autowired
//	ScheduleUpdateRepository metaInfoRepository;
//
//	public String upateUnitaClassificazione() throws Exception {
//		logger.info("start upateUnitaClassificazione");
//		int total = 0;
//		int stored = 0;
//		ObjectMapper objectMapper = new ObjectMapper();
//		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		String url;
//
//		// read epoc timestamp from db(if exist)
//		MetaInfo metaInfo = metaInfoRepository.findOne(metaInfoName);
//		if (metaInfo != null) {
//			url = infoTNAPIUrl + "/unita?timestamp=" + metaInfo.getEpocTimestamp();
//
//		} else {
//			metaInfo = new MetaInfo();
//			metaInfo.setName(metaInfoName);
//			url = infoTNAPIUrl + "/unita";
//
//		}
//
//		// call api.
//		String response = HTTPUtils.get(url, null, null, null);
//		if (response != null && !response.isEmpty()) {
//			JsonFactory jsonFactory = new JsonFactory();
//			jsonFactory.setCodec(objectMapper);
//			JsonParser jp = jsonFactory.createParser(response);
//			JsonToken current;
//			current = jp.nextToken();
//			if (current != JsonToken.START_ARRAY) {
//				logger.error("Error: root should be array: quiting.");
//				return "Error: root should be array: quiting.";
//			}
//			while (jp.nextToken() != JsonToken.END_ARRAY) {
//
//				total += 1;
//				Unita unita = jp.readValueAs(Unita.class);
//				logger.info("converting " + unita.getExtId());
//				TeachingUnit teachingUnitDb = teachingUnitRepository.findByExtId(unita.getOrigin(), unita.getExtId());
//				if (teachingUnitDb == null) {
//					logger.warn(String.format("TU not found: %s - %s", unita.getOrigin(), unita.getExtId()));
//					continue;
//				}
//				if (updateTeachingUnit(teachingUnitDb, unita)) {
//					teachingUnitRepository.save(teachingUnitDb);
//					stored += 1;
//					logger.info(String.format("Update TeachingUnit: %s - %s - %s", unita.getInstituteRef().getOrigin(),
//							unita.getInstituteRef().getExtId(), teachingUnitDb.getId()));
//				} else {
//					logger.info("Skip TeachingUnit: %s - %s - %s", unita.getOrigin(), unita.getExtId(),
//							teachingUnitDb.getId());
//				}
//			}
//
//			// update time stamp (if all works fine).
//			metaInfo.setEpocTimestamp(System.currentTimeMillis() / 1000);
//			metaInfo.setTotalRead(total);
//			metaInfo.setTotalStore(stored);
//			metaInfoRepository.save(metaInfo);
//
//		}
//
//		return stored + "/" + total;
//	}
//
//	// public String upateUnitaClassificazione() throws Exception {
//	// logger.info("start upateUnitaClassificazione");
//	// int total = 0;
//	// int stored = 0;
//	// FileReader fileReader = new FileReader(sourceFolder + "FBK_Unità
//	// Scolastiche v02.json");
//	// ObjectMapper objectMapper = new ObjectMapper();
//	// objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
//	// false);
//	// JsonFactory jsonFactory = new JsonFactory();
//	// jsonFactory.setCodec(objectMapper);
//	// JsonParser jp = jsonFactory.createParser(fileReader);
//	// JsonToken current;
//	// current = jp.nextToken();
//	// if (current != JsonToken.START_OBJECT) {
//	// logger.error("Error: root should be object: quiting.");
//	// return "Error: root should be object: quiting.";
//	// }
//	// while (jp.nextToken() != JsonToken.END_OBJECT) {
//	// String fieldName = jp.getCurrentName();
//	// current = jp.nextToken();
//	// if (fieldName.equals("items")) {
//	// if (current == JsonToken.START_ARRAY) {
//	// while (jp.nextToken() != JsonToken.END_ARRAY) {
//	// total += 1;
//	// Unita unita = jp.readValueAs(Unita.class);
//	// logger.info("converting " + unita.getExtid());
//	// TeachingUnit teachingUnitDb =
//	// teachingUnitRepository.findByExtId(unita.getOrigin(),
//	// unita.getExtid());
//	// if(teachingUnitDb == null) {
//	// logger.warn(String.format("TU not found: %s - %s",
//	// unita.getOrigin(), unita.getExtid()));
//	// continue;
//	// }
//	// if(updateTeachingUnit(teachingUnitDb, unita)) {
//	// teachingUnitRepository.save(teachingUnitDb);
//	// stored += 1;
//	// logger.info(String.format("Update TeachingUnit: %s - %s - %s",
//	// unita.getOrigin(),
//	// unita.getExtid(), teachingUnitDb.getId()));
//	// } else {
//	// logger.info("Skip TeachingUnit: %s - %s - %s", unita.getOrigin(),
//	// unita.getExtid(), teachingUnitDb.getId());
//	// }
//	// }
//	// } else {
//	// logger.warn("Error: records should be an array: skipping.");
//	// jp.skipChildren();
//	// }
//	// } else {
//	// logger.warn("Unprocessed property: " + fieldName);
//	// jp.skipChildren();
//	// }
//	// }
//	// return stored + "/" + total;
//	// }
//
//	private boolean updateTeachingUnit(TeachingUnit tu, Unita unita) {
//		boolean update = false;
//		Map<String, Typology> classifications = new HashMap<String, Typology>();
//		if (Utils.isNotEmpty(unita.getTeachingUnit().getOrdineScuola())) {
//			Typology typology = new Typology();
//			typology.setQualifiedName(Const.TYPOLOGY_QNAME_ORDINE);
//			typology.setName(unita.getTeachingUnit().getOrdineScuola());
//			classifications.put(Const.TYPOLOGY_QNAME_ORDINE, typology);
//		}
//		if (Utils.isNotEmpty(unita.getTeachingUnit().getTipoScuola())) {
//			Typology typology = new Typology();
//			typology.setQualifiedName(Const.TYPOLOGY_QNAME_TIPOLOGIA);
//			typology.setName(unita.getTeachingUnit().getTipoScuola());
//			classifications.put(Const.TYPOLOGY_QNAME_TIPOLOGIA, typology);
//		}
//		if (Utils.isNotEmpty(unita.getTeachingUnit().getMgIndirizzoDidattico())) {
//			Typology typology = new Typology();
//			typology.setQualifiedName(Const.TYPOLOGY_QNAME_INDIRIZZO);
//			typology.setName(unita.getTeachingUnit().getMgIndirizzoDidattico());
//			classifications.put(Const.TYPOLOGY_QNAME_INDIRIZZO, typology);
//		}
//		if (classifications.size() > 0) {
//			tu.setClassifications(classifications);
//			update = true;
//		}
//		return update;
//	}
//}
