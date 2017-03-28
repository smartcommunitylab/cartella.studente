package it.smartcommunitylab.csengine.model;

import java.util.HashMap;
import java.util.Map;

public class Certificate extends BaseObject {
	private String experienceId;
	private Map<String, Object> attributes = new HashMap<String, Object>();
	private String documentUri;
	
	public String getExperienceId() {
		return experienceId;
	}
	public void setExperienceId(String experienceId) {
		this.experienceId = experienceId;
	}
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	public String getDocumentUri() {
		return documentUri;
	}
	public void setDocumentUri(String documentUri) {
		this.documentUri = documentUri;
	}
	
}
