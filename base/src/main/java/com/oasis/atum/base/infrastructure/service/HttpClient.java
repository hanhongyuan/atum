package com.oasis.atum.base.infrastructure.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;

/**
 * 异步Http基础服务
 */
@Component
public class HttpClient
{
	private final WebClient client;

	public HttpClient()
	{
		this.client = WebClient.builder()
											.clientConnector(new ReactorClientHttpConnector())
											.build();
	}

	/**
	 * 异步Get
	 * @param uri
	 * @return
	 */
	public Mono<String> get(final String uri)
	{
		return get(uri);
	}

	/**
	 * 异步Get
	 * @param uri
	 * @param queryString
	 * @return
	 */
	public Mono<ClientResponse> get(final String uri, final String... queryString)
	{
		return Flux.fromArray(queryString)
							 .reduce((x, y) -> x + "&" + y)
							 .map(s -> uri + "?" + s)
							 .defaultIfEmpty(uri)
							 .flatMap(s -> client.get()
																 .uri(s)
																 .accept(MediaType.APPLICATION_JSON_UTF8)
																 .ifModifiedSince(ZonedDateTime.now())
																 .ifNoneMatch("*")
																 .exchange());
	}

	/**
	 * 异步Post
	 * @param <T>
	 * @param uri
	 * @param data
	 * @return
	 */
	public <T> Mono<ClientResponse> post(final String uri, final T data)
	{
		return client.post()
							 .uri(uri)
							 .contentType(MediaType.APPLICATION_JSON_UTF8)
							 .accept(MediaType.APPLICATION_JSON_UTF8)
							 .ifModifiedSince(ZonedDateTime.now())
							 .ifNoneMatch("*")
							 .body(BodyInserters.fromObject(JSON.toJSONString(data,
									 //格式化JSON
									 SerializerFeature.PrettyFormat,
									 //日期字符串输出
									 SerializerFeature.WriteDateUseDateFormat)))
							 .exchange();
	}
}
