package com.oasis.atum.wechat.infrastructure.repository;

import com.oasis.atum.wechat.domain.entity.Menu;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/**
 * 菜单持久化
 */
public interface MenuRepository extends ReactiveMongoRepository<Menu,String>
{
	/**
	 * 子菜单列表
	 * @param parentId
	 * @return
	 */
	Flux<Menu> findMenusByParentIdAndIsShowOrderBySortAsc(String parentId, boolean isShow);
}
