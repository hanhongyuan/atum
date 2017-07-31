package com.oasis.atum.commons.infrastructure.repository;


import com.oasis.atum.commons.domain.entity.SmsRecord;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * 短信记录持久化
 */
public interface SmsRecordRepository extends ReactiveMongoRepository<SmsRecord,String>
{
	/**
	 * 通过阿里唯一标识找到短信
	 * @param messageId
	 * @return
	 */
	Mono<SmsRecord> findByMessageId(String messageId);
}
