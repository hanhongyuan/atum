package com.oasis.atum.wechat.domain.entity;


import com.oasis.atum.base.infrastructure.util.IdWorker;
import com.oasis.atum.base.infrastructure.util.Validator;
import com.oasis.atum.wechat.domain.cmd.QRCodeCmd;
import com.oasis.atum.wechat.domain.enums.qrcode.QRCodeType;
import com.oasis.atum.wechat.domain.event.QRCodeEvent;
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
 * 二维码聚合根
 */
@Data
@Slf4j
@Aggregate
@NoArgsConstructor
@Document(collection = "qr_code")
public class QRCode
{
	@Id
	@AggregateIdentifier
	private String     id;
	// 二维码类型，QR_SCENE为临时,QR_LIMIT_SCENE为永久,QR_LIMIT_STR_SCENE为永久的字符串参数值
	private QRCodeType type;
	// 场景值ID，临时二维码时为32位非0整型，永久二维码时最大值为100000（目前参数只支持1--100000）
	private String     sceneId;
	// 场景值ID（字符串形式的ID），字符串类型，长度限制为1到64，仅永久二维码支持此字段
	private String     sceneStr;
	// 该二维码有效时间，以秒为单位。 最大不超过2592000（即30天），此字段如果不填，则默认有效期为30秒。
	private Integer    expireSeconds;
	// 获取的二维码ticket，凭借此ticket可以在有效时间内换取二维码。
	private String     ticket;
	// 转成短链接二维码
	private String     uri;
	private Date       createTime;
	@LastModifiedDate
	private Date       updateTime;

	/**
	 * 二维码创建
	 * @param cmd
	 */
	public QRCode(final QRCodeCmd.Create cmd)
	{
		val id = IdWorker.getFlowIdWorkerInstance().nextId() + "";
		//发布二维码创建事件
		val event = QRCodeEvent.Created.builder().id(id).cmd(cmd).build();
		apply(event);
	}

	/**
	 * 二维码修改
	 * @param cmd
	 */
	@CommandHandler
	public void update(final QRCodeCmd.Update cmd)
	{
		log.info("二维码修改命令处理");
		//发布二维码修改事件
		val event = QRCodeEvent.Updated.builder().id(cmd.id).cmd(cmd).build();
		apply(event);
	}

	@EventSourcingHandler
	public void handle(final QRCodeEvent.Created event)
	{
		id = event.id;
		type = event.cmd.type;
		sceneId = event.cmd.sceneId;
		sceneStr = event.cmd.sceneStr;
		expireSeconds = event.cmd.expireSeconds;
		createTime = event.cmd.createTime;
	}

	@EventSourcingHandler
	public void handle(final QRCodeEvent.Updated event)
	{
		type = Validator.either(event.cmd.type, type);
		uri = Validator.either(event.cmd.uri, uri);
		sceneId = Validator.either(event.cmd.sceneId, sceneId);
		sceneStr = Validator.either(event.cmd.sceneStr, sceneStr);
		ticket = Validator.either(event.cmd.ticket, ticket);
	}
}
