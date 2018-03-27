package it.smartcommunitylab.csengine.model;

import org.springframework.data.annotation.Id;

public class MetaInfo {
	@Id
	private String name;
	private long epocTimestamp;
	private int totalRead;
	private int totalStore;

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

}
