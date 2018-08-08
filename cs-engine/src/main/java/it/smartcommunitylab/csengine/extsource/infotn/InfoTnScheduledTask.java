package it.smartcommunitylab.csengine.extsource.infotn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InfoTnScheduledTask {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnScheduledTask.class);

	@Autowired
	private InfoTnImportIstituzioni importIstituzioni;
	
	@Autowired
	private InfoTnImportUnita importUnita;

	@Autowired
	private InfoTnImportAziende importAziende;

	@Autowired
	private InfoTnImportCorsi importCorsi;

	@Autowired
	private InfoTnImportStudenti importStudenti;

	@Autowired
	private InfoTnImportIscrizioneCorsi importIscrizioneCorsi;

	@Autowired
	private InfoTnImportStage importStage;

	@Autowired
	private InfoTnImportIscrizioneStage importIscrizioneStage;

	@Autowired
	private InfoTnImportCertificazioni importCertificazioni;

	@Autowired
	private InfoTnImportMobilita importMobilita;

	@Autowired
	private InfoTnImportEsami importEsami;

	@Autowired
	private InfoTnImportIscrizioneEsami importIscrizioneEsami;

	@Autowired
	private InfoTnImportCourseMetaInfo importCourseMetaInfo;

	@Autowired
	private InfoTnImportProfessori importProfessori;

	@Autowired
	private InfoTnImportProfessoriClassi importProfessoriClassi;

	// @Scheduled(cron = "0 58 23 * * ?")
	public String importAll() throws Exception {
		if(logger.isInfoEnabled()) {
			logger.info("start InfoTnScheduledTask.importAll");
		}
		// institute.
		importIstituzioni.importIstituzioniFromRESTAPI();
		// teaching unit.
		importUnita.importUnitaFromRESTAPI();
		// azienda
		importAziende.importAziendaFromRESTAPI();
		// course meta info.
		importCourseMetaInfo.importCourseMetaInfoFromRESTAPI();
		// courses.
		importCorsi.importCorsiFromRESTAPI();
		// student.
		importStudenti.importStudentiFromRESTAPI();
		// registration courses.
		importIscrizioneCorsi.importIscrizioneCorsiFromRESTAPI();
		// stage.
		importStage.importStageFromRESTAPI();
		importIscrizioneStage.importPartecipazioneStageFromRESTAPI();
		// esami.
		importEsami.importEsamiFromRESTAPI();
		importIscrizioneEsami.importIscrizioneEsamiFromRESTAPI();
		// mobilita.
		importMobilita.importIscrizioneMobilitaFromRESTAPI();
		// certificazione.
		importCertificazioni.importIscrizioneCertificazioneFromRESTAPI();
		// professori.
		importProfessori.importProfessoriFromRESTAPI();
		// professoriClassi.
		importProfessoriClassi.importProfessoriClassiFromRESTAPI();

		return "ok";
	}

}
