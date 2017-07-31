package com.oasis.atum.wechat.application.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.oasis.atum.wechat.application.service.TagService;
import com.oasis.atum.wechat.domain.cmd.TagCmd;
import com.oasis.atum.wechat.domain.request.TagRequest;
import com.oasis.atum.wechat.infrastructure.repository.TagRepository;
import com.oasis.atum.wechat.infrastructure.service.WechatClient;
import com.oasis.atum.wechat.interfaces.dto.TagDTO;
import lombok.val;
import org.axonframework.commandhandling.callbacks.FutureCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;

/**
 * 用户标签应用服务实现类
 */
@Service
@Transactional
public class TagServiceImpl implements TagService
{
	private final WechatClient   client;
	private final TagRepository  repository;
	private final CommandGateway commandGateway;

	public TagServiceImpl(final WechatClient client, final TagRepository repository, final CommandGateway commandGateway)
	{
		this.client = client;
		this.repository = repository;
		this.commandGateway = commandGateway;
	}

	@Override
	public Mono<Void> create(final TagDTO dto)
	{
		return Mono.justOrEmpty(dto)
						 //创建命令
						 .map(d -> TagCmd.Create.builder().id(d.id).name(d.name).createTime(new Date()).build())
						 .map(c ->
						 {
							 //异步命令处理结果
							 val future = new FutureCallback<TagCmd.Create, TagRequest.Create>();
							 //发送命令 - 创建标签
							 commandGateway.send(c, future);
							 return future.toCompletableFuture();
						 })
						 .flatMap(Mono::fromFuture)
						 //请求微信创建标签
						 .flatMap(d -> client.createTag(d)
														 .map(j -> j.getJSONObject("tag").getInteger("id"))
														 //创建命令
														 .map(id -> TagCmd.Update.builder().id(d.id).wxId(id).build()))
						 //发送命令 - 修改标签
						 .map(commandGateway::send)
						 .then();
	}

	@Override
	public Mono<JSONObject> addFans(final Integer wxId, final List<String> openIds)
	{
		return repository.findByWxId(wxId)
						 //创建命令
						 .map(d -> TagCmd.AddFans.builder().id(d.getId()).openIds(openIds).build())
						 .map(c ->
						 {
							 //异步命令
							 val future = new FutureCallback<TagCmd.AddFans, TagRequest.AddFans>();
							 //发送命令
							 commandGateway.send(c, future);
							 return future.toCompletableFuture();
						 })
						 .flatMap(Mono::fromFuture)
						 //请求微信给标签添加粉丝
						 .flatMap(client::addTagFans);
	}

	@Override
	public Mono<JSONObject> update(final TagDTO dto)
	{
		return Mono.justOrEmpty(dto)
						 //创建命令
						 .map(d -> TagCmd.Update.builder().id(d.id).wxId(d.wxId).name(d.name).build())
						 .map(c ->
						 {
							 //异步命令
							 val future = new FutureCallback<TagCmd.Update, TagRequest.Update>();
							 //发送命令
							 commandGateway.send(c, future);
							 return future.toCompletableFuture();
						 })
						 .flatMap(Mono::fromFuture)
						 //请求微信修改标签
						 .flatMap(client::updateTag);
	}
}
