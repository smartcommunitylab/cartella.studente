package it.smartcommunitylab.csengine.controller.ext;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.exception.EntityNotFoundException;
import it.smartcommunitylab.csengine.exception.StorageException;
import it.smartcommunitylab.csengine.exception.UnauthorizedException;
import it.smartcommunitylab.csengine.extsource.infotn.InfoTnImportAziende;
import it.smartcommunitylab.csengine.extsource.infotn.InfoTnImportCertificazioni;
import it.smartcommunitylab.csengine.extsource.infotn.InfoTnImportCorsi;
import it.smartcommunitylab.csengine.extsource.infotn.InfoTnImportCourseMetaInfo;
import it.smartcommunitylab.csengine.extsource.infotn.InfoTnImportEsami;
import it.smartcommunitylab.csengine.extsource.infotn.InfoTnImportIscrizioneCorsi;
import it.smartcommunitylab.csengine.extsource.infotn.InfoTnImportIscrizioneEsami;
import it.smartcommunitylab.csengine.extsource.infotn.InfoTnImportIscrizioneStage;
import it.smartcommunitylab.csengine.extsource.infotn.InfoTnImportIstituzioni;
import it.smartcommunitylab.csengine.extsource.infotn.InfoTnImportMobilita;
import it.smartcommunitylab.csengine.extsource.infotn.InfoTnImportProfessori;
import it.smartcommunitylab.csengine.extsource.infotn.InfoTnImportProfessoriClassi;
import it.smartcommunitylab.csengine.extsource.infotn.InfoTnImportStage;
import it.smartcommunitylab.csengine.extsource.infotn.InfoTnImportStudenti;
import it.smartcommunitylab.csengine.extsource.infotn.InfoTnImportUnita;
import it.smartcommunitylab.csengine.extsource.infotn.InfoTnUpdateUnita;
import it.smartcommunitylab.csengine.model.StudentAuth;

