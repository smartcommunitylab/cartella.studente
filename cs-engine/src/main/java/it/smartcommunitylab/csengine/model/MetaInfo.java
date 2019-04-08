package it.smartcommunitylab.csengine.model;

public class MetaInfo {
	private String name;
	private long epocTimestamp = -1;
	private int totalRead;
	private int totalStore;
	private int schoolYear = -1;
	private boolean blocked;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getEpocTimestamp() {
		return epocTimestamp;
	}

	public void setEpocTimestamp(long epocTimestamp) {
		this.epocTimestamp = epocTimestamp;
	}

	public int getTotalRead() {
		return totalRead;
	}

	public void setTotalRead(int totalRead) {
		this.totalRead = totalRead;
	}

	public int getTotalStore() {
		return totalStore;
	}

	public void setTotalStore(int totalStore) {
		this.totalStore = totalStore;
	}

	public int getSchoolYear() {
		return schoolYear;
	}

	public void setSchoolYear(int schoolYear) {
		this.schoolYear = schoolYear;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

}
