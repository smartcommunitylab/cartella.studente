package it.smartcommunitylab.csengine.controller.ext;

import java.io.File;
import java.io.FileReader;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import it.smartcommunitylab.csengine.extsource.csv.ImportFromCsv;
import it.smartcommunitylab.csengine.extsource.istat.IstatLookup;

@Controller
public class DataCompletionController {
	private static Log logger = LogFactory.getLog(DataCompletionController.class);
	
	@Autowired
	private IstatLookup istatLookup;
	
	@Autowired
	ImportFromCsv csvManager;

	@RequestMapping(value = "/extsource/istituzioni/update/istat", method = RequestMethod.GET)
	public @ResponseBody String importIstituzioniFromEmpty() throws Exception {
		String r1 = istatLookup.addIstatCodeToTeachingUnits();
		String r2 = istatLookup.addIstatCodeToInstitutes();
//		String r3 = istatLookup.geocodeCertifier();
		return r1 + "," + r2;
	}	
	
	@RequestMapping(value = "/extsource/user/student/import", method = RequestMethod.GET)
	public @ResponseBody void importStudent(@RequestParam String filePath, 
			@RequestParam(required=false, defaultValue="false") boolean addAuth, 
			HttpServletRequest request) throws Exception {
		File file = new File(filePath);
		if(file.exists()) {
			FileReader fileReader = new FileReader(file);
			csvManager.importStudent(fileReader, addAuth);
			logger.warn("importStudent - file imported:" + filePath);
		} else {
			logger.warn("importStudent - file doesn't exists:" + filePath);
		}
	}	
	
}
