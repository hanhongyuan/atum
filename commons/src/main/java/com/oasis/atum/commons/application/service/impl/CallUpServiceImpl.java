package com.oasis.atum.commons.application.service.impl;

import com.oasis.atum.base.infrastructure.service.HttpClient;
import com.oasis.atum.commons.application.service.CallUpService;
import com.oasis.atum.commons.domain.cmd.CallUpRecordCmd;
import com.oasis.atum.commons.domain.entity.CallUpRecord;
import com.oasis.atum.commons.infrastructure.enums.CallEventState;
import com.oasis.atum.commons.infrastructure.enums.CallState;
import com.oasis.atum.commons.infrastructure.enums.CallType;
import com.oasis.atum.commons.domain.request.MoorRequest;
import com.oasis.atum.commons.infrastructure.service.MoorClient;
import com.oasis.atum.commons.interfaces.assembler.CallUpRecordAssembler;
import com.oasis.atum.commons.interfaces.dto.CallUpRecordDTO;
import com.oasis.atum.commons.interfaces.dto.MoorDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.axonframework.commandhandling.callbacks.FutureCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Date;

/**
 * 打电话应用服务实现类
 */
@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class CallUpServiceImpl implements CallUpService
{
	private final HttpClient     http;
	private final MoorClient     client;
	private final CommandGateway commandGateway;

	@Override
	public Mono<CallUpRecordDTO> binding(final MoorDTO.Binding data)
	{
		return Mono.justOrEmpty(data)
						 //创建绑定命令
						 .map(d -> CallUpRecordCmd.Bind.builder().thirdId(d.thirdId).callMobile(d.call)
												 .callToMobile(d.to).maxCallTime(d.maxCallTime)
												 .noticeUri(d.noticeUri).createTime(new Date()).build())
						 //发送命令
						 .map(c ->
						 {
							 //异步处理结果
							 val f = new FutureCallback<CallUpRecordCmd.Bind, CallUpRecord>();
							 //发送命令
							 commandGateway.send(c, f);
							 return f.toCompletableFuture();
						 })
						 .flatMap(Mono::fromFuture)
						 .map(CallUpRecordAssembler::toDTO);
	}

	@Override
	public Mono<Void> hangUp(final String id)
	{
		return Mono.just(id)
						 //挂断命令
						 .map(pk -> CallUpRecordCmd.HangUp.builder().id(id).build())
						 .map(c ->
						 {
							 //异步处理结果
							 val f = new FutureCallback<CallUpRecordCmd.HangUp, MoorRequest.HangUp>();
							 //发送命令
							 commandGateway.send(c, f);
							 return f.toCompletableFuture();
						 })
						 .flatMap(Mono::fromFuture)
						 //容联七陌挂断
						 .flatMap(client::hangUp)
						 .then();
	}

	@Override
	public Mono<Void> hangUp(final String callNo, final String calledNo, final CallType callType, final Date ring, final Date begin, final Date end,
													 final CallState callState, final CallEventState state, final String actionId,
													 final String recordFile, final String fileServer, final String callId)
	{
		//修改命令
		return Mono.just(CallUpRecordCmd.Update.builder()
											 .callMobile(callNo).callToMobile(calledNo).callType(callType)
											 .ringTime(ring).beginTime(begin).endTime(end).callState(callState)
											 .state(state).recordFile(recordFile).fileServer(fileServer).callId(callId)
											 .build())
						 .map(c ->
						 {
							 //异步处理结果
							 val f = new FutureCallback<CallUpRecordCmd.Update, CallUpRecord>();
							 commandGateway.send(c, f);
							 return f.toCompletableFuture();
						 })
						 .flatMap(Mono::fromFuture)
						 //发送数据到回调地址
						 .flatMap(d -> http.post(d.getNoticeUri(), CallUpRecordAssembler.toDTO(d)))
						 .then();
	}

	@Override
	public Mono<Void> updateCallUp(final MoorDTO.CallUpCallBack data)
	{
		return Mono.justOrEmpty(data)
						 .map(d -> CallUpRecordCmd.Callback.builder()
												 .id(d.actionId)
												 .isSuccess(d.isSuccess)
												 .message(d.message)
												 .build())
						 .map(commandGateway::send)
						 .then();
	}
}
