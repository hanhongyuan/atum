package com.oasis.atum.wechat.domain.handler;

import com.oasis.atum.wechat.domain.entity.Menu;
import com.oasis.atum.wechat.domain.event.MenuEvent;
import com.oasis.atum.wechat.infrastructure.repository.MenuRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import static io.vavr.API.Option;

/**
 * 菜单事件处理
 */
@Slf4j
@Component
@AllArgsConstructor
public class MenuEventHandler
{
	private final Repository<Menu> repository;
	private final MenuRepository   persistence;

	@EventHandler
	public void handle(final MenuEvent.Created event)
	{
		log.info("菜单创建事件处理");

		repository.load(event.id).invoke(d -> persistence.insert(d).subscribe());
	}

	@EventHandler
	public void handle(final MenuEvent.Updated event)
	{
		log.info("菜单修改事件处理");

		repository.load(event.id).invoke(d -> persistence.save(d).subscribe());
	}

	@EventHandler
	public void handle(final MenuEvent.Deleted event)
	{
		log.info("菜单删除事件处理");

		repository.load(event.id)
				.execute(d -> Option(d.getParentId())
													//有父类Id说明是子类菜单，只需要删除自身即可.
													.map(persistence::deleteById)
													//没有说明是顶级菜单,需要删除自身和子类菜单.
													.getOrElse(() -> persistence.findByParentIdAndIsShowOrderBySortAsc(event.id, true)
																							 .collectList()
																							 //批量删除所有子类
																							 .flatMap(persistence::deleteAll)
																							 //删除父类自身
																							 .flatMap(v -> persistence.deleteById(event.id)))
				);
	}
}
