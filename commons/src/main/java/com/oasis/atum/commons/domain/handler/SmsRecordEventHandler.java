package com.oasis.atum.commons.domain.handler;

import com.oasis.atum.commons.domain.entity.SmsRecord;
import com.oasis.atum.commons.domain.event.SmsRecordEvent;
import com.oasis.atum.commons.infrastructure.repository.SmsRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

/**
 * 短信记录事件处理
 */
@Slf4j
@Component
@AllArgsConstructor
public class SmsRecordEventHandler
{
	private final Repository<SmsRecord> repository;
	private final SmsRecordRepository   persistence;

	@EventHandler
	public void handle(final SmsRecordEvent.Created event)
	{
		log.info("短信记录创建事件处理");
		repository.load(event.id).execute(data -> persistence.insert(data).subscribe());
	}

	@EventHandler
	public void handle(final SmsRecordEvent.Succeed event)
	{
		log.info("短信记录成功回调事件处理");
		update(event.id);
	}

	@EventHandler
	public void handle(final SmsRecordEvent.Failed event)
	{
		log.info("短信记录失败回调事件处理");
		update(event.id);
	}

	@EventHandler
	public void handle(final SmsRecordEvent.Replied event)
	{
		log.info("短信记录回复回调事件处理");
		update(event.id);
	}

	private void update(String id)
	{
		repository.load(id).execute(data -> persistence.save(data).subscribe());
	}
}
