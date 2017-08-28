package com.oasis.atum.commons.application.service;

import com.oasis.atum.commons.infrastructure.enums.CallEventState;
import com.oasis.atum.commons.infrastructure.enums.CallState;
import com.oasis.atum.commons.infrastructure.enums.CallType;
import com.oasis.atum.commons.interfaces.dto.CallUpRecordDTO;
import com.oasis.atum.commons.interfaces.dto.MoorDTO;
import reactor.core.publisher.Mono;

import java.util.Date;

/**
 * 打电话应用服务
 */
public interface CallUpService
{
	/**
	 * 绑定手机关系
	 * A=>坐席=>B 拨打方式
	 * @param data
	 * @return
	 */
	Mono<CallUpRecordDTO> binding(MoorDTO.Binding data);

	/**
	 * 解绑手机关系
	 * @param thirdId
	 * @return
	 */
	Mono<Void> unbinding(String thirdId);

	/**
	 * 电话挂断
	 * @param id
	 * @return
	 */
	Mono<Void> hangUp(String id);

	/**
	 * 通话事件推送更新
	 * @param callNo
	 * @param calledNo
	 * @param callType
	 * @param ring
	 * @param begin
	 * @param end
	 * @param callState
	 * @param state
	 * @param actionId
	 * @param recordFile
	 * @param fileServer
	 * @param callId
	 */
	Mono<Void> hangUp(String callNo, String calledNo, CallType callType, Date ring, Date begin, Date end, CallState callState, CallEventState state,
										String actionId,
										String recordFile, String fileServer, String callId);

	/**
	 * 异步回调更新通话记录
	 * @param data
	 */
	Mono<Void> updateCallUp(MoorDTO.CallUpCallBack data);
}
