package it.smartcommunitylab.csengine.common;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

@Component
public class ErrorLabelManager {

	private Map<String, String> errorStrings;
	private static Log logger = LogFactory.getLog(ErrorLabelManager.class);

	@PostConstruct
	private void init() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		errorStrings = mapper.readValue(Resources.toString(Resources.getResource("errorstring.json"), Charsets.UTF_8),
				Map.class);
	}

	public String get(String type) {
		if (errorStrings.containsKey(type)) {
			return errorStrings.get(type);
		} else {
			logger.warn("missing error label in json: " + type);
			return errorStrings.get("error.generale");
		}
	}

	public Map getAll() {
		return errorStrings;
	}

}