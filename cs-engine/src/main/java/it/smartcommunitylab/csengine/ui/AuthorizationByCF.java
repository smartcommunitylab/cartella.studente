package it.smartcommunitylab.csengine.ui;

import it.smartcommunitylab.aac.authorization.beans.AuthorizationResourceDTO;

import java.util.ArrayList;
import java.util.List;

public class AuthorizationByCF {
	private List<String> actions = new ArrayList<String>();
	private AuthorizationResourceDTO resource;
	private String cf;
	
	public List<String> getActions() {
		return actions;
	}
	public void setActions(List<String> actions) {
		this.actions = actions;
	}
	public AuthorizationResourceDTO getResource() {
		return resource;
	}
	public void setResource(AuthorizationResourceDTO resource) {
		this.resource = resource;
	}
	public String getCf() {
		return cf;
	}
	public void setCf(String cf) {
		this.cf = cf;
	}
}
