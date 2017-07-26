package com.oasis.atum.commons.domain.handler;

import com.oasis.atum.commons.domain.entity.CallUpRecord;
import com.oasis.atum.commons.domain.event.CallUpRecordEvent;
import com.oasis.atum.commons.infrastructure.repository.CallUpRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

/**
 * 通话记录事件处理
 */
@Slf4j
@Component
public class CallUpRecordEventHandler
{
	private final Repository<CallUpRecord> repository;
	private final CallUpRecordRepository   persistence;

	public CallUpRecordEventHandler(final Repository<CallUpRecord> repository,
																	final CallUpRecordRepository persistence)
	{
		this.repository = repository;
		this.persistence = persistence;
	}

	@EventHandler
	public void handle(final CallUpRecordEvent.Created event)
	{
		log.info("通话记录创建事件处理");

		repository.load(event.id).execute(d -> persistence.insert(d).subscribe());
	}

	@EventHandler
	public void handle(final CallUpRecordEvent.Updated event)
	{
		log.info("通话记录修改事件处理");

		update(event.id);
	}

	@EventHandler
	public void handle(final CallUpRecordEvent.Callbacked event)
	{
		log.info("通话记录回调事件处理");

		update(event.id);
	}

	@EventHandler
	public void handle(final CallUpRecordEvent.Saved event)
	{
		log.info("通话记录保存事件处理");

		repository.load(event.id).execute(persistence::insert);
	}

	private void update(String id)
	{
		repository.load(id).execute(d -> persistence.save(d).subscribe());
	}
}
