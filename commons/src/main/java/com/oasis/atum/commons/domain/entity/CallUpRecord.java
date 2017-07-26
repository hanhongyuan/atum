package com.oasis.atum.commons.domain.entity;

import com.oasis.atum.base.infrastructure.util.IdWorker;
import com.oasis.atum.base.infrastructure.util.Validator;
import com.oasis.atum.commons.domain.cmd.CallUpRecordCmd;
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
	private String    id;
	//呼叫
	private String    callMobile;
	//被呼叫
	private String    callToMobile;
	//最大通话时长
	private Long      maxCallTime;
	//本次通话时长
	private Long      callTime;
	//通话类型
	private CallType  callType;
	//通话振铃时间
	private Date      ringTime;
	//接通时间
	private Date      beginTime;
	//挂断时间
	private Date      endTime;
	//通话状态
	private CallState callState;
	//通话录音文件名
	private String    recordFile;
	//录音文件存在服务器
	private String    fileServer;
	//接口调用是否成功
	private Boolean   isSuccess;
	/**
	 * a)Message:4 被叫已接听
	 * b)Message:0 或 8 线路繁忙/异常(某些情况下，也可能为 被叫拒接/振铃未接/占线/关机/空号)
	 * c)Message:3 被叫拒接/振铃未接/占线/关机/空号 d)Message:5 一般情况下为余额不足，请先检查账户资费。
	 */
	private String    message;
	private String    comment;
	private Date      createTime;
	@LastModifiedDate
	private Date      updateTime;

	public CallUpRecord(final CallUpRecordCmd.Create cmd)
	{
		val id = IdWorker.getFlowIdWorkerInstance().nextId() + "";
		//发布通话记录创建事件
		val event = CallUpRecordEvent.Created.builder().id(id).cmd(cmd).build();
		apply(event);
	}

	@CommandHandler
	public void update(final CallUpRecordCmd.Update cmd)
	{
		log.info("通话记录修改命令处理");
		//发布通话记录修改事件
		val event = CallUpRecordEvent.Updated.builder().id(cmd.id).cmd(cmd).build();
		apply(event);
	}

	@CommandHandler
	public void callback(final CallUpRecordCmd.Callback cmd)
	{
		log.info("通话记录回调命令处理");
		//发布通话记录回调事件
		val event = CallUpRecordEvent.Callbacked.builder().id(cmd.id).cmd(cmd).build();
		apply(event);
	}

	@CommandHandler
	public void save(final CallUpRecordCmd.Save cmd)
	{
		log.info("通话记录保存命令处理");
		//发布通话记录保存事件
		val event = CallUpRecordEvent.Saved.builder().id(cmd.id).cmd(cmd).build();
		apply(event);

	}

	@EventSourcingHandler
	public void handle(final CallUpRecordEvent.Created event)
	{
		id = event.id;
		callMobile = event.cmd.callMobile;
		callToMobile = event.cmd.callToMobile;
		maxCallTime = event.cmd.maxCallTime;
		createTime = event.cmd.createTime;
	}

	@EventSourcingHandler
	public void handle(final CallUpRecordEvent.Updated event)
	{
		callTime = Validator.either(event.cmd.callTime, callTime);
		callType = Validator.either(event.cmd.callType, callType);
		ringTime = Validator.either(event.cmd.ringTime, ringTime);
		beginTime = Validator.either(event.cmd.beginTime, beginTime);
		endTime = Validator.either(event.cmd.endTime, endTime);
		callState = Validator.either(event.cmd.callState, callState);
		recordFile = Validator.either(event.cmd.recordFile, recordFile);
		fileServer = Validator.either(event.cmd.fileServer, fileServer);
	}

	@EventSourcingHandler
	public void handle(final CallUpRecordEvent.Callbacked event)
	{
		isSuccess = Validator.either(event.cmd.isSuccess, isSuccess);
		message = Validator.either(event.cmd.message, message);
	}

	@EventSourcingHandler
	public void handle(final CallUpRecordEvent.Saved event)
	{
		callMobile = Validator.either(event.cmd.callMobile, callMobile);
		callToMobile = Validator.either(event.cmd.callToMobile, callToMobile);
		callTime = Validator.either(event.cmd.callTime, callTime);
		callType = Validator.either(event.cmd.callType, callType);
		ringTime = Validator.either(event.cmd.ringTime, ringTime);
		beginTime = Validator.either(event.cmd.beginTime, beginTime);
		endTime = Validator.either(event.cmd.endTime, endTime);
		callState = Validator.either(event.cmd.callState, callState);
		recordFile = Validator.either(event.cmd.recordFile, recordFile);
		fileServer = Validator.either(event.cmd.fileServer, fileServer);
		createTime = event.cmd.createTime;
	}

}
