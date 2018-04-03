package it.smartcommunitylab.csengine.storage;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.smartcommunitylab.csengine.model.MetaInfo;

public interface MetaInfoRepository extends MongoRepository<MetaInfo, String> {

}
