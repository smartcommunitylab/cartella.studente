package it.smartcommunitylab.csengine.model.stats;

import java.util.ArrayList;
import java.util.List;

public class RegistrationStats {
	private String year;
	private List<KeyValue> values = new ArrayList<KeyValue>();
	
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public List<KeyValue> getValues() {
		return values;
	}
	public void setValues(List<KeyValue> values) {
		this.values = values;
	}
	
}
