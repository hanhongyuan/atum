package com.oasis.atum.wechat.domain.handler;

import com.oasis.atum.wechat.domain.cmd.QRCodeCmd;
import com.oasis.atum.wechat.domain.entity.QRCode;
import com.oasis.atum.wechat.domain.request.QRCodeInfo;
import com.oasis.atum.wechat.domain.request.QRCodeRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Repository;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 二维码命令处理
 */
@Slf4j
@Component
public class QRCodeCmdHandler
{
	private final Repository<QRCode> repository;

	public QRCodeCmdHandler(final Repository<QRCode> repository)
	{
		this.repository = repository;
	}

	@CommandHandler
	public QRCodeRequest handle(final QRCodeCmd.Create cmd)
	{
		log.info("二维码创建命令处理");
		try
		{
			return repository.newInstance(() -> new QRCode(cmd)).invoke(q ->
			{
				//转成微信请求格式数据
				val info = new QRCodeInfo();
				//场景Str
				val scene = q.getSceneStr();
				if (Objects.isNull(scene)) info.setSceneId(q.getSceneId());
				else info.setSceneStr(q.getSceneStr());

				return QRCodeRequest.builder().id(q.getId()).actionName(q.getType())
								 .qrCodeInfo(info).expireSeconds(q.getExpireSeconds()).build();
			});
		}
		catch (Exception e)
		{
			log.error("创建二维码出错", e);
			throw new IllegalArgumentException("创建二维码出错");
		}
	}
}
