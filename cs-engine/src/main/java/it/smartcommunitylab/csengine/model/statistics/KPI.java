package it.smartcommunitylab.csengine.model.statistics;

import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

public class KPI {
	private String id;

	private String type = "KeyPerformanceIndicator";

	private String name;

	private String description;

	private List<String> category = Lists.newArrayList();

	private Organization organization;

	private Provider provider;

	private int kpiValue;

	private String currentStanding;

	private CalculationPeriod calculationPeriod;

	private String calculationMethod;

	private String calculationFrequency;

	private Date dateModified;

	private String dateNextCalculation;

	private Address address;

	public Address getAddress() {
		return this.address;
	}

	public String getCalculationFrequency() {
		return this.calculationFrequency;
	}

	public String getCalculationMethod() {
		return this.calculationMethod;
	}

	public CalculationPeriod getCalculationPeriod() {
		return this.calculationPeriod;
	}

	public List<String> getCategory() {
		return this.category;
	}

	public String getCurrentStanding() {
		return this.currentStanding;
	}

	public Date getDateModified() {
		return this.dateModified;
	}

	public String getDateNextCalculation() {
		return this.dateNextCalculation;
	}

	public String getDescription() {
		return this.description;
	}

	public String getId() {
		return this.id;
	}

	public int getKpiValue() {
		return this.kpiValue;
	}

	public String getName() {
		return this.name;
	}

	public Organization getOrganization() {
		return this.organization;
	}

	public Provider getProvider() {
		return this.provider;
	}

	public String getType() {
		return this.type;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public void setCalculationFrequency(String calculationFrequency) {
		this.calculationFrequency = calculationFrequency;
	}

	public void setCalculationMethod(String calculationMethod) {
		this.calculationMethod = calculationMethod;
	}

	public void setCalculationPeriod(CalculationPeriod calculationPeriod) {
		this.calculationPeriod = calculationPeriod;
	}

	public void setCategory(List<String> category) {
		this.category = category;
	}

	public void setCurrentStanding(String currentStanding) {
		this.currentStanding = currentStanding;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public void setDateNextCalculation(String dateNextCalculation) {
		this.dateNextCalculation = dateNextCalculation;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setKpiValue(int kpiValue) {
		this.kpiValue = kpiValue;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public void setType(String type) {
		this.type = type;
	}
}