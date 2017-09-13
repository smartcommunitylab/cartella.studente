package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.TeachingUnit;
import it.smartcommunitylab.csengine.model.Typology;

import java.util.List;

public interface TeachingUnitRepositoryCustom {
	public List<TeachingUnit> findByClassification(List<Typology> classification);
}
