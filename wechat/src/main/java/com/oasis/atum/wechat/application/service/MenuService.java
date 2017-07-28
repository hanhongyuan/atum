package com.oasis.atum.wechat.application.service;

import com.alibaba.fastjson.JSONObject;
import reactor.core.publisher.Mono;

/**
 * 菜单应用服务
 */
public interface MenuService
{
	/**
	 * 重置微信菜单
	 * @return
	 */
	Mono<JSONObject> reset();

	/**
	 * 创建个性化菜单
	 * @return
	 */
	Mono<String> createCustom(final Integer wxId);

//	Flux<MenuDTO> getMenusByParentId(String parentId, Pageable pageable);
//
//	Mono<MenuDTO> getMenuById(String id);
//
//	Mono<String> create(MenuDTO dto);
//
//	void update(MenuDTO dto);
//
//	void delete(String id);
}
