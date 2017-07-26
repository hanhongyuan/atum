package com.oasis.atum.wechat.interfaces.dto;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import com.oasis.atum.wechat.domain.enums.qrcode.QRCodeType;
import lombok.Builder;

/**
 * 二维码Dto
 */
@Builder
public class QRCodeDTO
{
	public final String     id;
	public final QRCodeType type;
	public final String     sceneId;
	public final String     sceneStr;
	public final Integer    expireSeconds;
	public final String     ticket;
	public final String     uri;

	@JSONCreator
	public QRCodeDTO(@JSONField(name = "id") final String id, @JSONField(name = "type") final QRCodeType type,
									 @JSONField(name = "sceneId") final String sceneId, @JSONField(name = "sceneStr") final String sceneStr,
									 @JSONField(name = "expireSeconds") final Integer expireSeconds, @JSONField(name = "ticket") final String ticket,
									 @JSONField(name = "uri") final String uri)
	{
		this.id = id;
		this.type = type;
		this.sceneId = sceneId;
		this.sceneStr = sceneStr;
		this.expireSeconds = expireSeconds;
		this.ticket = ticket;
		this.uri = uri;
	}
}
