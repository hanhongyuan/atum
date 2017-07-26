package com.oasis.atum.commons.application.service.impl;

import com.oasis.atum.base.infrastructure.util.DateUtil;
import com.oasis.atum.commons.application.service.SmsService;
import com.oasis.atum.commons.domain.cmd.SmsRecordCmd;
import com.oasis.atum.commons.infrastructure.repository.SmsRecordRepository;
import com.oasis.atum.commons.interfaces.dto.SmsDTO;
import com.oasis.atum.commons.interfaces.request.SmsCallBack;
import lombok.val;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;

/**
 * 短信应用服务实现类
 */
@Service
@Transactional
public class SmsServiceImpl implements SmsService
{
	private final SmsRecordRepository persistence;
	private final CommandGateway      commandGateway;

	public SmsServiceImpl(final SmsRecordRepository persistence, final CommandGateway commandGateway)
	{
		this.persistence = persistence;
		this.commandGateway = commandGateway;
	}

	@Async
	@Override
	public void sendCaptcha(final SmsDTO data)
	{
		//创建命令
		val cmd = SmsRecordCmd.Create.builder()
								.smsType(data.smsType)
								.mobiles(Arrays.asList(data.mobile))
								.isBusiness(false)
								.createTime(new Date())
								.build();
		//发送命令
		commandGateway.send(cmd);
	}

	@Async
	@Override
	public void success(final SmsCallBack data)
	{

		persistence.findSmsRecordByMessageId(data.messageId)
			.subscribe(d ->
			{
				//创建命令
				val cmd = SmsRecordCmd.Success.builder().id(d.getId())
										.isSuccess(true)
										.count(Integer.parseInt(data.smsCount))
										.receiveTime(DateUtil.toDate(data.receiveTime))
										.build();
				//发送命令
				commandGateway.send(cmd);
			});
	}

	@Async
	@Override
	public void fail(final SmsCallBack data)
	{
		persistence.findSmsRecordByMessageId(data.messageId)
			.subscribe(d ->
			{
				//创建命令
				val cmd = SmsRecordCmd.Fail.builder().id(d.getId())
										.isSuccess(false)
										.errCode(data.errCode)
										.build();
				//发送命令
				commandGateway.send(cmd);
			});
	}

	@Async
	@Override
	public void reply(final SmsCallBack data)
	{
		//创建命令
		val cmd = SmsRecordCmd.Reply.builder()
								.isSuccess(true)
								.build();
		//发送命令
		commandGateway.send(cmd);
	}
}
