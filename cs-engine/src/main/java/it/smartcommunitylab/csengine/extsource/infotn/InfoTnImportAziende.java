package it.smartcommunitylab.csengine.extsource.infotn;

import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.model.Certifier;
import it.smartcommunitylab.csengine.storage.CertifierRepository;

import java.io.FileReader;

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
public class InfoTnImportAziende {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportAziende.class);
	
	@Autowired
	@Value("${infotn.source.folder}")	
	private String sourceFolder;
	
	@Autowired
	CertifierRepository certifierRepository;
	
	public String importAziendeFromEmpty() throws Exception {
		logger.info("start importAziendeFromEmpty");
		int total = 0;
		int stored = 0;
		FileReader fileReader = new FileReader(sourceFolder + "FBK_COMPANY v.01.json");
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
						Azienda azienda = jp.readValueAs(Azienda.class);
						logger.info("converting " + azienda.getExtid());
						Certifier certifierDb = certifierRepository.findByExtId(azienda.getOrigin(), 
								azienda.getExtid());
						if(certifierDb != null) {
							logger.warn(String.format("Certifier already exists: %s - %s", 
									azienda.getOrigin(), azienda.getExtid()));
							continue;
						}
						Certifier certifier = convertToCertifier(azienda);
						certifierRepository.save(certifier);
						stored += 1;
						logger.info(String.format("Save TeachingUnit: %s - %s - %s", azienda.getOrigin(), 
								azienda.getExtid(), certifier.getId()));
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
	
	private Certifier convertToCertifier(Azienda azienda) {
		Certifier result = new Certifier();
		result.setOrigin(azienda.getOrigin());
		result.setExtId(azienda.getExtid());
		result.setId(Utils.getUUID());
		result.setCf(azienda.getPartita_iva());
		result.setName(azienda.getDescription());
		result.setAddress(azienda.getAddress());
		result.setPhone(azienda.getPhone());
		result.setEmail(azienda.getEmail());
		return result;
	}
}
