package com.oasis.atum.wechat.domain.handler;


import com.oasis.atum.wechat.domain.entity.QRCode;
import com.oasis.atum.wechat.domain.event.QRCodeEvent;
import com.oasis.atum.wechat.infrastructure.repository.QRCodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

/**
 * 二维码事件处理
 */
@Slf4j
@Component
public class QRCodeEventHandler
{
	private final Repository<QRCode> repository;
	private final QRCodeRepository   persistence;

	public QRCodeEventHandler(final Repository<QRCode> repository, final QRCodeRepository persistence)
	{
		this.repository = repository;
		this.persistence = persistence;
	}

	@EventHandler
	public void handle(final QRCodeEvent.Created event)
	{
		log.info("二维码创建事件处理");

		repository.load(event.id).execute(persistence::insert);
	}

	@EventHandler
	public void handle(final QRCodeEvent.Updated event)
	{
		log.info("二维码修改事件处理");

		repository.load(event.id).execute(persistence::save);
	}
}
