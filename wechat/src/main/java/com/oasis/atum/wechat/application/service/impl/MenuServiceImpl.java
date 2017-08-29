package com.oasis.atum.wechat.application.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.oasis.atum.wechat.application.service.MenuService;
import com.oasis.atum.wechat.domain.request.MenuRequest;
import com.oasis.atum.wechat.domain.service.MenuDomainService;
import com.oasis.atum.wechat.infrastructure.repository.MenuRepository;
import com.oasis.atum.wechat.infrastructure.service.WechatClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * 菜单应用服务实现类
 */
@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class MenuServiceImpl implements MenuService
{
	private final WechatClient      client;
	private final MenuDomainService domain;
	private final MenuRepository    persistence;
	private final CommandGateway    commandGateway;

	@Override
	@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
	public Mono<JSONObject> reset()
	{
		//菜单领域服务获取数据
		return domain.reset()
							 //请求微信重置微信菜单
							 .flatMap(client::resetMenu);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
	public Mono<String> createCustom(final Integer wxId)
	{
		//菜单领域服务获取数据
		return domain.reset()
							 //按标签匹配
							 .map(d -> MenuRequest.Create.builder().button(d.button).matchrule(MenuRequest.Matchrule.builder().tagId(wxId).build()).build())
							 //请求微信重置微信菜单
							 .flatMap(client::customMenu)
							 .map(s -> s.getString("menuid"));
	}
}
