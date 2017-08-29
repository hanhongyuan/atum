package com.oasis.atum.wechat.domain.service.impl;

import com.oasis.atum.wechat.domain.entity.Menu;
import com.oasis.atum.wechat.domain.request.MenuRequest;
import com.oasis.atum.wechat.domain.service.MenuDomainService;
import com.oasis.atum.wechat.infrastructure.repository.MenuRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.Objects;


/**
 * 菜单领域服务实现类
 */
@Component
public class MenuDomainServiceImpl implements MenuDomainService
{
	private final MenuRepository persistence;

	public MenuDomainServiceImpl(final MenuRepository persistence)
	{
		this.persistence = persistence;
	}

	@Override
	public Mono<MenuRequest.Create> reset()
	{
		return persistence.findAll()
							 //顶级菜单
							 .filter(d -> Objects.isNull(d.getParentId()))
							 //sort越小越前面
							 .sort(Comparator.comparing(Menu::getSort))
							 //对应子菜单
							 .flatMap(d -> persistence.findByParentIdAndIsShowOrderBySortAsc(d.getId(), true)
																 //转成微信需要请求格式数据
																 .map(MenuRequest.Button::new)
																 .collectList()
																 .map(l -> new MenuRequest.Button(d, l)))
							 .collectList()
							 .map(l -> MenuRequest.Create.builder().button(l).build());
	}
}
