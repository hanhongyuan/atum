package com.oasis.atum.commons.application.service.impl;

import com.oasis.atum.commons.application.service.CallUpService;
import com.oasis.atum.commons.domain.cmd.CallUpRecordCmd;
import com.oasis.atum.commons.domain.entity.CallUpRecord;
import com.oasis.atum.commons.domain.enums.CallState;
import com.oasis.atum.commons.domain.enums.CallType;
import com.oasis.atum.commons.domain.request.CallUpRequest;
import com.oasis.atum.commons.infrastructure.repository.CallUpRecordRepository;
import com.oasis.atum.commons.infrastructure.service.MoorClient;
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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
//		return Mono.justOrEmpty(actionId)
//			.map(id -> CallUpRecordCmd.Update.builder()
//																						.id(id)
//																						.callType(callType)
//																						.ringTime(ring)
//																						.beginTime(begin)
//																						.endTime(end)
//																						.callState(callState)
//																						.recordFile(recordFile)
//																						.fileServer(fileServer)
//																						.build())
//			.map(commandGateway::send)


		//更新命令
		val cmd = CallUpRecordCmd.Update.builder()
								.id(actionId).callType(callType)
								.ringTime(ring)
								.beginTime(begin)
								.endTime(end)
								.callState(callState)
								.recordFile(recordFile)
								.fileServer(fileServer)
								.build();
		//发送命令
		commandGateway.send(cmd);


		persistence.findById(actionId)
			.map(CallUpRecord::getNoticeUri)
			.map(s -> WebClient.builder()
			.clientConnector(new ReactorClientHttpConnector())
			.build()
			.post()
			.uri(s)
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.accept(MediaType.APPLICATION_JSON_UTF8)
			.body())；

//		return Mono.justOrEmpty(actionId)
//						 //如果ActionID存在则更新现在通话记录
//						 .map(id -> (CallUpRecordCmd) CallUpRecordCmd.Update.builder()
//																						.id(id)
//																						.callType(callType)
//																						.ringTime(ring)
//																						.beginTime(begin)
//																						.endTime(end)
//																						.callState(callState)
//																						.recordFile(recordFile)
//																						.fileServer(fileServer)
//																						.build())
//						 //不存在则新增一条通话记录
//						 .defaultIfEmpty(CallUpRecordCmd.Save.builder()
//															 .callMobile(callNo)
//															 .callToMobile(calledNo)
//															 .callType(callType)
//															 .ringTime(ring)
//															 .beginTime(begin)
//															 .endTime(end)
//															 .callState(callState)
//															 .recordFile(recordFile)
//															 .fileServer(fileServer)
//															 .createTime(new Date())
//															 .build())
//						 //发送命令
//						 .map(commandGateway::send)
//						 .then();

//		//如果ActionID存在则更新现在通话记录
//		Optional.ofNullable(actionId).map(id ->
//		{
//			//命令
//			val cmd = CallUpRecordCmd.Update.builder().id(actionId).callType(callType).ringTime(ring).beginTime(begin)
//									.endTime(end).callState(callState).recordFile(recordFile).fileServer(fileServer).build();
//			//发送命令
//			commandGateway.send(cmd);
//			return id;
//			//不存在则新增一条通话记录
//		}).orElseGet(() ->
//		{
//			//命令
//			val cmd = CallUpRecordCmd.Save.builder().callMobile(callNo).callToMobile(calledNo).callType(callType).ringTime(ring).beginTime(begin)
//									.endTime(end).callState(callState).recordFile(recordFile).fileServer(fileServer).createTime(new Date()).build();
//			//发送命令
//			commandGateway.send(cmd);
//			return null;
//		});
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
