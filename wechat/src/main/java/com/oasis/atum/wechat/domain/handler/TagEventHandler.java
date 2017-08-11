package com.oasis.atum.wechat.domain.handler;

import com.oasis.atum.wechat.domain.entity.Tag;
import com.oasis.atum.wechat.domain.event.TagEvent;
import com.oasis.atum.wechat.infrastructure.repository.TagRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

/**
 * 标签事件处理
 */
@Slf4j
@Component
@AllArgsConstructor
public class TagEventHandler
{
	private final Repository<Tag> repository;
	private final TagRepository   persistence;

	@EventHandler
	public void handle(final TagEvent.Created event)
	{
		log.info("标签创建事件处理");

		repository.load(event.id).execute(d -> persistence.insert(d).subscribe());
	}

	@EventHandler
	public void handle(final TagEvent.AddedFans event)
	{
		log.info("标签添加粉丝事件处理");

		update(event.id);
	}

	@EventHandler
	public void handle(final TagEvent.Updated event)
	{
		log.info("标签修改事件处理");

		update(event.id);
	}

	private void update(String id)
	{
		repository.load(id).execute(d -> persistence.save(d).subscribe());
	}
}
