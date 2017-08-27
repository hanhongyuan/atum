package com.oasis.atum.commons.application.service.impl;

import com.oasis.atum.base.infrastructure.util.DateUtil;
import com.oasis.atum.commons.application.service.SmsService;
import com.oasis.atum.commons.domain.cmd.SmsRecordCmd;
import com.oasis.atum.commons.infrastructure.repository.SmsRecordRepository;
import com.oasis.atum.commons.interfaces.dto.SmsDTO;
import com.oasis.atum.commons.interfaces.request.SmsCallBack;
import lombok.AllArgsConstructor;
import lombok.val;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Date;

/**
 * 短信应用服务实现类
 */
@Service
@Transactional
@AllArgsConstructor
public class SmsServiceImpl implements SmsService
{
	private final SmsRecordRepository persistence;
	private final CommandGateway      commandGateway;

	@Override
	public Mono<Void> sendCaptcha(final SmsDTO data)
	{
		return Mono.justOrEmpty(data)
						 .map(d -> SmsRecordCmd.Create.builder()
												 .smsType(d.smsType)
												 .mobile(d.mobile)
												 .isBusiness(false)
												 .createTime(new Date())
												 .build())
						 .map(commandGateway::send)
						 .then();
	}

	@Override
	public Mono<Void> success(final SmsCallBack data)
	{
		return persistence.findByMessageId(data.messageId)
						 //创建命令
						 .map(d -> SmsRecordCmd.Success.builder().id(d.getId())
												 .isSuccess(true)
												 .count(Integer.parseInt(data.smsCount))
												 .receiveTime(DateUtil.toDate(data.receiveTime))
												 .build())
						 //发送命令
						 .map(commandGateway::send)
						 .then();
	}

	@Override
	public Mono<Void> fail(final SmsCallBack data)
	{
		return persistence.findByMessageId(data.messageId)
						 .map(d -> SmsRecordCmd.Fail.builder().id(d.getId())
												 .isSuccess(false)
												 .errCode(data.errCode)
												 .build())
						 .map(commandGateway::send)
						 .then();
	}

	@Override
	public Mono<Void> reply(final SmsCallBack data)
	{
		//创建命令
		val cmd = SmsRecordCmd.Reply.builder()
								.isSuccess(true)
								.build();
		//发送命令
		commandGateway.send(cmd);

		return Mono.empty();
	}
}
