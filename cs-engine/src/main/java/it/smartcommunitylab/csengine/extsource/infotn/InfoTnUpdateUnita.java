package it.smartcommunitylab.csengine.extsource.infotn;

import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.model.TeachingUnit;
import it.smartcommunitylab.csengine.model.Typology;
import it.smartcommunitylab.csengine.storage.TeachingUnitRepository;

import java.io.FileReader;
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

@Component
public class InfoTnUpdateUnita {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnUpdateUnita.class);
	
	@Autowired
	@Value("${infotn.source.folder}")	
	private String sourceFolder;
	
	@Autowired
	TeachingUnitRepository teachingUnitRepository;
	
	public String upateUnitaClassificazione() throws Exception {
		logger.info("start upateUnitaClassificazione");
		int total = 0;
		int stored = 0;
		FileReader fileReader = new FileReader(sourceFolder + "FBK_Unità Scolastiche v02.json");
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
						Unita unita = jp.readValueAs(Unita.class);
						logger.info("converting " + unita.getExtid());
						TeachingUnit teachingUnitDb = teachingUnitRepository.findByExtId(unita.getOrigin(), 
								unita.getExtid());
						if(teachingUnitDb == null) {
							logger.warn(String.format("TU not found: %s - %s", 
									unita.getOrigin(), unita.getExtid()));
							continue;
						}
						if(updateTeachingUnit(teachingUnitDb, unita)) {
							teachingUnitRepository.save(teachingUnitDb);
							stored += 1;
							logger.info(String.format("Update TeachingUnit: %s - %s - %s", unita.getOrigin(), 
									unita.getExtid(), teachingUnitDb.getId()));
						} else {
							logger.info("Skip TeachingUnit: %s - %s - %s", unita.getOrigin(), 
									unita.getExtid(), teachingUnitDb.getId());
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
	
	private boolean updateTeachingUnit(TeachingUnit tu, Unita unita) {
		boolean update = false;
		Map<String, Typology> classifications = new HashMap<String, Typology>();
		if(Utils.isNotEmpty(unita.getOrdinescuola())) {
			Typology typology = new Typology();
			typology.setQualifiedName(Const.TYPOLOGY_QNAME_ORDINE);
			typology.setName(unita.getOrdinescuola());
			classifications.put(Const.TYPOLOGY_QNAME_ORDINE, typology);
		}
		if(Utils.isNotEmpty(unita.getTiposcuola())) {
			Typology typology = new Typology();
			typology.setQualifiedName(Const.TYPOLOGY_QNAME_TIPOLOGIA);
			typology.setName(unita.getTiposcuola());
			classifications.put(Const.TYPOLOGY_QNAME_TIPOLOGIA, typology);
		}
		if(Utils.isNotEmpty(unita.getIndirizzodidattico())) {
			Typology typology = new Typology();
			typology.setQualifiedName(Const.TYPOLOGY_QNAME_INDIRIZZO);
			typology.setName(unita.getIndirizzodidattico());
			classifications.put(Const.TYPOLOGY_QNAME_INDIRIZZO, typology);
		}
		if(classifications.size() > 0) {
			tu.setClassifications(classifications);
			update = true;
		}
		return update;
	}
}
