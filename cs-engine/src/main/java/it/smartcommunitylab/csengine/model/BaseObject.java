package it.smartcommunitylab.csengine.model;

import it.smartcommunitylab.csengine.common.Utils;

import java.util.Date;
import java.util.Objects;

import org.springframework.data.annotation.Id;

public class BaseObject {
	@Id
	private String id;
	private String origin;
	private String extId;
	private Date creationDate;
	private Date lastUpdate;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	@Override
	public boolean equals(Object o) {
		boolean result = false; 
		if(o instanceof BaseObject) {
			BaseObject object = (BaseObject) o;
			if(Utils.isNotEmpty(object.getId())) {
				if(object.getId().equals(id)) {
					result = true;
				}
			}
		}
		return result;
	}
	
	@Override
  public int hashCode() {
      return Objects.hash(id);
  }


}
