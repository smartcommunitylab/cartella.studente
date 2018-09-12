package it.smartcommunitylab.csengine.storage;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.smartcommunitylab.csengine.model.ScheduleUpdate;

public interface ScheduleUpdateRepository extends MongoRepository<ScheduleUpdate, String> {

}
