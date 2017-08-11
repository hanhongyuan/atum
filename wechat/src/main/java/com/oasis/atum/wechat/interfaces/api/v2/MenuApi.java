package com.oasis.atum.wechat.interfaces.api.v2;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.oasis.atum.base.infrastructure.util.Restful;
import com.oasis.atum.wechat.application.service.MenuService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 微信菜单接口
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("v2/menus")
public class MenuApi
{
	private final MenuService service;

	//子菜单资源集合
	private static final String CHILDREN_RESOURCES = "{parentId}/childrens";
	//子菜单资源
	private static final String CHILDREN_RESOURCE  = "{parentId}/childrens/{id}";

	@PostMapping("reset")
	public Mono<ResponseEntity> reset()
	{
		log.info("重置微信菜单");
		return service.reset().map(Restful::ok);
	}

	@PostMapping("custom")
	public Mono<ResponseEntity> custom(@RequestBody Mono<String> data)
	{
		log.info("创建自定义菜单");
		return data.map(Integer::parseInt)
						 .flatMap(service::createCustom)
						 .map(Restful::ok);
	}
}
