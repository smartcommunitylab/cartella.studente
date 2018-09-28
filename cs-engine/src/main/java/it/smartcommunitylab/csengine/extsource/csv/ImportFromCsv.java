package it.smartcommunitylab.csengine.extsource.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.csengine.model.User;
import it.smartcommunitylab.csengine.model.Student;
import it.smartcommunitylab.csengine.storage.StudentRepository;
import it.smartcommunitylab.csengine.storage.UserRepository;

@Component
public class ImportFromCsv {
	private static Log logger = LogFactory.getLog(ImportFromCsv.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	/**
	 * Create ASLUser starting from csv info and studenteRepository.
	 * @param csv : [student-cf,student-email]
	 * @return
	 */
	public List<User> importStudent(Reader contentReader) {
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
