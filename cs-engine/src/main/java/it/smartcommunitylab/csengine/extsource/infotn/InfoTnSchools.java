package it.smartcommunitylab.csengine.extsource.infotn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

@Component
public class InfoTnSchools {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnSchools.class);
	
	private ObjectMapper objectMapper;
	
	private HashMap<String, Scuola> schoolMap = new HashMap<>();
	
	@PostConstruct
	public void init() {
		try {
			objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			String json = Resources.toString(Resources.getResource("scuole_infotn.json"), Charsets.UTF_8);
			TypeReference<ArrayList<Scuola>> typeRef = new TypeReference<ArrayList<Scuola>>() {};
			List<Scuola> list = objectMapper.readValue(json, typeRef);
			for(Scuola scuola : list) {
				schoolMap.put(scuola.getExtId(), scuola);
			}
		} catch (Exception e) {
			logger.error("InfoTnSchools.init():" + e.getMessage());
		}
	}
	
	public Scuola getScuola(String extId) {
		return schoolMap.get(extId);
	}

}
