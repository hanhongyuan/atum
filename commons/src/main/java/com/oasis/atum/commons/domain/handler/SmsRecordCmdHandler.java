package com.oasis.atum.commons.domain.handler;

import com.oasis.atum.base.infrastructure.service.RedisClient;
import com.oasis.atum.base.infrastructure.util.BaseUtil;
import com.oasis.atum.commons.domain.cmd.SmsRecordCmd;
import com.oasis.atum.commons.domain.entity.SmsRecord;
import com.oasis.atum.commons.infrastructure.service.SmsClient;
import io.vavr.collection.List;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Repository;
import org.springframework.stereotype.Component;

/**
 * 短信记录命令处理
 */
@Slf4j
@Component
@AllArgsConstructor
public class SmsRecordCmdHandler
{
	private final RedisClient           redis;
	private final SmsClient             client;
	private final Repository<SmsRecord> repository;

	@CommandHandler
	public void handle(final SmsRecordCmd.Create cmd)
	{
		log.info("短信记录创建命令处理");
		Try.of(() -> repository.newInstance(() -> new SmsRecord(cmd)))
			.onSuccess(d -> d.execute(data ->
			{
				//4位随机整数
				val code = BaseUtil.randomNum(4);
				//Redis记录用户与验证码关系 5分钟验证码过期
				redis.put(cmd.smsType + "=>" + cmd.mobile, code, 5L)
					.subscribe(b -> client.sendCaptcha(code, List.of(cmd.mobile))
														.subscribe(data::setMessageId));
			}));
	}
}
