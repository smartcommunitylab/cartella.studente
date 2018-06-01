package it.smartcommunitylab.csengine.model.statistics;

public class Location {
	
	private String type;

	private Double[] coordinates;

	public Double[] getCoordinates() {
		return this.coordinates;
	}

	public String getType() {
		return this.type;
	}

	public void setCoordinates(Double[] coordinates) {
		this.coordinates = coordinates;
	}

	public void setType(String type) {
		this.type = type;
	}
}