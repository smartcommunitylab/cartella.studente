package it.smartcommunitylab.csengine.cv;

import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.model.CV;
import it.smartcommunitylab.csengine.model.Document;
import it.smartcommunitylab.csengine.model.Registration;
import it.smartcommunitylab.csengine.model.Student;
import it.smartcommunitylab.csengine.model.StudentExperience;
import it.smartcommunitylab.csengine.storage.RegistrationRepository;
import it.smartcommunitylab.csengine.storage.StudentExperienceRepository;
import it.smartcommunitylab.csengine.storage.StudentRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class CVTransformer {
	private static final transient Logger logger = LoggerFactory.getLogger(CVTransformer.class);
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private RegistrationRepository registrationRepository;
	
	@Autowired
	private StudentExperienceRepository studentExperienceRepository;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
	
	public void getCvTemplate(CV cv) {
		//Student
		Student student = studentRepository.findOne(cv.getStudentId());
		if(student != null) {
			cv.setStudent(student);
		}
		
		//Registrations
		Map<String, CVRegistration> registrationMap = new HashMap<String, CVRegistration>();
		for(String registrationId : cv.getRegistrationIdList()) {
			Registration registration = registrationRepository.findOne(registrationId);
			if(registration != null) {
				String key = registration.getTeachingUnitId() + "-" + registration.getCourse();
				CVRegistration cvRegistration = registrationMap.get(key);
				if(cvRegistration == null) {
					cvRegistration = new CVRegistration();
					cvRegistration.setCourse(registration.getCourse());
					cvRegistration.setDateFrom(sdf.format(registration.getDateFrom()));
					cvRegistration.setDateTo(sdf.format(registration.getDateTo()));
					cvRegistration.setInstituteName(registration.getInstitute().getName());
					cvRegistration.setTeachingUnit(registration.getTeachingUnit().getName());
				} else {
					//check registration period
					try {
						Date currentDateFrom = registration.getDateFrom();
						Date existingDateFrom = sdf.parse(cvRegistration.getDateFrom());
						if(currentDateFrom.before(existingDateFrom)) {
							cvRegistration.setDateFrom(sdf.format(currentDateFrom));
						}
						Date currentDateTo = registration.getDateTo();
						Date existingDateTo = sdf.parse(cvRegistration.getDateTo());
						if(currentDateTo.after(existingDateTo)) {
							cvRegistration.setDateTo(sdf.format(currentDateTo));
						}
					} catch (ParseException e) {
						logger.warn("getCvTemplate date parse error:" + e.getMessage());
					}
				}
				registrationMap.put(key, cvRegistration);
			}
		}
		List<CVRegistration> registrations = new ArrayList<CVRegistration>(registrationMap.values());
		Collections.sort(registrations, new Comparator<CVRegistration>() {

			@Override
			public int compare(CVRegistration arg0, CVRegistration arg1) {
				try {
					Date arg0DateFrom = sdf.parse(arg0.getDateFrom());
					Date arg1DateFrom = sdf.parse(arg1.getDateFrom());
					if(arg0DateFrom.before(arg1DateFrom)) {
						return -1;
					} else if(arg0DateFrom.after(arg1DateFrom)) {
						return 1;
					}
				} catch (ParseException e) {
					logger.warn("getCvTemplate date parse error:" + e.getMessage());
				}
				return 0;
			}
			
		});
		cv.setCvRegistrationList(registrations);
		
		//Mobility
		List<String> mobilityIdList = cv.getStudentExperienceIdMap().get(Const.EXP_TYPE_MOBILITY);
		if(mobilityIdList != null) {
			for(String experienceId : mobilityIdList) {
				StudentExperience studentExperience = studentExperienceRepository.findOne(experienceId);
				if(studentExperience != null) {
					CVMobility cvMobility = new CVMobility();
					cvMobility.setDateFrom(sdf.format(getExperienceDate(studentExperience, Const.ATTR_DATEFROM)));
					cvMobility.setDateTo(sdf.format(getExperienceDate(studentExperience, Const.ATTR_DATETO)));
					cvMobility.setTitle((String) studentExperience.getExperience()
							.getAttributes().get(Const.ATTR_TITLE));
					cvMobility.setLocation((String) studentExperience.getExperience()
							.getAttributes().get(Const.ATTR_LOCATION));
					cv.getCvMobilityList().add(cvMobility);
					cv.getAttachments().addAll(getFileNames(cv, studentExperience));
				}
			}
			Collections.sort(cv.getCvMobilityList(), new Comparator<CVMobility>() {

				@Override
				public int compare(CVMobility arg0, CVMobility arg1) {
					try {
						Date arg0DateFrom = sdf.parse(arg0.getDateFrom());
						Date arg1DateFrom = sdf.parse(arg1.getDateFrom());
						if(arg0DateFrom.before(arg1DateFrom)) {
							return -1;
						} else if(arg0DateFrom.after(arg1DateFrom)) {
							return 1;
						}
					} catch (ParseException e) {
						logger.warn("getCvTemplate date parse error:" + e.getMessage());
					}
					return 0;
				}
				
			});			
		}
	
		//Stage
		List<String> stageIdList = cv.getStudentExperienceIdMap().get(Const.EXP_TYPE_STAGE);
		if(stageIdList != null) {
			for(String experienceId : stageIdList) {
				StudentExperience studentExperience = studentExperienceRepository.findOne(experienceId);
				if(studentExperience != null) {
					CVStage cvStage = new CVStage();
					cvStage.setDateFrom(sdf.format(getExperienceDate(studentExperience, Const.ATTR_DATEFROM)));
					cvStage.setDateTo(sdf.format(getExperienceDate(studentExperience, Const.ATTR_DATETO)));
					cvStage.setTitle((String) studentExperience.getExperience()
							.getAttributes().get(Const.ATTR_TITLE));
					cvStage.setDescription((String) studentExperience.getExperience()
							.getAttributes().get(Const.ATTR_DESCRIPTION));
					cvStage.setLocation((String) studentExperience.getExperience()
							.getAttributes().get(Const.ATTR_LOCATION));
					cvStage.setContact((String) studentExperience.getExperience()
							.getAttributes().get(Const.ATTR_CONTACT));
					cv.getCvStageList().add(cvStage);
					cv.getAttachments().addAll(getFileNames(cv, studentExperience));
				}
			}			
		}

		//Job
		List<String> jobIdList = cv.getStudentExperienceIdMap().get(Const.EXP_TYPE_JOB);
		if(jobIdList != null) {
			for(String experienceId : jobIdList) {
				StudentExperience studentExperience = studentExperienceRepository.findOne(experienceId);
				if(studentExperience != null) {
					CVStage cvStage = new CVStage();
					cvStage.setDateFrom(sdf.format(getExperienceDate(studentExperience, Const.ATTR_DATEFROM)));
					cvStage.setDateTo(sdf.format(getExperienceDate(studentExperience, Const.ATTR_DATETO)));
					cvStage.setTitle((String) studentExperience.getExperience()
							.getAttributes().get(Const.ATTR_TITLE));
					cvStage.setDescription((String) studentExperience.getExperience()
							.getAttributes().get(Const.ATTR_DESCRIPTION));
					cvStage.setLocation((String) studentExperience.getExperience()
							.getAttributes().get(Const.ATTR_LOCATION));
					cvStage.setContact((String) studentExperience.getExperience()
							.getAttributes().get(Const.ATTR_CONTACT));
					cv.getCvStageList().add(cvStage);
					cv.getAttachments().addAll(getFileNames(cv, studentExperience));
				}
			}			
		}
		Collections.sort(cv.getCvStageList(), new Comparator<CVStage>() {

			@Override
			public int compare(CVStage arg0, CVStage arg1) {
				try {
					Date arg0DateFrom = sdf.parse(arg0.getDateFrom());
					Date arg1DateFrom = sdf.parse(arg1.getDateFrom());
					if(arg0DateFrom.before(arg1DateFrom)) {
						return -1;
					} else if(arg0DateFrom.after(arg1DateFrom)) {
						return 1;
					}
				} catch (ParseException e) {
					logger.warn("getCvTemplate date parse error:" + e.getMessage());
				}
				return 0;
			}
			
		});

		//Certifications
		List<String> certificationIdList = cv.getStudentExperienceIdMap().get(Const.EXP_TYPE_CERT);
		if(certificationIdList != null) {
			for(String experienceId : certificationIdList) {
				StudentExperience studentExperience = studentExperienceRepository.findOne(experienceId);
				if(studentExperience != null) {
					String type = (String) studentExperience.getExperience().getAttributes().get(Const.ATTR_TYPE);
					if(Utils.isNotEmpty(type) && type.equals(Const.CERT_TYPE_LANG)) {
						CVLangCertification cvLangCert = new CVLangCertification();
						cvLangCert.setDateFrom(sdf.format(getExperienceDate(studentExperience, Const.ATTR_DATEFROM)));
						cvLangCert.setDateTo(sdf.format(getExperienceDate(studentExperience, Const.ATTR_DATETO)));
						cvLangCert.setName((String) studentExperience.getExperience()
								.getAttributes().get(Const.ATTR_TITLE));
						cvLangCert.setLang((String) studentExperience.getExperience()
								.getAttributes().get(Const.ATTR_LANG));
						String level = (String) studentExperience.getExperience()
								.getAttributes().get(Const.ATTR_LEVEL);
						if(Utils.isNotEmpty(level)) {
							cvLangCert.setLevel(level);
							if(level.equalsIgnoreCase("A1") || level.equalsIgnoreCase("A2")) {
								cvLangCert.setMappedLevel("Utente base");
							} else if(level.equalsIgnoreCase("B1") || level.equalsIgnoreCase("B2")) {
								cvLangCert.setMappedLevel("Utente intermedio");
							} else if(level.equalsIgnoreCase("C1") || level.equalsIgnoreCase("C2")) {
								cvLangCert.setMappedLevel("Utente avanzato");
							}
						}
						cv.getCvLangCertList().add(cvLangCert);
						cv.getAttachments().addAll(getFileNames(cv, studentExperience));
					}
				}
			}
			Collections.sort(cv.getCvLangCertList(), new Comparator<CVLangCertification>() {

				@Override
				public int compare(CVLangCertification arg0, CVLangCertification arg1) {
					try {
						Date arg0DateFrom = sdf.parse(arg0.getDateFrom());
						Date arg1DateFrom = sdf.parse(arg1.getDateFrom());
						if(arg0DateFrom.before(arg1DateFrom)) {
							return -1;
						} else if(arg0DateFrom.after(arg1DateFrom)) {
							return 1;
						}
					} catch (ParseException e) {
						logger.warn("getCvTemplate date parse error:" + e.getMessage());
					}
					return 0;
				}
				
			});
		}		
	}

	private List<String> getFileNames(CV cv, StudentExperience studentExperience) {
		List<String> result = new ArrayList<String>();
		for(Document document : studentExperience.getDocuments()) {
			if(cv.getStorageIdList().contains(document.getStorageId())) {
				String title = (String) document.getAttributes().get(Const.ATTR_TITLE);
				if(Utils.isNotEmpty(title)) {
					result.add(title);
				} else {
					result.add(document.getFilename());
				}
			}
		}
		return result;
	}
	
	private Date getExperienceDate(StudentExperience studentExperience, String attribute) {
		if(studentExperience.getExperience().getAttributes().get(attribute) instanceof Date) {
			return (Date) studentExperience.getExperience().getAttributes().get(attribute);
		} else if(studentExperience.getExperience().getAttributes().get(attribute) instanceof Long) {
			return new Date((Long) studentExperience.getExperience().getAttributes().get(attribute));
		} else {
			return null;
		}
	}
	
}
