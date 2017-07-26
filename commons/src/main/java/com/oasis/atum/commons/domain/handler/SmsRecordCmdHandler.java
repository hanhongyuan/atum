package com.oasis.atum.commons.domain.handler;

import com.oasis.atum.base.infrastructure.service.RedisClient;
import com.oasis.atum.base.infrastructure.util.CommonUtil;
import com.oasis.atum.commons.domain.cmd.SmsRecordCmd;
import com.oasis.atum.commons.domain.entity.SmsRecord;
import com.oasis.atum.commons.infrastructure.service.SmsClient;
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
public class SmsRecordCmdHandler
{
	private final RedisClient           redis;
	private final SmsClient             client;
	private final Repository<SmsRecord> repository;

	public SmsRecordCmdHandler(final RedisClient redis, final SmsClient client,
														 final Repository<SmsRecord> repository)
	{
		this.redis = redis;
		this.client = client;
		this.repository = repository;
	}

	@CommandHandler
	public void handle(final SmsRecordCmd.Create cmd)
	{
		log.info("短信记录创建命令处理");
		try
		{
			repository.newInstance(() -> new SmsRecord(cmd)).execute(data ->
			{
				//短信类型
				switch (cmd.smsType)
				{
					//注册
					case register:
						//登录
					case login:
						//邀请
					case invitation:
						//4位随机整数
						val code = CommonUtil.randomNum(4);
						//Redis记录用户与验证码关系 5分钟验证码过期
						cmd.mobiles.forEach(s -> redis.put(cmd.smsType + "=>" + s, code, 5L).subscribe());
						//阿里唯一标识
						client.sendCaptcha(code, cmd.mobiles).subscribe(id ->
						{
							log.info(id);
							data.setMessageId(id);
						});
						break;
				}
			});
		}
		catch (Exception e)
		{
			log.error("短信记录创建出错", e);
		}
	}
}
