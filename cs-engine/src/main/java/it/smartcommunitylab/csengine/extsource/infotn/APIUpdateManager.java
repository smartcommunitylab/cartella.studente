package it.smartcommunitylab.csengine.extsource.infotn;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
				// API without schoolYear.
				importInfoTNIstituzioneManager.initIstituzioni(scheduleUpdate);
				importInfoTNUnitaManager.initUnita(scheduleUpdate);
				importInfoTNAziende.initAziende(scheduleUpdate);
				importInfoTNCourseMetaInfo.initCourseMetaInfo(scheduleUpdate);
				importInfoTNProfessori.initProfessori(scheduleUpdate);
				// API with schoolYear.
				importInfoTNCorsi.initCorsi(scheduleUpdate);
				importInfoTNStudenti.initStudenti(scheduleUpdate);
				importInfoTNIscrizioniCorsi.initIscrCorsi(scheduleUpdate);
				importInfoTnStage.initStage(scheduleUpdate);
				importInfoTnIscrizioneStage.initIscrStage(scheduleUpdate);
				importInfoTNEsami.initEsami(scheduleUpdate);
				importInfoTNIscrizioniEsame.initIscrzEsami(scheduleUpdate);
				importInfoTNMobilita.initIscrzMobilita(scheduleUpdate);
				importInfoTnCertificazioni.initIscrzCertficazioni(scheduleUpdate);
				importInfoTnProfesoriClassi.initProfessoriClassi(scheduleUpdate);

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

	// @Scheduled(cron = "0 58 23 * * ?")
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

}