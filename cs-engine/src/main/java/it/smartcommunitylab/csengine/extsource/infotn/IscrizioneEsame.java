package it.smartcommunitylab.csengine.extsource.infotn;

public class IscrizioneEsame {
	private String extId;
	private String origin;
	private Esame examRef;
	private EsameStudent students;

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getExtId() {
		return extId;
	}

	public void setExtId(String extId) {
		this.extId = extId;
	}

	public Esame getExamRef() {
		return examRef;
	}

	public void setExamRef(Esame examRef) {
		this.examRef = examRef;
	}

	public EsameStudent getStudents() {
		return students;
	}

	public void setStudents(EsameStudent students) {
		this.students = students;
	}

}
