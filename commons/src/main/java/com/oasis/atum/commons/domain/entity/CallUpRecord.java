package com.oasis.atum.commons.domain.entity;

import com.oasis.atum.base.infrastructure.constant.DateField;
import com.oasis.atum.base.infrastructure.util.DateUtil;
import com.oasis.atum.base.infrastructure.util.IdWorker;
import com.oasis.atum.commons.domain.cmd.CallUpRecordCmd;
import com.oasis.atum.commons.domain.event.CallUpRecordEvent;
import com.oasis.atum.commons.infrastructure.enums.CallEventState;
import com.oasis.atum.commons.infrastructure.enums.CallState;
import com.oasis.atum.commons.infrastructure.enums.CallType;
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

import static io.vavr.API.*;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

/**
 * 通话记录聚合根
 */
@Data
@Slf4j
@NoArgsConstructor
@Aggregate
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
	//容联七陌唯一标识
	private String         callId;
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

	public CallUpRecord(final CallUpRecordCmd.Bind cmd)
	{
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
		//通话时长
		callTime = Stream(event.cmd.beginTime).zipWith(Stream(event.cmd.endTime), (x, y) -> DateUtil.compareTo(x, y, DateField.SECONDS))
								 .getOrElse(0L);
		callType = Option(event.cmd.callType).getOrElse(callType);
		ringTime = Option(event.cmd.ringTime).getOrElse(ringTime);
		beginTime = Option(event.cmd.beginTime).getOrElse(beginTime);
		endTime = Option(event.cmd.endTime).getOrElse(endTime);
		callState = Option(event.cmd.callState).getOrElse(callState);
		state = Option(event.cmd.state).getOrElse(state);
		recordFile = Option(event.cmd.recordFile).getOrElse(recordFile);
		fileServer = Option(event.cmd.fileServer).getOrElse(fileServer);
		callId = Option(event.cmd.callId).getOrElse(callId);

	}

	@EventSourcingHandler
	public void handle(final CallUpRecordEvent.Callbacked event)
	{
		isSuccess = Option(event.cmd.isSuccess).getOrElse(isSuccess);
		message = Option(event.cmd.message).getOrElse(message);
	}

}
