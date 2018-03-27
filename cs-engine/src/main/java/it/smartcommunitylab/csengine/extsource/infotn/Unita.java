package it.smartcommunitylab.csengine.extsource.infotn;

public class Unita {
	private String origin;
	private String extId;
	private String dateFrom;
	private String dateTo;
	private Institute instituteRef;
	private TeachingUnit teachingUnit;

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getExtId() {
		return extId;
	}

	public void setExtId(String extId) {
		this.extId = extId;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public Institute getInstituteRef() {
		return instituteRef;
	}

	public void setInstituteRef(Institute instituteRef) {
		this.instituteRef = instituteRef;
	}

	public TeachingUnit getTeachingUnit() {
		return teachingUnit;
	}

	public void setTeachingUnit(TeachingUnit teachingUnit) {
		this.teachingUnit = teachingUnit;
	}

}

class Institute {

	private String extId;
	private String origin;

	public String getExtId() {
		return extId;
	}

	public void setExtId(String extId) {
		this.extId = extId;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

}

class TeachingUnit {

	private String address;
	private String mgIndirizzoDidattico;
	private String ordineScuola;
	private String tipoOrari;
	private String tipoScuola;
	private String name;
	private String description;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMgIndirizzoDidattico() {
		return mgIndirizzoDidattico;
	}

	public void setMgIndirizzoDidattico(String mgIndirizzoDidattico) {
		this.mgIndirizzoDidattico = mgIndirizzoDidattico;
	}

	public String getOrdineScuola() {
		return ordineScuola;
	}

	public void setOrdineScuola(String ordineScuola) {
		this.ordineScuola = ordineScuola;
	}

	public String getTipoOrari() {
		return tipoOrari;
	}

	public void setTipoOrari(String tipoOrari) {
		this.tipoOrari = tipoOrari;
	}

	public String getTipoScuola() {
		return tipoScuola;
	}

	public void setTipoScuola(String tipoScuola) {
		this.tipoScuola = tipoScuola;
	}

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

}
