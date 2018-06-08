package it.smartcommunitylab.csengine.controller.ext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import it.smartcommunitylab.csengine.extsource.istat.IstatLookup;

@Controller
public class DataCompletionController {
	
	@Autowired
	private IstatLookup istatLookup;

	@RequestMapping(value = "/extsource/istituzioni/update/istat", method = RequestMethod.GET)
	public @ResponseBody String importIstituzioniFromEmpty() throws Exception {
		String r1 = istatLookup.addIstatCodeToTeachingUnits();
		String r2 = istatLookup.addIstatCodeToInstitutes();
//		String r3 = istatLookup.geocodeCertifier();
		return r1 + "," + r2;
	}	
	
}
