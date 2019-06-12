package it.smartcommunitylab.csengine.extsource.infotn;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.csengine.model.MetaInfo;
import it.smartcommunitylab.csengine.model.ScheduleUpdate;
import it.smartcommunitylab.csengine.storage.ScheduleUpdateRepository;

@Component
public class APIUpdateManager {

	private static final transient Logger logger = LoggerFactory.getLogger(APIUpdateManager.class);

	private ScheduleUpdate scheduleUpdate;

	@Value("${infotn.api.url}")
	private String infoTNAPIUrl;
	@Value("${infotn.api.user}")
	private String user;
	@Value("${infotn.api.pass}")
	private String password;
	@Value("${infotn.starting.year}")
	private int startingYear;

	@Autowired
	private ScheduleUpdateRepository scheduleUpdateRepository;
	@Autowired
	private InfoTnImportIstituzioni importInfoTNIstituzioneManager;
	@Autowired
	private InfoTnImportUnita importInfoTNUnitaManager;
	@Autowired
	private InfoTnImportAziende importInfoTNAziende;
	@Autowired
	private InfoTnImportCourseMetaInfo importInfoTNCourseMetaInfo;
	@Autowired
	private InfoTnImportProfessori importInfoTNProfessori;
	@Autowired
	private InfoTnImportCorsi importInfoTNCorsi;
	@Autowired
	private InfoTnImportStudenti importInfoTNStudenti;
	@Autowired
	private InfoTnImportIscrizioneCorsi importInfoTNIscrizioniCorsi;
	@Autowired
	private InfoTnImportStage importInfoTnStage;
	@Autowired
	private InfoTnImportIscrizioneStage importInfoTnIscrizioneStage;
	@Autowired
	private InfoTnImportEsami importInfoTNEsami;
	@Autowired
	private InfoTnImportIscrizioneEsami importInfoTNIscrizioniEsame;
	@Autowired
	private InfoTnImportMobilita importInfoTNMobilita;
	@Autowired
	private InfoTnImportCertificazioni importInfoTnCertificazioni;
	@Autowired
	private InfoTnImportProfessoriClassi importInfoTnProfesoriClassi;

	@PostConstruct
	public void verifica() {
		// se c'è un oggetto ScheduledUpdate non fa nulla
		if (scheduleUpdateRepository.count() < 1) {
			if (logger.isInfoEnabled()) {
				logger.info("start InfoTnImportTask for fresh import");
			}
			scheduleUpdate = new ScheduleUpdate();
			try {

				scheduleUpdateRepository.save(scheduleUpdate);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public List<MetaInfo> fetchMetaInfoForAPI(String key) {

		List<MetaInfo> result = null;
		// get updated object from mongo.
		scheduleUpdate = getScheduleUpdate();
		if (scheduleUpdate != null && scheduleUpdate.getUpdateMap().containsKey(key)) {
			result = scheduleUpdate.getUpdateMap().get(key);
		}

		return result;

	}

	public void saveMetaInfoList(String key, List<MetaInfo> list) {
		scheduleUpdate.getUpdateMap().put(key, list);
		scheduleUpdateRepository.save(scheduleUpdate);
	}

	public ScheduleUpdate getScheduleUpdate() {
		// get update object from mongo.
		if (scheduleUpdateRepository.count() > 0) {
			scheduleUpdate = scheduleUpdateRepository.findAll().get(0);
		}
		return scheduleUpdate;
	}

	public void setScheduleUpdate(ScheduleUpdate scheduleUpdate) {
		this.scheduleUpdate = scheduleUpdate;
	}

	/**
	 * SCHEDULED TASK FOR REGISTRATION.
	 * RUN DAILY AT 22:58
	 * @throws Exception
	 */
//	@Scheduled(cron = "0 58 22 * * ?")
	public void importCartellaRegistration() throws Exception {

		if (logger.isInfoEnabled()) {
			logger.info("start ScheduledTask.importInfoTNRegistration(" + new Date() + ")");
		}
		// student.
		importInfoTNStudenti.importStudentiFromRESTAPI();
		// registration.
		importInfoTNIscrizioniCorsi.importIscrizioneCorsiFromRESTAPI();
		// teaching unit.
//		importInfoTNUnitaManager.importUnitaFromRESTAPI();
		// azienda.
		importInfoTNAziende.importAziendaFromRESTAPI();

	}

	public String importAll() throws Exception {

		if (logger.isInfoEnabled()) {
			logger.info("start InfoTnScheduledTask.importAll");
		}
		// institute.
		importInfoTNIstituzioneManager.importIstituzioniFromRESTAPI();
		// teaching unit.
		importInfoTNUnitaManager.importUnitaFromRESTAPI();
		// azienda
		importInfoTNAziende.importAziendaFromRESTAPI();
		// course meta info.
		importInfoTNCourseMetaInfo.importCourseMetaInfoFromRESTAPI();
		// professori.
		importInfoTNProfessori.importProfessoriFromRESTAPI();
		// courses.
		importInfoTNCorsi.importCorsiFromRESTAPI();
		// student.
		importInfoTNStudenti.importStudentiFromRESTAPI();
		// registration courses.
		importInfoTNIscrizioniCorsi.importIscrizioneCorsiFromRESTAPI();
		// stage.
		importInfoTnStage.importStageFromRESTAPI();
		importInfoTnIscrizioneStage.importPartecipazioneStageFromRESTAPI();
		// esami.
		importInfoTNEsami.importEsamiFromRESTAPI();
		importInfoTNIscrizioniEsame.importIscrizioneEsamiFromRESTAPI();
		// mobilita.
		importInfoTNMobilita.importIscrizioneMobilitaFromRESTAPI();
		// certificazione.
		importInfoTnCertificazioni.importIscrizioneCertificazioneFromRESTAPI();

		// professoriClassi.
		importInfoTnProfesoriClassi.importProfessoriClassiFromRESTAPI();

		return "ok";
	}

	public List<MetaInfo> createMetaInfoForAPI(String apiKey, boolean multipleYears) {

		// init.
		List<MetaInfo> metaInfos = new ArrayList<MetaInfo>();

		if (multipleYears) {

			for (int i = startingYear; i <= Calendar.getInstance().get(Calendar.YEAR); i++) {
				MetaInfo metaInfo = new MetaInfo();
				metaInfo.setName(apiKey);
				metaInfo.setSchoolYear(i);
				metaInfos.add(metaInfo);
			}

		} else {

			MetaInfo metaInfo = new MetaInfo();
			metaInfo.setName(apiKey);
			metaInfos.add(metaInfo);
		}

		scheduleUpdate.getUpdateMap().put(apiKey, metaInfos);

		return metaInfos;

	}

}
