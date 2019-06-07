package it.smartcommunitylab.csengine.extsource.infotn;

public class Azienda {
	private String extId;
	private String origin;
	private String address;
	private String description;
	private String email;
	private String name;
	private String partita_iva;
	private String pec;
	private String phone;
	private String datefrom;
	private String datefrom_headquarter;
	private int idTipoAzienda;

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDatefrom() {
		return datefrom;
	}

	public void setDatefrom(String datefrom) {
		this.datefrom = datefrom;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPartita_iva() {
		return partita_iva;
	}

	public void setPartita_iva(String partita_iva) {
		this.partita_iva = partita_iva;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDatefrom_headquarter() {
		return datefrom_headquarter;
	}

	public void setDatefrom_headquarter(String datefrom_headquarter) {
		this.datefrom_headquarter = datefrom_headquarter;
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

	public String getExtId() {
		return extId;
	}

	public void setExtId(String extId) {
		this.extId = extId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPec() {
		return pec;
	}

	public void setPec(String pec) {
		this.pec = pec;
	}

	public int getIdTipoAzienda() {
		return idTipoAzienda;
	}

	public void setIdTipoAzienda(int idTipoAzienda) {
		this.idTipoAzienda = idTipoAzienda;
	}

}
