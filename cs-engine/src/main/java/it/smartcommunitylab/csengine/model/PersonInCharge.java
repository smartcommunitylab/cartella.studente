package it.smartcommunitylab.csengine.model;

import java.util.ArrayList;
import java.util.List;

public class PersonInCharge extends BaseObject {
	private String name;
	private String surname;
	private String cf;
	private String address;
	private String phone;
	private String mobilePhone;
	private String email;
	private String type;
	private List<String> studentIds = new ArrayList<String>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
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
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<String> getStudentIds() {
		return studentIds;
	}
	public void setStudentIds(List<String> studentIds) {
		this.studentIds = studentIds;
	}

}
