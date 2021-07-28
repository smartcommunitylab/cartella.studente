package it.smartcommunitylab.csengine.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import it.smartcommunitylab.csengine.storage.RepositoryManager;

@Controller
public class DevController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(DevController.class);
	
	@Autowired
	RepositoryManager repositoryManager;
	
	@RequestMapping(value = "/dev/clean/institute", method = RequestMethod.POST)
	public @ResponseBody String deleteNonActiveInstitute() {
		logger.info("start deleteNonActiveInstitute");
		repositoryManager.deleteNonActiveInstitute();
		logger.info("end deleteNonActiveInstitute");
		return "OK";
	}

	@RequestMapping(value = "/dev/clean/tu", method = RequestMethod.POST)
	public @ResponseBody String deleteNonActiveTeachingUnit() {
		logger.info("start deleteNonActiveTeachingUnit");
		repositoryManager.deleteNonActiveTeachingUnit();
		logger.info("end deleteNonActiveTeachingUnit");
		return "OK";
	}

	@RequestMapping(value = "/dev/clean/course", method = RequestMethod.POST)
	public @ResponseBody String deleteNonActiveCourse() {
		logger.info("start deleteNonActiveCourse");
		repositoryManager.deleteNonActiveCourse();
		logger.info("end deleteNonActiveCourse");
		return "OK";
	}

	@RequestMapping(value = "/dev/clean/registration", method = RequestMethod.POST)
	public @ResponseBody String deleteNonActiveRegistration() {
		logger.info("start deleteNonActiveRegistration");
		repositoryManager.deleteNonActiveRegistration();
		logger.info("end deleteNonActiveRegistration");
		return "OK";
	}

	@RequestMapping(value = "/dev/clean/student", method = RequestMethod.POST)
	public @ResponseBody String deleteNonActiveStudent() {
		logger.info("start deleteNonActiveStudent");
		repositoryManager.deleteNonActiveStudent();
		logger.info("end deleteNonActiveStudent");
		return "OK";
	}

}
