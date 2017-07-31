package com.oasis.atum.wechat.infrastructure.repository;

import com.oasis.atum.wechat.domain.entity.Tag;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * 用户标签持久化
 */
public interface TagRepository extends ReactiveMongoRepository<Tag, String>
{
	/**
	 * 微信唯一标识查找
	 * @param wxId
	 * @return
	 */
	Mono<Tag> findByWxId(Integer wxId);
}
