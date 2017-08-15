package com.oasis.atum.commons.infrastructure.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.oasis.atum.commons.domain.request.CallUpRequest;
import com.oasis.atum.commons.infrastructure.config.MoorConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
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
										.clientConnector(new ReactorClientHttpConnector())
										.build();
		this.config = config;
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

	/**
	 * 打电话
	 * @param data
	 * @return
	 */
	public Mono<JSONObject> callUp(final CallUpRequest data)
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
}
