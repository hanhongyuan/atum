package com.oasis.atum.wechat.interfaces.api.v2;

import com.oasis.atum.base.infrastructure.util.Restful;
import com.oasis.atum.wechat.infrastructure.config.WechatConfiguration;
import com.oasis.atum.wechat.infrastructure.service.WechatClient;
import com.oasis.atum.wechat.interfaces.request.TemplateRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 微信模版消息接口
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("v2/templates")
public class TemplateApi
{
	private final WechatClient                              client;
	private final WechatConfiguration.TemplateConfiguration template;

	@PostMapping("registered")
	public Mono<ResponseEntity> registered(@RequestBody final Mono<TemplateRequest.Send> data)
	{
		log.info("预约挂号成功通知");

		//设置模版信息
		return data.map(d -> d.setTemplateId(template.getRegister()))
						 //发送模版
						 .flatMap(client::sendTemplate)
						 .map(Restful::ok);
	}

	@PostMapping("succeed")
	public Mono<ResponseEntity> succeed(@RequestBody final Mono<TemplateRequest.Send> data)
	{
		log.info("订单支付成功通知");

		//设置模版信息
		return data.map(d -> d.setTemplateId(template.getSuccess()))
						 //发送模版
						 .flatMap(client::sendTemplate)
						 .map(Restful::ok);
	}

	@PostMapping("cancel")
	public Mono<ResponseEntity> cancel(@RequestBody final Mono<TemplateRequest.Send> data)
	{
		log.info("订单取消通知");

		//设置模版信息
		return data.map(d -> d.setTemplateId(template.getCancel()))
						 //发送模版
						 .flatMap(client::sendTemplate)
						 .map(Restful::ok);
	}

	@PostMapping("submitted")
	public Mono<ResponseEntity> submitted(@RequestBody final Mono<TemplateRequest.Send> data)
	{
		log.info("订单提交完成通知");

		//设置模版信息
		return data.map(d -> d.setTemplateId(template.getSubmit()))
						 //发送模版
						 .flatMap(client::sendTemplate)
						 .map(Restful::ok);
	}
}
