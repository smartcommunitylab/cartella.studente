package it.smartcommunitylab.csengine.controller;

import it.smartcommunitylab.aac.AACProfileService;
import it.smartcommunitylab.aac.model.AccountProfile;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.security.AuthorizationManager;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	@Value("${authorization.clientId}")	
	private String clientId;

	@Autowired
	@Value("${authorization.clientSecret}")	
	private String clientSecret;
	
	@Autowired
	@Value("${profile.serverUrl}")
	private String profileServerUrl;

	@Autowired
	@Value("${profile.account}")
	private String profileAccount;

	@Autowired
	@Value("${profile.attribute}")
	private String profileAttribute;
	
	@Autowired
	AuthorizationManager authorizationManager;

	private AACProfileService profileConnector;

	@PostConstruct
	public void init() throws Exception {
		profileConnector = new AACProfileService(profileServerUrl);
	}

	protected String getCF(AccountProfile accountProfile) {
		String result = null;
		if(accountProfile != null) {
			result = accountProfile.getAttribute(profileAccount, profileAttribute);
		}
		return result;
	}
	
	protected String getSubject(AccountProfile accountProfile) {
		String result = null;
		if(accountProfile != null) {
			result = accountProfile.getUserId();
		}
		return result;
	}
	
	protected AccountProfile getAccoutProfile(HttpServletRequest request) {
		AccountProfile result = null;
		String token = request.getHeader("Authorization");
		if (Utils.isNotEmpty(token)) {
			token = token.replace("Bearer ", "");
			try {
				result = profileConnector.findAccountProfile(token);
			} catch (Exception e) {
				if (logger.isWarnEnabled()) {
					logger.warn(String.format("getAccoutProfile[%s]: %s", token, e.getMessage()));
				}
			} 
		}
		return result;
	}	
}
