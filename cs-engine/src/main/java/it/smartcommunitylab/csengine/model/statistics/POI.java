package it.smartcommunitylab.csengine.model.statistics;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class POI {
	
	private String id;

	private String type = "PointOfInterest";

	private String name;

	private String description;

	private Address address;

	private List<String> category = Lists.newArrayList();

	private Location location;

	private String source;

	private List<String> refSeeAlso = Lists.newArrayList();
	
	private Map<String, Object> metadata = Maps.newTreeMap();

	public Address getAddress() {
		return this.address;
	}

	public List<String> getCategory() {
		return this.category;
	}

	public String getDescription() {
		return this.description;
	}

	public String getId() {
		return this.id;
	}

	public Location getLocation() {
		return this.location;
	}

	public String getName() {
		return this.name;
	}

	public List<String> getRefSeeAlso() {
		return this.refSeeAlso;
	}

	public String getSource() {
		return this.source;
	}

	public String getType() {
		return this.type;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public void setCategory(List<String> category) {
		this.category = category;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRefSeeAlso(List<String> refSeeAlso) {
		this.refSeeAlso = refSeeAlso;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}

}
