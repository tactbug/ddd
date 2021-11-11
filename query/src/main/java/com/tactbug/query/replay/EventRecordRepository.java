package com.tactbug.query.replay;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRecordRepository extends MongoRepository<EventRecord, Long> {

}