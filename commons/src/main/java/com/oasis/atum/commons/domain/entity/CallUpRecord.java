package com.oasis.atum.commons.domain.entity;

import com.oasis.atum.base.infrastructure.constant.DateField;
import com.oasis.atum.base.infrastructure.util.DateUtil;
import com.oasis.atum.base.infrastructure.util.IdWorker;
import com.oasis.atum.base.infrastructure.util.Validator;
import com.oasis.atum.commons.domain.cmd.CallUpRecordCmd;
import com.oasis.atum.commons.domain.enums.CallEventState;
import com.oasis.atum.commons.domain.enums.CallState;
import com.oasis.atum.commons.domain.enums.CallType;
import com.oasis.atum.commons.domain.event.CallUpRecordEvent;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Optional;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

/**
 * 通话记录聚合根
 */
@Data
@Slf4j
@Aggregate
@NoArgsConstructor
@Document(collection = "call_up_record")
public class CallUpRecord
{
	@Id
	@AggregateIdentifier
	private String         id;
	//呼叫
	private String         callMobile;
	//被呼叫
	private String         callToMobile;
	//最大通话时长
	private Long           maxCallTime;
	//本次通话时长
	private Long           callTime;
	//通话类型
	private CallType       callType;
	//通话振铃时间
	private Date           ringTime;
	//接通时间
	private Date           beginTime;
	//挂断时间
	private Date           endTime;
	//接听状态
	private CallState      callState;
	//事件状态
	private CallEventState state;
	//通话录音文件名
	private String         recordFile;
	//录音文件存在服务器
	private String         fileServer;
	//接口调用是否成功
	private Boolean        isSuccess;
	//通知地址
	private String         noticeUri;
	//第三方唯一标识
	private String         thirdId;
	/**
	 * a)Message:4 被叫已接听
	 * b)Message:0 或 8 线路繁忙/异常(某些情况下，也可能为 被叫拒接/振铃未接/占线/关机/空号)
	 * c)Message:3 被叫拒接/振铃未接/占线/关机/空号 d)Message:5 一般情况下为余额不足，请先检查账户资费。
	 */
	private String         message;
	private String         comment;
	private Date           createTime;
	@LastModifiedDate
	private Date           updateTime;

//	@CommandHandler
	public CallUpRecord(final CallUpRecordCmd.Bind cmd)
	{
//		log.info("通话记录绑定命令处理");
		val id = IdWorker.getFlowIdWorkerInstance().nextSID();
		//发布通话记录绑定事件
		val event = CallUpRecordEvent.Bound.builder().id(id).cmd(cmd).build();
		apply(event);
	}

	public CallUpRecord update(final String id, final CallUpRecordCmd.Update cmd)
	{
		//发布通话记录修改事件
		val event = CallUpRecordEvent.Updated.builder().id(id).cmd(cmd).build();
		apply(event);
		return this;
	}

	@CommandHandler
	public void callback(final CallUpRecordCmd.Callback cmd)
	{
		log.info("通话记录回调命令处理");
		//发布通话记录回调事件
		val event = CallUpRecordEvent.Callbacked.builder().id(cmd.id).cmd(cmd).build();
		apply(event);
	}

	@EventSourcingHandler
	public void handle(final CallUpRecordEvent.Bound event)
	{
		id = event.id;
		callMobile = event.cmd.callMobile;
		callToMobile = event.cmd.callToMobile;
		maxCallTime = event.cmd.maxCallTime;
		noticeUri = event.cmd.noticeUri;
		thirdId = event.cmd.thirdId;
		createTime = event.cmd.createTime;
	}

	@EventSourcingHandler
	public void handle(final CallUpRecordEvent.Updated event)
	{
		log.info("通话记录修改事件 Inner");
		//通话时长
		callTime = Optional.ofNullable(event.cmd.beginTime)
								 .map(x -> DateUtil.compareTo(x, event.cmd.endTime, DateField.SECONDS))
								 .orElse(callTime);
		callType = Validator.either(event.cmd.callType, callType);
		ringTime = Validator.either(event.cmd.ringTime, ringTime);
		beginTime = Validator.either(event.cmd.beginTime, beginTime);
		endTime = Validator.either(event.cmd.endTime, endTime);
		callState = Validator.either(event.cmd.callState, callState);
		state = Validator.either(event.cmd.state, state);
		recordFile = Validator.either(event.cmd.recordFile, recordFile);
		fileServer = Validator.either(event.cmd.fileServer, fileServer);
	}

	@EventSourcingHandler
	public void handle(final CallUpRecordEvent.Callbacked event)
	{
		isSuccess = Validator.either(event.cmd.isSuccess, isSuccess);
		message = Validator.either(event.cmd.message, message);
	}

}
