package it.smartcommunitylab.csengine.extsource.infotn;

public class IscrizioneStage {
	private String extId;
	private String origin;
	private Stage stageRef;
	private StageStudent student;

	// private String origin_course;
	// private String extid_course;
	// private String origin_student;
	// private String extid_student;
	// private String origin_stage;
	// private String extid_stage;

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

	public Stage getStageRef() {
		return stageRef;
	}

	public void setStageRef(Stage stageRef) {
		this.stageRef = stageRef;
	}

	public StageStudent getStudent() {
		return student;
	}

	public void setStudent(StageStudent student) {
		this.student = student;
	}

}
