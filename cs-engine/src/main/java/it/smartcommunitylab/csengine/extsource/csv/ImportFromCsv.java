package it.smartcommunitylab.csengine.extsource.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.aac.authorization.beans.AccountAttributeDTO;
import it.smartcommunitylab.aac.authorization.beans.AuthorizationDTO;
import it.smartcommunitylab.aac.authorization.beans.AuthorizationUserDTO;
import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.model.Student;
import it.smartcommunitylab.csengine.model.User;
import it.smartcommunitylab.csengine.security.AuthorizationManager;
import it.smartcommunitylab.csengine.storage.StudentRepository;
import it.smartcommunitylab.csengine.storage.UserRepository;

@Component
public class ImportFromCsv {
	private static Log logger = LogFactory.getLog(ImportFromCsv.class);
	
	@Value("${authorization.userType}")
	private String userType;
	
	@Value("${profile.account}")
	private String profileAccount;
	
	@Value("${profile.attribute}")
	private String profileAttribute;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	AuthorizationManager authorizationManager;
	
	/**
	 * Create ASLUser starting from csv info and studenteRepository.
	 * @param addAuth 
	 * @param csv : [student-cf,student-email]
	 * @return
	 */
	public List<User> importStudent(Reader contentReader, boolean addAuth) {
		BufferedReader in = new BufferedReader(contentReader);
		List<User> result = new ArrayList<>();
		String line;
		try {
			while((line = in.readLine()) != null) {
				try {
					String[] strings = line.split(";");
					String cf = strings[0].toUpperCase().trim();
					String email = strings[1].trim();
					Student student = studentRepository.findByCF(cf);
					if(student == null) {
						logger.warn("importStudent: student not found - " + cf);
						continue;
					}
					User csUser = userRepository.findByCf(cf);
					if(csUser == null) {
						Date now = new Date();
						csUser = new User();
						csUser.setCf(cf);
						csUser.setEmail(email);
						csUser.setOrigin(student.getOrigin());
						csUser.setExtId(student.getExtId());
						csUser.setOriginalId(student.getId());
						csUser.setRole("student");
						csUser.setCreationDate(now);
						csUser.setLastUpdate(now);
						userRepository.save(csUser);
						result.add(csUser);
						logger.warn("importStudent: csUser created - " + cf);
						if(addAuth) {
							// set autorizhation
							AccountAttributeDTO account = new AccountAttributeDTO();
							account.setAccountName(profileAccount);
							account.setAttributeName(profileAttribute);
							account.setAttributeValue(cf);
							AuthorizationUserDTO user = new AuthorizationUserDTO();
							user.setAccountAttribute(account);
							user.setType(userType);
							List<String> actions = new ArrayList<String>();
							actions.add(Const.AUTH_ACTION_ADD);
							actions.add(Const.AUTH_ACTION_DELETE);
							actions.add(Const.AUTH_ACTION_READ);
							actions.add(Const.AUTH_ACTION_UPDATE);
							Map<String, String> attributes = new HashMap<String, String>();
							attributes.put("student-studentId", student.getId());
							AuthorizationDTO authorization = authorizationManager.getNewAuthorization(user, user, actions, "student",
									attributes);
							try {
								authorizationManager.insertAuthorization(authorization);
							} catch (Exception e) {
								logger.warn(String.format("Error creating authorization: %s -%s - %s", student.getOrigin(),
										student.getExtId(), e.getMessage()));
							}
						}
					} else {
						logger.warn("importStudent: csUser already present - " + cf);
					}
				} catch (Exception e) {
					logger.warn("importStudent:" + e.getMessage());
				}
			}
		} catch (IOException e) {
			logger.warn("importStudent:" + e.getMessage());
		}
		return result;
	}
	
}
