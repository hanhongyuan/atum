package com.oasis.atum.commons.application.service.impl;

import com.oasis.atum.commons.application.service.CallUpService;
import com.oasis.atum.commons.domain.cmd.CallUpRecordCmd;
import com.oasis.atum.commons.domain.entity.CallUpRecord;
import com.oasis.atum.commons.domain.enums.CallState;
import com.oasis.atum.commons.domain.enums.CallType;
import com.oasis.atum.commons.domain.request.CallUpRequest;
import com.oasis.atum.commons.infrastructure.repository.CallUpRecordRepository;
import com.oasis.atum.commons.infrastructure.service.MoorClient;
import com.oasis.atum.commons.interfaces.assembler.CallUpRecordAssembler;
import com.oasis.atum.commons.interfaces.dto.CallUpDTO;
import com.oasis.atum.commons.interfaces.request.CallUpCallBack;
import lombok.AllArgsConstructor;
import lombok.val;
import org.axonframework.commandhandling.callbacks.FutureCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.Date;

/**
 * 打电话应用服务实现类
 */
@Service
@Transactional
@AllArgsConstructor
public class CallUpServiceImpl implements CallUpService
{
	private final MoorClient             client;
	private final CallUpRecordRepository persistence;
	private final CommandGateway         commandGateway;

	@Override
	public Mono<String> callUp(final CallUpDTO data)
	{
		return Mono.justOrEmpty(data)
						 .map(d -> CallUpRecordCmd.Create.builder().callMobile(d.callMobile)
												 .callToMobile(d.callToMobile).maxCallTime(d.maxCallTime)
												 .noticeUri(d.noticeUri).createTime(new Date()).build())
						 .map(c ->
						 {
							 //异步处理结果
							 val future = new FutureCallback<CallUpRecordCmd.Create, CallUpRequest>();
							 //发送命令
							 commandGateway.send(c, future);
							 return future.toCompletableFuture();
						 })
						 .flatMap(Mono::fromFuture)
						 //容联七陌打电话
						 .flatMap(client::callUp)
						 .map(j -> j.getString("ActionID"));
	}

	@Override
	public Mono<Void> hangUp(final String callNo, final String calledNo, final CallType callType, final Date ring, final Date begin, final Date end,
													 final CallState callState, final String actionId,
													 final String recordFile, final String fileServer)
	{
		return Mono.just(actionId)
						 //更新命令
						 .map(id -> CallUpRecordCmd.Update.builder()
													.id(id).callType(callType)
													.ringTime(ring)
													.beginTime(begin)
													.endTime(end)
													.callState(callState)
													.recordFile(recordFile)
													.fileServer(fileServer)
													.build())
						 .map(c ->
						 {
							 //异步处理结果
							 val f = new FutureCallback<CallUpRecordCmd.Update, CallUpRecord>();
							 commandGateway.send(c, f);
							 return f.toCompletableFuture();
						 })
						 .flatMap(Mono::fromFuture)
						 //查询短信
						 .flatMap(d -> persistence.findById(actionId)
														 //回调地址
														 .map(CallUpRecord::getNoticeUri)
														 //回调通知
														 .flatMap(s -> WebClient.builder()
																						 .clientConnector(new ReactorClientHttpConnector())
																						 .build()
																						 .post()
																						 .uri(s)
																						 .contentType(MediaType.APPLICATION_JSON_UTF8)
																						 .accept(MediaType.APPLICATION_JSON_UTF8)
																						 .ifModifiedSince(ZonedDateTime.now())
																						 .ifNoneMatch("*")
																						 .body(BodyInserters.fromObject(CallUpRecordAssembler.toDTO(d)))
																						 .exchange()
														 ))
						 .then();
	}

	@Override
	public Mono<Void> updateCallUp(final CallUpCallBack data)
	{
		return Mono.justOrEmpty(data)
						 .map(d -> CallUpRecordCmd.Callback.builder().id(d.actionId).isSuccess(d.isSuccess)
												 .message(d.message).build())
						 .map(commandGateway::send)
						 .then();
	}
}
