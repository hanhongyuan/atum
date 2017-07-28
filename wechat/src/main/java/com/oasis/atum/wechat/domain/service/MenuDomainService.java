package com.oasis.atum.wechat.domain.service;

import com.oasis.atum.wechat.domain.request.MenuRequest;
import reactor.core.publisher.Mono;

/**
 * 菜单领域服务
 */
public interface MenuDomainService
{
	/**
	 * 重置微信菜单
	 * @return
	 */
	Mono<MenuRequest> reset();
}
