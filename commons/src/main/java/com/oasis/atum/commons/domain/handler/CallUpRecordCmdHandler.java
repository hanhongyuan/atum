package com.oasis.atum.commons.domain.handler;

import com.oasis.atum.base.infrastructure.service.RedisClient;
import com.oasis.atum.commons.domain.cmd.CallUpRecordCmd;
import com.oasis.atum.commons.domain.entity.CallUpRecord;
import com.oasis.atum.commons.domain.request.MoorRequest;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Repository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 通话记录命令处理
 */
@Slf4j
@Component
@AllArgsConstructor
public class CallUpRecordCmdHandler
{
	private final RedisClient              redis;
	private final Repository<CallUpRecord> repository;
	/**
	 * Redis_key 手机绑定关系
	 */
	private static final String REDIS_KEY_BINDING = "binding=>";

	@CommandHandler
	@SneakyThrows(Exception.class)
	public CallUpRecord handle(final CallUpRecordCmd.Bind cmd)
	{
		log.info("通话记录绑定命令处理");

		return repository.newInstance(() -> new CallUpRecord(cmd))
						 .invoke(d -> d);
	}

	@CommandHandler
	public Mono<Long> handle(final CallUpRecordCmd.UnBind cmd)
	{
		log.info("通话记录解绑命令处理");

		return redis.delete(REDIS_KEY_BINDING + cmd.call);
	}

	@CommandHandler
	public CallUpRecord handle(final CallUpRecordCmd.Update cmd)
	{
		log.info("通话记录修改命令处理");
		//同步阻塞等待返回
		val id = redis.getJSONObject(REDIS_KEY_BINDING + cmd.callMobile).block().getString("id");
		//查询手机号绑定关系
		return repository.load(id).invoke(d -> d.update(id, cmd));
	}

	@CommandHandler
	public MoorRequest.HangUp handle(final CallUpRecordCmd.HangUp cmd)
	{
		log.info("通话记录挂断命令处理");

		return repository.load(cmd.id)
						 .invoke(d ->
						 {
							 //挂断之后解除绑定
							 redis.delete(d.getCallMobile()).subscribe();

							 return MoorRequest.HangUp.builder()
												.callId(d.getCallId())
												.actionId(d.getId())
												.build();
						 });
	}
}
