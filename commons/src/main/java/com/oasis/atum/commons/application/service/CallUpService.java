package com.oasis.atum.commons.application.service;

import com.oasis.atum.commons.domain.enums.CallState;
import com.oasis.atum.commons.domain.enums.CallType;
import com.oasis.atum.commons.interfaces.dto.CallUpDTO;
import com.oasis.atum.commons.interfaces.request.CallUpCallBack;
import reactor.core.publisher.Mono;

import java.util.Date;

/**
 * 打电话应用服务
 */
public interface CallUpService
{
	/**
	 * 打电话
	 * @param data
	 */
	Mono<String> callUp(CallUpDTO data);

	/**
	 * 通话事件推送更新
	 * @param callNo
	 * @param calledNo
	 * @param callType
	 * @param ring
	 * @param begin
	 * @param end
	 * @param callState
	 * @param actionId
	 * @param recordFile
	 * @param fileServer
	 */
	Mono<Void> hangUp(String callNo, String calledNo, CallType callType, Date ring, Date begin, Date end, CallState callState, String actionId,
							String recordFile, String fileServer);

	/**
	 * 异步回调更新通话记录
	 * @param data
	 */
	Mono<Void> updateCallUp(CallUpCallBack data);
}
