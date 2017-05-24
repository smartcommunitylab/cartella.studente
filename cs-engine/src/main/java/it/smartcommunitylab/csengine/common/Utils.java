package it.smartcommunitylab.csengine.common;

import it.smartcommunitylab.csengine.model.Experience;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletRequest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Utils {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static ObjectMapper fullMapper = new ObjectMapper();
	static {
		Utils.fullMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Utils.fullMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
		Utils.fullMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		Utils.fullMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		Utils.fullMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
	}
	
	public static JsonNode readJsonFromString(String json) throws JsonParseException, JsonMappingException, IOException {
		return Utils.fullMapper.readValue(json, JsonNode.class);
	}
	
	public static JsonNode readJsonFromReader(Reader reader) throws JsonProcessingException, IOException {
		return Utils.fullMapper.readTree(reader);
	}
	
	public static <T> List<T> readJSONListFromInputStream(InputStream in, Class<T> cls)
			throws IOException {
		List<Object> list = Utils.fullMapper.readValue(in, new TypeReference<List<?>>() {
		});
		List<T> result = new ArrayList<T>();
		for (Object o : list) {
			result.add(Utils.fullMapper.convertValue(o, cls));
		}
		return result;
	}
	
	public static <T> T toObject(Object in, Class<T> cls) {
		return Utils.fullMapper.convertValue(in, cls);
	}

	public static <T> T toObject(JsonNode in, Class<T> cls) throws JsonProcessingException {
		return Utils.fullMapper.treeToValue(in, cls);
	}
	
	public static boolean isNotEmpty(String value) {
		boolean result = false;
		if ((value != null) && (!value.isEmpty())) {
			result = true;
		}
		return result;
	}
	
	public static boolean isEmpty(String value) {
		boolean result = true;
		if ((value != null) && (!value.isEmpty())) {
			result = false;
		}
		return result;
	}
	
	public static String getUUID() {
		return UUID.randomUUID().toString();
	}

	public static String getString(Map<String, String> data, String lang, String defaultLang) {
		String result = null;
		if(data.containsKey(lang)) {
			result = data.get(lang);
		} else {
			result = data.get(defaultLang);
		}
		return result;
	}

	public static Map<String,String> handleError(Exception exception) {
		Map<String,String> errorMap = new HashMap<String,String>();
		errorMap.put(Const.ERRORTYPE, exception.getClass().toString());
		errorMap.put(Const.ERRORMSG, exception.getMessage());
		return errorMap;
	}
	
	public static boolean validateAPIRequest(ServletRequest req, String apiToken) {
//		boolean result = false;
//		HttpServletRequest request = (HttpServletRequest) req;
//		String tokenArrived = request.getHeader("X-ACCESS-TOKEN");
//		if(isNotEmpty(tokenArrived)) {
//			if(tokenArrived.equals(apiToken)) {
//				result = true;
//			}
//		}
//		return result;
		return true;
	}
	
	public static Date parseDate(String stringDate) throws ParseException {
		return sdf.parse(stringDate);
	}
	
	public static String formatDate(Date date) {
		return sdf.format(date);
	}
	
	public static String formatDate(long timestamp) {
		return sdf.format(new Date(timestamp));
	}
	
	public static boolean isCertified(Experience experience) {
		boolean result = false;
		if(experience!= null) {
			Boolean certified = (Boolean) experience.getAttributes().get(Const.ATTR_CERTIFIED);
			if(certified != null) {
				result = certified.booleanValue();
			}
		}
		return result;
	}
	
	public static <T> Collection<T> getNullableClause(T param) {
		Collection<T> coll = new ArrayList<T>();
		coll.add(param);
		coll.add(null);
		return coll;
	}
	
}
