package it.smartcommunitylab.csengine.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleUpdate extends BaseObject {

	public Map<String, List<MetaInfo>> getUpdateMap() {
		return updateMap;
	}

	public void setUpdateMap(Map<String, List<MetaInfo>> updateMap) {
		this.updateMap = updateMap;
	}

	Map<String, List<MetaInfo>> updateMap = new HashMap<String, List<MetaInfo>>();

}
