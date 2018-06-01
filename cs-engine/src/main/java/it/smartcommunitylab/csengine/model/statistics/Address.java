package it.smartcommunitylab.csengine.model.statistics;

public class Address {
	
	private String addressCountry;

	private String addressLocality;

	public String getAddressCountry() {
		return this.addressCountry;
	}

	public String getAddressLocality() {
		return this.addressLocality;
	}

	public void setAddressCountry(String addressCountry) {
		this.addressCountry = addressCountry;
	}

	public void setAddressLocality(String addressLocality) {
		this.addressLocality = addressLocality;
	}
}