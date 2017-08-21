package com.oasis.atum.commons.infrastructure.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.oasis.atum.base.infrastructure.util.DateUtil;
import com.oasis.atum.base.infrastructure.util.EncryptionUtil;
import com.oasis.atum.commons.domain.request.MoorRequest;
import com.oasis.atum.commons.infrastructure.config.MoorConfiguration;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * 容联七陌基础服务
 */
@Slf4j
@Component
public class MoorClient
{
	private final WebClient         client;
	private final MoorConfiguration config;

	public MoorClient(final MoorConfiguration config)
	{
		this.client = WebClient.builder()
										.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
										.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE)
										.clientConnector(new ReactorClientHttpConnector())
										.build();
		this.config = config;
	}

	/**
	 * 容联七陌鉴权 请求头部分
	 * Base64编码(账户Id +冒号+时间戳)
	 * @param timeStamp
	 * @return
	 */
	private String authenticationHeader(final String timeStamp)
	{
		return EncryptionUtil.base64Encode(config.getAccount() + ":" + timeStamp);
	}

	/**
	 * 容联七陌鉴权 请求参数部分
	 * MD5编码(帐号Id + 帐号APISecret +时间戳)
	 * @param timeStamp
	 * @return
	 */
	private String authenticationParameter(final String timeStamp)
	{
		return EncryptionUtil.MD5(config.getAccount() + config.getSecret() + timeStamp);
	}

	private Mono<JSONObject> get(final String uri, final String... queryString)
	{
		return Flux.fromArray(queryString)
						 .log()
						 //规约
						 .reduce((x, y) -> x + "&" + y)
						 //uri?queryString
						 .map(s -> uri + "?" + s)
						 .defaultIfEmpty(uri)
						 .flatMap(s -> client.get()
														 .uri(s)
														 .ifNoneMatch("*")
														 .ifModifiedSince(ZonedDateTime.now())
														 .retrieve()
														 .bodyToMono(String.class))
						 .map(JSON::parseObject);
	}

	private <T> Mono<JSONObject> post(final String uri, final T data)
	{
		val timeStamp = DateUtil.datetimeStamp();

		return client.post()
						 .uri(uri + "?sig=" + authenticationParameter(timeStamp))
						 //接口鉴权
						 .header("Authorization", authenticationHeader(timeStamp))
						 .ifNoneMatch("*")
						 .ifModifiedSince(ZonedDateTime.now())
						 .body(BodyInserters.fromObject(data))
						 .retrieve()
						 .bodyToMono(String.class)
						 .map(JSON::parseObject);
	}

	/**
	 * 打电话
	 * @param data
	 * @return
	 */
	public Mono<JSONObject> callUp(final MoorRequest.CallUp data)
	{
		return Optional.ofNullable(data.maxCallTime)
						 //有限制通话时间
						 .map(t -> get(config.getCallUp(),
							 "Action=Webcall", "Account=" + config.getAccount(),
							 "PBX=" + config.getPBX(), "ServiceNo=" + config.getServiceNo(),
							 "Exten=" + data.exten, "Variable=phoneNum:" + data.variable,
							 "MaxCallTime=" + data.maxCallTime, config.getCallbackType(),
							 config.getWebCallType(), "CallBackUrl=" + config.getCallbackUrl(),
							 "ActionID=" + data.actionId))
						 //无限制
						 .orElseGet(() -> get(config.getCallUp(),
							 "Action=Webcall", "Account=" + config.getAccount(),
							 "PBX=" + config.getPBX(), "ServiceNo=" + config.getServiceNo(),
							 "Exten=" + data.exten, "Variable=phoneNum:" + data.variable,
							 config.getCallbackType(), config.getWebCallType(),
							 "CallBackUrl=" + config.getCallbackUrl(), "ActionID=" + data.actionId));
	}

	/**
	 * 电话挂断
	 * @param data
	 * @return
	 */
	public Mono<JSONObject> hangUp(final MoorRequest.HangUp data)
	{
		return post("http://apis.7moor.com/v20160818/call/hangup/"+config.getAccount(),data);
	}
}
