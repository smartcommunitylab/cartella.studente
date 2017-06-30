package it.smartcommunitylab.csengine.security;

import it.smartcommunitylab.aac.AACAuthorizationService;
import it.smartcommunitylab.aac.AACException;
import it.smartcommunitylab.aac.AACService;
import it.smartcommunitylab.aac.authorization.beans.AuthorizationDTO;
import it.smartcommunitylab.aac.authorization.beans.AuthorizationNodeValueDTO;
import it.smartcommunitylab.aac.authorization.beans.AuthorizationResourceDTO;
import it.smartcommunitylab.aac.authorization.beans.AuthorizationUserDTO;
import it.smartcommunitylab.aac.model.TokenData;
import it.smartcommunitylab.csengine.exception.UnauthorizedException;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.httpclient.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


public class AuthorizationManager {
	@SuppressWarnings("unused")
	private static final transient Logger logger = LoggerFactory.getLogger(AuthorizationManager.class);
	
	@Autowired
	@Value("${oauth.serverUrl}")	
	private String oauthServerUrl;
	
	@Autowired
	@Value("${authorization.serverUrl}")	
	private String authorizationServerUrl;
	
	@Autowired
	@Value("${authorization.clientId}")	
	private String clientId;

	@Autowired
	@Value("${authorization.clientSecret}")	
	private String clientSecret;
	
	@Autowired
	@Value("${authorization.domain}")	
	private String clientDomain;
	
	@Autowired
	@Value("${authorization.userType}")	
	private String userType;
	
	private TokenData tokenData = null;
	
	private HttpClient httpClient = null;
	
	private AACService aacService = null;
	
	private AACAuthorizationService aacAuthorizationService = null;
	
	public AuthorizationManager() {
		httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(25000);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(25000);
	}
	
	@PostConstruct
	public void init() {
		aacService = new AACService(oauthServerUrl, clientId, clientSecret);
		aacAuthorizationService = new AACAuthorizationService(authorizationServerUrl);
	}
	
	private String refreshAuthToken() throws UnauthorizedException {
		if((tokenData == null) || isTokenExpired()) {
      try {
      	tokenData = aacService.generateClientToken(null);
	    } catch (Exception e) {
	    	throw new UnauthorizedException("error connectiong to oauth/token:" + e.getMessage());
			}
		}
		return tokenData.getAccess_token();
	}
	
	private boolean isTokenExpired() {
		boolean result = false;
		if(tokenData == null) {
			return true;
		}
		if(tokenData.getExpires_on() > 0) {
			long now = System.currentTimeMillis();
			if(now > tokenData.getExpires_on()) {
				result = true;
			}
		}
		return result;
	}
	
	public AuthorizationUserDTO getSubject(String subject) {
		AuthorizationUserDTO userDTO = new AuthorizationUserDTO();
		userDTO.setId(subject);
		userDTO.setType(userType);
		return userDTO;
	}
	
	public AuthorizationDTO getAuthorization(String subject, String action, String resourceName, 
			Map<String, String> attributes) {
		AuthorizationDTO result = new AuthorizationDTO();
		AuthorizationUserDTO userDTO = new AuthorizationUserDTO();
		userDTO.setId(subject);
		userDTO.setType(userType);
		result.setEntity(userDTO);
		result.setAction(action);
		AuthorizationResourceDTO resource= new AuthorizationResourceDTO();
		resource.setQnameRef(resourceName);
		resource.setValues(new ArrayList<AuthorizationNodeValueDTO>());
		for(String key : attributes.keySet()) {
			String value = attributes.get(key);
			int indexOf = key.lastIndexOf("-");
			String attrName = key.substring(indexOf + 1);
			String qname = key.substring(0, indexOf);
			AuthorizationNodeValueDTO nodeValue = new AuthorizationNodeValueDTO();
			nodeValue.setQname(qname);
			nodeValue.setName(attrName);
			nodeValue.setValue(value);
			resource.getValues().add(nodeValue);
		}
		result.setResource(resource);
		return result;
	}
	
	public boolean validateAuthorization(AuthorizationDTO authorization) 
			throws UnauthorizedException, SecurityException, AACException {
		boolean result = false;
		if(isTokenExpired()) {
			refreshAuthToken();	
		}
		result = aacAuthorizationService.validateAuthorization(tokenData.getAccess_token(), 
				clientDomain, authorization);
		return result;
	}
	
	public AuthorizationDTO insertAuthorization(AuthorizationDTO authorization) 
			throws UnauthorizedException, SecurityException, AACException {
		if(isTokenExpired()) {
			refreshAuthToken();	
		}
		AuthorizationDTO result = aacAuthorizationService.insertAuthorization(tokenData.getAccess_token(), 
				clientDomain, authorization);
		return result;
	}
	
	public void loadAuthSchema(String json) 
			throws SecurityException, AACException, UnauthorizedException {
		if(isTokenExpired()) {
			refreshAuthToken();
		}
		aacAuthorizationService.loadSchema(tokenData.getAccess_token(), clientDomain, json);
	}
	
	public void deleteAuthorization(String authId) 
			throws UnauthorizedException, SecurityException, AACException {
		if(isTokenExpired()) {
			refreshAuthToken();
		}
		aacAuthorizationService.removeAuthorization(tokenData.getAccess_token(), clientDomain, authId);
	}
	
}
