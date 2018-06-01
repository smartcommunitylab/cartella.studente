package it.smartcommunitylab.csengine.model;

import java.util.HashMap;
import java.util.Map;

public class TeachingUnit extends BaseObject {
	private String name;
	private String description;
	private String cf;
	private String address;
	private String phone;
	private String email;
	private String pec;
	private Double[] geocode;
	private int geocodeAccuracy; // 0 = imported, 1 = geocoding, 2 = istat
	private String instituteId;
	private Map<String, Typology> classifications = new HashMap<String, Typology>();
	private String codiceIstat;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCf() {
		return cf;
	}
	public void setCf(String cf) {
		this.cf = cf;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPec() {
		return pec;
	}
	public void setPec(String pec) {
		this.pec = pec;
	}
	public Double[] getGeocode() {
		return geocode;
	}
	public void setGeocode(Double[] geocode) {
		this.geocode = geocode;
	}
	public int getGeocodeAccuracy() {
		return geocodeAccuracy;
	}
	public void setGeocodeAccuracy(int geocodeAccuracy) {
		this.geocodeAccuracy = geocodeAccuracy;
	}
	public String getInstituteId() {
		return instituteId;
	}
	public void setInstituteId(String instituteId) {
		this.instituteId = instituteId;
	}
	public Map<String, Typology> getClassifications() {
		return classifications;
	}
	public void setClassifications(Map<String, Typology> classifications) {
		this.classifications = classifications;
	}
	public String getCodiceIstat() {
		return codiceIstat;
	}
	public void setCodiceIstat(String codiceIstat) {
		this.codiceIstat = codiceIstat;
	}
	
}