@Controller
public class InfoTnController {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnController.class);

	@Autowired
	InfoTnImportIstituzioni importIstituzioni;

	@Autowired
	InfoTnImportUnita importUnita;

	@Autowired
	InfoTnImportCorsi importCorsi;

	@Autowired
	InfoTnImportStudenti importStudenti;

	@Autowired
	InfoTnImportAziende importAziende;

	@Autowired
	InfoTnImportIscrizioneCorsi importIscrizioneCorsi;

	@Autowired
	InfoTnImportEsami importEsami;

	@Autowired
	InfoTnImportIscrizioneEsami importIscrizioneEsami;

	@Autowired
	InfoTnImportStage importStage;

	@Autowired
	InfoTnImportIscrizioneStage importIscrizioneStage;

	@Autowired
	InfoTnImportCertificazioni importCertificazioni;

	@Autowired
	InfoTnImportMobilita importMobilita;

	@Autowired
	InfoTnUpdateUnita updateUnita;

	@Autowired
	InfoTnImportCourseMetaInfo importCourseMetaInfo;
	
	@Autowired
	InfoTnImportProfessori importProfessori;
	
	@Autowired
	InfoTnImportProfessoriClassi importProfessoriClassi;

	@RequestMapping(value = "/extsource/infotn/import/all/empty", method = RequestMethod.GET)
	public @ResponseBody String importAllFromEmpty() throws Exception {
		return importIstituzioni.importAll();
	}

	@RequestMapping(value = "/extsource/infotn/istituzioni/empty", method = RequestMethod.GET)
	public @ResponseBody String importIstituzioniFromEmpty() throws Exception {
		return importIstituzioni.importIstituzioniFromRESTAPI();
	}

	@RequestMapping(value = "/extsource/infotn/unita/empty", method = RequestMethod.GET)
	public @ResponseBody String importUnitaFromEmpty() throws Exception {
		return importUnita.importUnitaFromRESTAPI();
	}

	@RequestMapping(value = "/extsource/infotn/course/meta/info/empty", method = RequestMethod.GET)
	public @ResponseBody String importCousreMetaInfo() throws Exception {
		return importCourseMetaInfo.importCourseMetaInfoFromRESTAPI();
	}

	@RequestMapping(value = "/extsource/infotn/corsi/empty", method = RequestMethod.GET)
	public @ResponseBody String importCorsiFromEmpty() throws Exception {
		return importCorsi.importCorsiFromRESTAPI();
	}

	@RequestMapping(value = "/extsource/infotn/studenti/empty", method = RequestMethod.GET)
	public @ResponseBody String importStudentiFromEmpty() throws Exception {
		return importStudenti.importStudentiFromRESTAPI();
	}

	@RequestMapping(value = "/extsource/infotn/aziende/empty", method = RequestMethod.GET)
	public @ResponseBody String importAziendeFromEmpty() throws Exception {
		return importAziende.importAziendaFromRESTAPI();
	}

	@RequestMapping(value = "/extsource/infotn/iscrizionecorsi/empty", method = RequestMethod.GET)
	public @ResponseBody String importIscrizioneCorsiFromEmpty() throws Exception {
		return importIscrizioneCorsi.importIscrizioneCorsiFromRESTAPI();
	}

	@RequestMapping(value = "/extsource/infotn/esami/empty", method = RequestMethod.GET)
	public @ResponseBody String importEsamiFromEmpty() throws Exception {
		return importEsami.importEsamiFromRESTAPI();
	}

	@RequestMapping(value = "/extsource/infotn/iscrizioneesami/empty", method = RequestMethod.GET)
	public @ResponseBody String importIscrizioneEsamiFromEmpty() throws Exception {
		return importIscrizioneEsami.importIscrizioneEsamiFromRESTAPI();
	}

	@RequestMapping(value = "/extsource/infotn/stage/empty", method = RequestMethod.GET)
	public @ResponseBody String importStageFromEmpty() throws Exception {
		return importStage.importStageFromRESTAPI();
	}

	@RequestMapping(value = "/extsource/infotn/iscrizionestage/empty", method = RequestMethod.GET)
	public @ResponseBody String importIscrizioneStageFromEmpty() throws Exception {
		return importIscrizioneStage.importPartecipazioneStageFromRESTAPI();
	}

	@RequestMapping(value = "/extsource/infotn/certificazioni/empty", method = RequestMethod.GET)
	public @ResponseBody String importCertificazioniFromEmpty() throws Exception {
		return importCertificazioni.importIscrizioneCertificazioneFromRESTAPI();
	}

	@RequestMapping(value = "/extsource/infotn/mobilita/empty", method = RequestMethod.GET)
	public @ResponseBody String importMobilitaFromEmpty() throws Exception {
		return importMobilita.importIscrizioneMobilitaFromRESTAPI();
	}

	@RequestMapping(value = "/extsource/infotn/unita/update/clasification", method = RequestMethod.GET)
	public @ResponseBody String upateUnitaClassificazione() throws Exception {
		return updateUnita.upateUnitaClassificazione();
	}
	
	@RequestMapping(value = "/extsource/infotn/iscrizionecorsi/year", method = RequestMethod.GET)
	public @ResponseBody void importIscrizioneCorsiForYear(@RequestParam String schoolYear) throws Exception {
		importCorsi.importCorsiForYear(schoolYear);
		importIscrizioneCorsi.importIscrizioneCorsiForYear(schoolYear);
	}
	
	@RequestMapping(value = "/extsource/infotn/addStudentConsent/cf/{authCf}", method = RequestMethod.POST)
	public @ResponseBody void addStudentConsent(@PathVariable String authCf, HttpServletRequest request)
			throws Exception {
		importStudenti.addConsent(authCf);
	}
	
	@RequestMapping(value = "/extsource/infotn/professori/empty", method = RequestMethod.GET)
	public @ResponseBody String importProfessoriFromEmpty() throws Exception {
		return importProfessori.importProfessoriFromRESTAPI();
	}
	
	@RequestMapping(value = "/extsource/infotn/professoriclassi/empty", method = RequestMethod.GET)
	public @ResponseBody String importProfessoriClassiFromEmpty() throws Exception {
		return importProfessoriClassi.importProfessoriClassiFromRESTAPI();
	}

	@ExceptionHandler({ EntityNotFoundException.class, StorageException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Map<String, String> handleEntityNotFoundError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}

	@ExceptionHandler(UnauthorizedException.class)
	@ResponseStatus(value = HttpStatus.FORBIDDEN)
	@ResponseBody
	public Map<String, String> handleUnauthorizedError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Map<String, String> handleGenericError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}

}
