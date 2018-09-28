package it.smartcommunitylab.csengine.controller;

import it.smartcommunitylab.aac.AACProfileService;
import it.smartcommunitylab.aac.authorization.beans.AccountAttributeDTO;
import it.smartcommunitylab.aac.authorization.beans.AuthorizationUserDTO;
import it.smartcommunitylab.aac.model.AccountProfile;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.model.User;
import it.smartcommunitylab.csengine.security.AuthorizationManager;
import it.smartcommunitylab.csengine.storage.UserRepository;

import java.util.Map;

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
	@Value("${authorization.userType}")	
	private String userType;
	
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
	
	@Autowired
	UserRepository userRepository;

	private AACProfileService profileConnector;

	@PostConstruct
	public void init() throws Exception {
		profileConnector = new AACProfileService(profileServerUrl);
	}

	protected String getCF(AccountProfile accountProfile) {
		String result = null;
		if(accountProfile != null) {
			if (accountProfile.getAccounts().containsKey("adc")) {
				result = accountProfile.getAttribute("adc", "pat_attribute_codicefiscale"); 
			} else {
				Map<String, String> accountAttributes = null;
				if (accountProfile.getAccounts().containsKey("google")) {
					accountAttributes = accountProfile.getAccountAttributes("google");
				} else if (accountProfile.getAccounts().containsKey("facebook")) {
					accountAttributes = accountProfile.getAccountAttributes("facebook");
				} else if (accountProfile.getAccounts().containsKey("internal")) {
					accountAttributes = accountProfile.getAccountAttributes("internal");
				}
				String email = accountAttributes.get("email");
				User user = userRepository.findByEmail(email);
				if(user != null) {
					result = user.getCf();
				}
			}
		}
		//return result;
		//TODO TEST
		return "ABCDEF12G34H567I";
	}
	
	protected String getSubject(AccountProfile accountProfile) {
		String result = null;
		if(accountProfile != null) {
			result = accountProfile.getUserId();
		}
		//return result;
		//TODO TEST
		return "429";
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
	
	protected AccountAttributeDTO getAccountByCF(HttpServletRequest request) {
		String cf = getCF(getAccoutProfile(request));
		AccountAttributeDTO account = new AccountAttributeDTO();
		account.setAccountName(profileAccount);
		account.setAttributeName(profileAttribute);
		account.setAttributeValue(cf);
		return account;
	}
	
	protected AuthorizationUserDTO getUserByCF(HttpServletRequest request) {
		AccountAttributeDTO accountDTO = getAccountByCF(request);
		AuthorizationUserDTO userDTO = new AuthorizationUserDTO();
		userDTO.setAccountAttribute(accountDTO);
		userDTO.setType(userType);
		return userDTO;
	}
	
	protected AuthorizationUserDTO getUserByCF(String cf) {
		AccountAttributeDTO accountDTO = new AccountAttributeDTO();
		accountDTO.setAccountName(profileAccount);
		accountDTO.setAttributeName(profileAttribute);
		accountDTO.setAttributeValue(cf);
		AuthorizationUserDTO userDTO = new AuthorizationUserDTO();
		userDTO.setAccountAttribute(accountDTO);
		userDTO.setType(userType);
		return userDTO;
	}
}
