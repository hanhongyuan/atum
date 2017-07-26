package com.oasis.atum.wechat.application.service.impl;

import com.oasis.atum.wechat.application.service.QRCodeService;
import com.oasis.atum.wechat.domain.cmd.QRCodeCmd;
import com.oasis.atum.wechat.domain.request.qrcode.QRCodeRequest;
import com.oasis.atum.wechat.infrastructure.repository.QRCodeRepository;
import com.oasis.atum.wechat.infrastructure.service.WechatClient;
import com.oasis.atum.wechat.interfaces.assembler.QRCodeAssembler;
import com.oasis.atum.wechat.interfaces.dto.QRCodeDTO;
import lombok.val;
import org.axonframework.commandhandling.callbacks.FutureCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

/**
 * 二维码应用服务实现类
 */
@Service
@Transactional
public class QRCodeServiceImpl implements QRCodeService
{
	private final WechatClient     client;
	private final QRCodeRepository persistence;
	private final CommandGateway   commandGateway;

	public QRCodeServiceImpl(final WechatClient client, final QRCodeRepository persistence, final CommandGateway commandGateway)
	{
		this.client = client;
		this.persistence = persistence;
		this.commandGateway = commandGateway;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
	public Mono<QRCodeDTO> getQRCodeById(final String id)
	{
		return persistence.findById(id).map(QRCodeAssembler::toDTO);
	}

	@Override
	public Mono<Void> create(final QRCodeDTO dto)
	{
		//二维码创建命令
		val cmd = QRCodeCmd.Create.builder().id(dto.id).type(dto.type).sceneId(dto.sceneId)
								.sceneStr(dto.sceneStr).expireSeconds(dto.expireSeconds).createTime(new Date()).build();
		//异步命令处理结果
		val future = new FutureCallback<QRCodeCmd.Create, QRCodeRequest>();
		//发送命令
		commandGateway.send(cmd, future);

		return Mono.fromFuture(future.toCompletableFuture())
						 .flatMap(d ->
						 {
							 //请求微信创建二维码
							 return client.createQRCode(d).flatMap(j ->
							 {
								 System.out.println(j);
								 //票根
								 val ticket = j.getString("ticket");
								 //票根换取二维码图片并转成短链接
								 val json = d.long2Short(ticket);
								 //请求微信生成短链接
								 return client.createShortURI(json).map(js -> QRCodeCmd.Update.builder().id(d.id).type(dto.type).sceneId(dto.sceneId)
																																.sceneStr(dto.sceneStr).ticket(ticket).uri(js.getString("short_url")).build());
							 });
						 })
						 //发送命令 修改二维码
						 .map(commandGateway::send)
						 .then();
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
	public Flux<QRCodeDTO> getQRCodes(final Pageable pageable)
	{
		return persistence.findAll()
						 .skip(pageable.getPageNumber())
						 .limitRate(pageable.getPageSize())
						 .map(QRCodeAssembler::toDTO);
	}
}
