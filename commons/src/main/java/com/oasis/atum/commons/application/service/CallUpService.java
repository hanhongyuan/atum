package com.oasis.atum.commons.application.service;

import com.oasis.atum.commons.domain.enums.CallEventState;
import com.oasis.atum.commons.domain.enums.CallState;
import com.oasis.atum.commons.domain.enums.CallType;
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
	 * 挂断
	 * @param data
	 * @return
	 */
	Mono<String> hangUp(MoorDTO.HangUp data);

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
	 */
	Mono<Void> hangUp(String callNo, String calledNo, CallType callType, Date ring, Date begin, Date end, CallState callState, CallEventState state, String actionId,
										String recordFile, String fileServer);

	/**
	 * 异步回调更新通话记录
	 * @param data
	 */
	Mono<Void> updateCallUp(MoorDTO.CallUpCallBack data);
}
