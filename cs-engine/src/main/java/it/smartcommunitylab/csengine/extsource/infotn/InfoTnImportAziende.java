package it.smartcommunitylab.csengine.extsource.infotn;

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
import it.smartcommunitylab.csengine.model.Certifier;
import it.smartcommunitylab.csengine.model.MetaInfo;
import it.smartcommunitylab.csengine.storage.CertifierRepository;
import it.smartcommunitylab.csengine.storage.MetaInfoRepository;

@Component
public class InfoTnImportAziende {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportAziende.class);

	@Autowired
	@Value("${infotn.source.folder}")
	private String sourceFolder;

	@Value("${infotn.api.url}")
	private String infoTNAPIUrl;

	@Value("${infotn.api.user}")
	private String user;

	@Value("${infotn.api.pass}")
	private String password;

	private String metaInfoName = "Azienda";

	@Autowired
	CertifierRepository certifierRepository;

	@Autowired
	MetaInfoRepository metaInfoRepository;

	// public String importAziendeFromEmpty() throws Exception {
	// logger.info("start importAziendeFromEmpty");
	// int total = 0;
	// int stored = 0;
	// FileReader fileReader = new FileReader(sourceFolder + "FBK_COMPANY
	// v.02.json");
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
	// Azienda azienda = jp.readValueAs(Azienda.class);
	// logger.info("converting " + azienda.getExtid());
	// Certifier certifierDb =
	// certifierRepository.findByExtId(azienda.getOrigin(),
	// azienda.getExtid());
	// if (certifierDb != null) {
	// logger.warn(String.format("Certifier already exists: %s - %s",
	// azienda.getOrigin(),
	// azienda.getExtid()));
	// continue;
	// }
	// Certifier certifier = convertToCertifier(azienda);
	// certifierRepository.save(certifier);
	// stored += 1;
	// logger.info(String.format("Save TeachingUnit: %s - %s - %s",
	// azienda.getOrigin(),
	// azienda.getExtid(), certifier.getId()));
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

	public String importAziendaFromRESTAPI() throws Exception {
		logger.info("start importAziendaFromRESTAPI");
		int total = 0;
		int stored = 0;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;

		// read epoc timestamp from db(if exist)
		MetaInfo metaInfo = metaInfoRepository.findOne(metaInfoName);
		if (metaInfo != null) {
			url = infoTNAPIUrl + "/azienda?timestamp=" + metaInfo.getEpocTimestamp();

		} else {
			metaInfo = new MetaInfo();
			metaInfo.setName(metaInfoName);
			url = infoTNAPIUrl + "/azienda";

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
				Azienda azienda = jp.readValueAs(Azienda.class);
				logger.info("converting " + azienda.getExtId());
				Certifier certifierDb = certifierRepository.findByExtId(azienda.getOrigin(), azienda.getExtId());
				if (certifierDb != null) {
					logger.warn(String.format("Certifier already exists: %s - %s", azienda.getOrigin(),
							azienda.getExtId()));
					continue;
				}
				Certifier certifier = convertToCertifier(azienda);
				certifierRepository.save(certifier);
				stored += 1;
				logger.info(String.format("Save Azienda: %s - %s - %s", azienda.getOrigin(), azienda.getExtId(),
						certifier.getId()));

			}

			// update time stamp (if all works fine).
			metaInfo.setEpocTimestamp(System.currentTimeMillis() / 1000);
			metaInfo.setTotalRead(total);
			metaInfo.setTotalStore(stored);
			metaInfoRepository.save(metaInfo);

		}

		return stored + "/" + total + "(" + metaInfo.getEpocTimestamp() + ")";

	}

	private Certifier convertToCertifier(Azienda azienda) {
		Certifier result = new Certifier();
		result.setOrigin(azienda.getOrigin());
		result.setExtId(azienda.getExtId());
		result.setId(Utils.getUUID());
		result.setCf(azienda.getPartita_iva());
		result.setName(azienda.getDescription());
		result.setAddress(azienda.getAddress());
		result.setPhone(azienda.getPhone());
		result.setEmail(azienda.getEmail());
		return result;
	}
}
