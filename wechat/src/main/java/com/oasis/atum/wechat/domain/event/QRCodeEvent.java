package com.oasis.atum.wechat.domain.event;


import com.oasis.atum.wechat.domain.cmd.QRCodeCmd;
import lombok.Builder;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

/**
 * 二维码事件集
 * Created by ryze on 2017/7/3.
 */
public interface QRCodeEvent
{
	/**
	 * 创建
	 */
	@Builder
	final class Created
	{
		@TargetAggregateIdentifier
		public final String           id;
		public final QRCodeCmd.Create cmd;
	}

	/**
	 * 修改
	 */
	@Builder
	final class Updated
	{
		@TargetAggregateIdentifier
		public final String           id;
		public final QRCodeCmd.Update cmd;
	}

}
