package com.oasis.atum.commons.infrastructure.repository;

import com.oasis.atum.commons.domain.entity.CallUpRecord;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * 通话记录持久化
 */
public interface CallUpRecordRepository extends ReactiveMongoRepository<CallUpRecord,String>
{
	/**
	 * 通过第三方唯一标识查找
	 * @param thirdId
	 * @return
	 */
	Mono<CallUpRecord> findByThirdId(String thirdId);
}
