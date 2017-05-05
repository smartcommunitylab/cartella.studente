package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.model.Registration;

import java.util.List;

import org.springframework.data.domain.Pageable;

public interface RegistrationRepositoryCustom {

	public List<Registration> searchRegistration(String studentId, String teachingUnitId,
			String schoolYear, Long dateFrom, Long dateTo, Pageable pageable);
			
}
