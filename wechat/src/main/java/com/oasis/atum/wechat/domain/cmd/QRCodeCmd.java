package com.oasis.atum.wechat.domain.cmd;

import com.oasis.atum.wechat.domain.enums.QRCodeType;
import lombok.Builder;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

import java.util.Date;

/**
 * 二维码命令集
 */
public interface QRCodeCmd
{
	/**
	 * 创建
	 */
	@Builder
	final class Create implements QRCodeCmd
	{
		@TargetAggregateIdentifier
		public final String     id;
		public final QRCodeType type;
		public final String     sceneId;
		public final String     sceneStr;
		public final Integer    expireSeconds;
		public final Date       createTime;
	}

	/**
	 * 修改
	 */
	@Builder
	final class Update implements QRCodeCmd
	{
		@TargetAggregateIdentifier
		public final String id;
		public final QRCodeType type;
		public final String sceneId;
		public final String sceneStr;
		public final String ticket;
		public final String uri;
	}
}
