package com.oasis.atum.wechat.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oasis.atum.wechat.domain.enums.QRCodeType;
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

	@JsonCreator
	public QRCodeDTO(@JsonProperty("id") final String id, @JsonProperty("type") final QRCodeType type,
									 @JsonProperty("sceneId") final String sceneId, @JsonProperty("sceneStr") final String sceneStr,
									 @JsonProperty("expireSeconds") final Integer expireSeconds, @JsonProperty("ticket") final String ticket,
									 @JsonProperty("uri") final String uri)
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
