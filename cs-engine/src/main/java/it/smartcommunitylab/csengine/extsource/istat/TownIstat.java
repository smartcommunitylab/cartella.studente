package it.smartcommunitylab.csengine.extsource.istat;

public class TownIstat {

	private String name;
	private String istat;
	private Double lat;
	private Double lon;

	private Double[] coords;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIstat() {
		return istat;
	}

	public void setIstat(String istat) {
		this.istat = istat;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public Double[] getCoords() {
		return coords;
	}

	public void setCoords(Double[] coords) {
		this.coords = coords;
	}

}
