package com.oasis.atum.commons.domain.handler;

import com.alibaba.fastjson.JSONObject;
import com.oasis.atum.base.infrastructure.service.RedisClient;
import com.oasis.atum.commons.domain.entity.CallUpRecord;
import com.oasis.atum.commons.domain.event.CallUpRecordEvent;
import com.oasis.atum.commons.infrastructure.repository.CallUpRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

/**
 * 通话记录事件处理
 */
@Slf4j
@Component
@AllArgsConstructor
public class CallUpRecordEventHandler
{
	private final RedisClient              redis;
	private final Repository<CallUpRecord> repository;
	private final CallUpRecordRepository   persistence;
	/**
	 * Redis_key 手机绑定关系
	 */
	private static final String REDIS_KEY_BINDING = "binding=>";

	@EventHandler
	public void handle(final CallUpRecordEvent.Bound event)
	{
		log.info("通话记录绑定事件处理");

		repository.load(event.id)
			.execute(data -> persistence.insert(data)
												 .subscribe(d ->
												 {
													 log.info("创建成功后存入Redis");

													 val value = new JSONObject();
													 value.put("id", d.getId());
													 value.put("thirdId", d.getThirdId());
													 value.put("to", d.getCallToMobile());
													 value.put("call", d.getCallMobile());
													 value.put("maxCallTime", d.getMaxCallTime());
													 /**
														* 30分钟
														* key binding=>call
														* value {id:"1"...}
														*/
													 redis.put(REDIS_KEY_BINDING + d.getCallMobile(), value, 30L).subscribe();
												 }));
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

	private void update(String id)
	{
		repository.load(id).execute(d -> persistence.save(d).subscribe());
	}
}
