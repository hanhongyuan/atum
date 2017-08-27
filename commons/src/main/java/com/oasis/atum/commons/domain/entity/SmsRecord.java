package com.oasis.atum.commons.domain.entity;

import com.oasis.atum.base.infrastructure.util.IdWorker;
import com.oasis.atum.commons.domain.cmd.SmsRecordCmd;
import com.oasis.atum.commons.domain.event.SmsRecordEvent;
import com.oasis.atum.commons.infrastructure.enums.SmsType;
import io.vavr.collection.List;
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

import static io.vavr.API.Option;
import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

/**
 * 短信记录聚合根
 */
@Data
@Slf4j
@Aggregate
@NoArgsConstructor
@Document(collection = "sms_record")
public class SmsRecord
{
	@Id
	@AggregateIdentifier
	private String       id;
	private String       messageId;
	//发送条数
	private int          count;
	//发送手机号
	private List<String> mobiles;
	//内容
	private String       content;
	//短信类型
	private SmsType      smsType;
	//是否业务短信
	private Boolean      isBusiness;
	//短信接收成功
	private Boolean      isSuccess;
	//失败错误码
	private String       errCode;
	//短信接收时间
	private Date         receiveTime;
	private Date         createTime;
	@LastModifiedDate
	private Date         updateTime;

	public SmsRecord(final SmsRecordCmd.Create cmd)
	{
		val id = IdWorker.getFlowIdWorkerInstance().nextSID();
		//发布短信记录创建事件
		apply(SmsRecordEvent.Created.builder().id(id).cmd(cmd).build());
	}

	@CommandHandler
	public void success(final SmsRecordCmd.Success cmd)
	{
		log.info("短信记录成功回调命令处理");
		//发布短信记录成功回调事件
		val event = SmsRecordEvent.Succeed.builder().id(id).cmd(cmd).build();
		apply(event);
	}

	@CommandHandler
	public void fail(final SmsRecordCmd.Fail cmd)
	{
		log.info("短信记录失败回调命令处理");
		//发布短信记录失败回调事件
		val event = SmsRecordEvent.Failed.builder().id(id).cmd(cmd).build();
		apply(event);
	}

	@CommandHandler
	public void reply(final SmsRecordCmd.Reply cmd)
	{
		log.info("短信记录回复回调命令处理");
		//发布短信记录回复回调事件
		val event = SmsRecordEvent.Replied.builder().id(id).cmd(cmd).build();
		apply(event);
	}

	@EventSourcingHandler
	public void handle(final SmsRecordEvent.Created event)
	{
		this.id = event.id;
		this.mobiles = List.of(event.cmd.mobile);
		this.smsType = event.cmd.smsType;
		this.isBusiness = event.cmd.isBusiness;
		this.messageId = event.cmd.messageId;
		this.createTime = event.cmd.createTime;
	}

	@EventSourcingHandler
	public void handle(final SmsRecordEvent.Succeed event)
	{
		this.isSuccess = Option(event.cmd.isSuccess).getOrElse(isSuccess);
		this.count = Option(event.cmd.count).getOrElse(count);
		this.receiveTime = Option(event.cmd.receiveTime).getOrElse(receiveTime);
		this.messageId = Option(event.cmd.messageId).getOrElse(messageId);
	}

	@EventSourcingHandler
	public void handle(final SmsRecordEvent.Failed event)
	{
		this.isSuccess = Option(event.cmd.isSuccess).getOrElse(isSuccess);
		this.errCode = Option(event.cmd.errCode).getOrElse(errCode);
		this.messageId = Option(event.cmd.messageId).getOrElse(messageId);
	}

	@EventSourcingHandler
	public void handle(final SmsRecordEvent.Replied event)
	{
		this.isSuccess = Option(event.cmd.isSuccess).getOrElse(isSuccess);
	}

}
