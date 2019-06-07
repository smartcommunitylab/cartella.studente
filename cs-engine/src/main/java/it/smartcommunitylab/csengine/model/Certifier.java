package it.smartcommunitylab.csengine.model;

import java.util.ArrayList;
import java.util.List;

public class Certifier extends BaseObject {
	private String name;
	private String description;
	private String cf;
	private String address;
	private String phone;
	private String email;
	private String pec;
	private Double[] geocode;
	private String businessName;
	private List<String> atecoCode = new ArrayList<>();
	private List<String> atecoDesc = new ArrayList<>();
	private int idTipoAzienda;

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

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public List<String> getAtecoCode() {
		return atecoCode;
	}

	public void setAtecoCode(List<String> atecoCode) {
		this.atecoCode = atecoCode;
	}

	public List<String> getAtecoDesc() {
		return atecoDesc;
	}

	public void setAtecoDesc(List<String> atecoDesc) {
		this.atecoDesc = atecoDesc;
	}

	public int getIdTipoAzienda() {
		return idTipoAzienda;
	}

	public void setIdTipoAzienda(int idTipoAzienda) {
		this.idTipoAzienda = idTipoAzienda;
	}

}
