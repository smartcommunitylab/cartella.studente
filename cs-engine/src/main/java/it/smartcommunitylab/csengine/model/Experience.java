package it.smartcommunitylab.csengine.model;

import java.util.HashMap;
import java.util.Map;

public class Experience extends BaseObject {
	private String type;
	private Map<String, Object> attributes = new HashMap<String, Object>();
	
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
