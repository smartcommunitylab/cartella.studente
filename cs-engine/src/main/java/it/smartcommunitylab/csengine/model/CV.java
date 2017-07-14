package it.smartcommunitylab.csengine.model;

import it.smartcommunitylab.csengine.cv.CVLangCertification;
import it.smartcommunitylab.csengine.cv.CVMobility;
import it.smartcommunitylab.csengine.cv.CVRegistration;
import it.smartcommunitylab.csengine.cv.CVStage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Transient;

public class CV extends BaseObject {
	private String studentId;
	private Map<String, List<String>> studentExperienceIdMap = new HashMap<String, List<String>>();
	private List<String> registrationIdList = new ArrayList<String>();
	private List<String> storageIdList = new ArrayList<String>();
	@Transient
	private List<CVRegistration> cvRegistrationList = new ArrayList<CVRegistration>();
	@Transient
	private List<CVMobility> cvMobilityList = new ArrayList<CVMobility>();
	@Transient
	private List<CVStage> cvStageList = new ArrayList<CVStage>();
	@Transient
	private List<CVLangCertification> cvLangCertList = new ArrayList<CVLangCertification>();
	@Transient
	private Student student;
	@Transient
	private List<String> attachments = new ArrayList<String>();
	
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public Map<String, List<String>> getStudentExperienceIdMap() {
		return studentExperienceIdMap;
	}
	public void setStudentExperienceIdMap(Map<String, List<String>> studentExperienceIdMap) {
		this.studentExperienceIdMap = studentExperienceIdMap;
	}
	public List<String> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<String> attachments) {
		this.attachments = attachments;
	}
	public List<CVRegistration> getCvRegistrationList() {
		return cvRegistrationList;
	}
	public void setCvRegistrationList(List<CVRegistration> cvRegistrationList) {
		this.cvRegistrationList = cvRegistrationList;
	}
	public List<CVMobility> getCvMobilityList() {
		return cvMobilityList;
	}
	public void setCvMobilityList(List<CVMobility> cvMobilityList) {
		this.cvMobilityList = cvMobilityList;
	}
	public List<CVStage> getCvStageList() {
		return cvStageList;
	}
	public void setCvStageList(List<CVStage> cvStageList) {
		this.cvStageList = cvStageList;
	}
	public List<CVLangCertification> getCvLangCertList() {
		return cvLangCertList;
	}
	public void setCvLangCertList(List<CVLangCertification> cvLangCertList) {
		this.cvLangCertList = cvLangCertList;
	}
	public List<String> getRegistrationIdList() {
		return registrationIdList;
	}
	public void setRegistrationIdList(List<String> registrationIdList) {
		this.registrationIdList = registrationIdList;
	}
	public List<String> getStorageIdList() {
		return storageIdList;
	}
	public void setStorageIdList(List<String> storageIdList) {
		this.storageIdList = storageIdList;
	}
}
