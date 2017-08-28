package com.oasis.atum.commons.infrastructure.repository;

import com.oasis.atum.commons.domain.entity.CallUpRecord;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * 通话记录持久化
 */
public interface CallUpRecordRepository extends ReactiveMongoRepository<CallUpRecord, String>
{
}
