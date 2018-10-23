package it.smartcommunitylab.csengine.extsource.infotn.parix;

import java.util.ArrayList;
import java.util.List;

public class DettaglioImpresa {
	private String denominazione;
	private String codiceFiscale;
	private String oggettoSociale;
	private String provincia;
	private String comune;
	private String toponimo;
	private String via;
	private String numeroCivico;
	private String cap;
	private String telefono;
	private String pec;
	private List<String> codiciAteco = new ArrayList<>();
	private List<String> descAteco = new ArrayList<>();
	
	public String getCodiceFiscale() {
		return codiceFiscale;
	}
	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}
	public String getOggettoSociale() {
		return oggettoSociale;
	}
	public void setOggettoSociale(String oggettoSociale) {
		this.oggettoSociale = oggettoSociale;
	}
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	public String getComune() {
		return comune;
	}
	public void setComune(String comune) {
		this.comune = comune;
	}
	public String getVia() {
		return via;
	}
	public void setVia(String via) {
		this.via = via;
	}
	public String getNumeroCivico() {
		return numeroCivico;
	}
	public void setNumeroCivico(String numeroCivico) {
		this.numeroCivico = numeroCivico;
	}
	public String getCap() {
		return cap;
	}
	public void setCap(String cap) {
		this.cap = cap;
	}
	public String getPec() {
		return pec;
	}
	public void setPec(String pec) {
		this.pec = pec;
	}
	public List<String> getCodiciAteco() {
		return codiciAteco;
	}
	public void setCodiciAteco(List<String> codiciAteco) {
		this.codiciAteco = codiciAteco;
	}
	public List<String> getDescAteco() {
		return descAteco;
	}
	public void setDescAteco(List<String> descAteco) {
		this.descAteco = descAteco;
	}
	public String getDenominazione() {
		return denominazione;
	}
	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getToponimo() {
		return toponimo;
	}
	public void setToponimo(String toponimo) {
		this.toponimo = toponimo;
	}

}
