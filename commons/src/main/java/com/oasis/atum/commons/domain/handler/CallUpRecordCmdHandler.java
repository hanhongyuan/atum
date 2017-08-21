package com.oasis.atum.commons.domain.handler;

import com.alibaba.fastjson.JSONObject;
import com.oasis.atum.base.infrastructure.service.RedisClient;
import com.oasis.atum.commons.domain.cmd.CallUpRecordCmd;
import com.oasis.atum.commons.domain.entity.CallUpRecord;
import com.oasis.atum.commons.domain.event.CallUpRecordEvent;
import com.oasis.atum.commons.domain.request.MoorRequest;
import com.oasis.atum.commons.infrastructure.repository.CallUpRecordRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static org.axonframework.eventhandling.GenericEventMessage.asEventMessage;

/**
 * 通话记录命令处理
 */
@Slf4j
@Component
@AllArgsConstructor
public class CallUpRecordCmdHandler
{
	private final RedisClient              redis;
	private final EventStore               eventStore;
	private final Repository<CallUpRecord> repository;
	private final CallUpRecordRepository   persistence;
	/**
	 * Redis_key 手机绑定关系
	 */
	private static final String REDIS_KEY_BINDING = "binding=>";

	@CommandHandler
	@SneakyThrows(Exception.class)
	public CallUpRecord handle(final CallUpRecordCmd.Bind cmd)
	{
		log.info("通话记录绑定命令处理");

		val value = new JSONObject();
		value.put("thirdId", cmd.thirdId);
		value.put("to", cmd.callToMobile);
		value.put("maxCallTime", cmd.maxCallTime);
		/**
		 * 暂存Redis 30分钟
		 * key binding=>call
		 * value {"to":to,"maxCallTime":maxCallTime......}
		 */
		redis.put(REDIS_KEY_BINDING + cmd.callMobile, value, 30L).subscribe();

		return repository.newInstance(() -> new CallUpRecord(cmd))
						 .invoke(d -> d);
	}

	@CommandHandler
	public Mono<String> handle(final CallUpRecordCmd.Update cmd)
	{
		log.info("通话记录修改命令处理");
		//查询手机号绑定关系
		return redis.getJSONObject(REDIS_KEY_BINDING + cmd.callMobile)
						 //查询库中数据
						 .flatMap(j -> persistence.findByThirdId(j.getString("thirdId")))
						 //修改数据
						 .map(d ->
						 {
							 //发布通话记录修改事件
							 val e = CallUpRecordEvent.Updated.builder().id(d.getId()).cmd(cmd).build();
							 eventStore.publish(asEventMessage(e));

							 return d.getId();
						 });
	}
}
