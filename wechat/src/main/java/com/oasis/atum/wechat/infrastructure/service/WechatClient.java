package com.oasis.atum.wechat.infrastructure.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.oasis.atum.base.infrastructure.service.RedisClient;
import com.oasis.atum.wechat.domain.request.qrcode.QRCodeRequest;
import com.oasis.atum.wechat.infrastructure.config.WechatConfiguration;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * 微信基础服务
 * POST函数对微信公众号无效 只能用同步请求 异步处理了
 * 其他函数没事
 */
@Slf4j
@Component
public class WechatClient
{
	private final WebClient           client;
	private final RedisClient         redis;
	private final RestTemplate        template;
	private final WechatConfiguration config;

	private static final String REDIS_KEY_ACCESSTOKEN  = "AccessToken";
	private static final String REDIS_KEY_JSAPI_TICKET = "JsApiTicket";
	private static final String REDIS_KEY_API_TICKET   = "ApiTicket";

	public WechatClient(final RedisClient redis, final RestTemplate template, final WechatConfiguration config)
	{
		this.client = WebClient.builder()
										.baseUrl("https://api.weixin.qq.com/")
										.clientConnector(new ReactorClientHttpConnector())
										.build();
		this.redis = redis;
		this.template = template;
		this.config = config;
	}

	private Mono<String> replaceAccessToken(@NonNull final String uri)
	{
		return accessToken().map(s -> uri.replace("access_token=#", "access_token=" + s));
	}

	private Mono<JSONObject> get(final String uri, final Object... queryString)
	{
		return get(uri, JSONObject.class, queryString);
	}

	private <REP> Mono<REP> get(final String uri, final Class<REP> clazz, final Object... queryString)
	{
		val monad = Flux.fromArray(queryString)
									//规约
									.reduce((x, y) -> x + "&" + y)
									//uri?queryString
									.map(s -> uri + "?" + s);

		//uri是否包含access_token=#
		return monad.filter(s -> s.contains("access_token=#"))
						 //替换
						 .flatMap(this::replaceAccessToken)
						 //不处理
						 .switchIfEmpty(monad.defaultIfEmpty(uri))
						 .flatMap(s -> client.get()
														 .uri(s)
														 .ifNoneMatch("*")
														 .ifModifiedSince(ZonedDateTime.now())
														 .retrieve()
														 .bodyToMono(clazz));

	}

	private <REQ> Mono<JSONObject> post(final String uri, final REQ data)
	{
		return post(uri, data, JSONObject.class);
	}

	private <REQ, REP> Mono<REP> post(final String uri, final REQ data, final Class<REP> clazz)
	{
		return Mono.just(uri)
						 //uri是否包含access_token=#
						 .filter(s -> s.contains("access_token=#"))
						 //如果有则替换成AccessToken
						 .flatMap(this::replaceAccessToken)
						 //没有不处理
						 .defaultIfEmpty(uri)
						 .map(s ->
						 {
							 val request = RequestEntity.post(URI.create(s))
															 .ifNoneMatch("*")
															 .contentType(MediaType.APPLICATION_JSON_UTF8)
															 .acceptCharset(Charsets.UTF_8)
															 .body(JSON.toJSONString(data));
							 return template.postForObject(URI.create(s), request, clazz);
						 });
	}

	/**
	 * 获取AccessToken
	 * @return
	 */
	public Mono<String> accessToken()
	{
		//先从Redis读取
		return redis.get(REDIS_KEY_ACCESSTOKEN)
						 //没有则网络请求微信服务器获取
						 .switchIfEmpty(get("cgi-bin/token", String.class, "grant_type=client_credential",
							 "appid=" + config.getAppId(),
							 "secret=" + config.getSecret())
															.map(JSON::parseObject)
															.map(j ->
															{
																val accessToken = j.getString("access_token");
																log.info("获取微信AccessToken =====> {}", accessToken);
																redis.put(REDIS_KEY_ACCESSTOKEN, accessToken, 109L).subscribe();
																return accessToken;
															}));
	}

	/**
	 * 获取JsApiTicket
	 * @return
	 */
	public Mono<String> jsApiTicket()
	{
		//先从Redis读取
		return redis.get(REDIS_KEY_JSAPI_TICKET)
						 //没有则网络请求微信服务器获取
						 .switchIfEmpty(get("cgi-bin/ticket/getticket", "access_token=#", "type=jsapi")
															.map(j ->
															{
																val jsApiTicket = j.getString("ticket");
																log.info("获取微信JsApiTicket: {}", jsApiTicket);
																redis.put(REDIS_KEY_JSAPI_TICKET, jsApiTicket, 109L).subscribe();
																return jsApiTicket;
															}));
	}

	/**
	 * 获取ApiTicket
	 * @return
	 */
	public Mono<String> apiTicket()
	{
		//先从Redis读取
		return redis.get(REDIS_KEY_API_TICKET)
						 //没有则网络请求微信服务器获取
						 .switchIfEmpty(get("cgi-bin/ticket/getticket", "access_token=#", "type=wx_card")
															.map(j ->
															{
																val apiTicket = j.getString("ticket");
																log.info("获取微信ApiTicket: {}", apiTicket);
																redis.put(REDIS_KEY_API_TICKET, apiTicket, 109L).subscribe();
																return apiTicket;
															}));
	}

	/**
	 * 获取OAuth2
	 * @param code
	 * @return
	 */
	public Mono<JSONObject> oAuth2(final String code)
	{
		return get("sns/oauth2/access_token", String.class, "appid=" + config.getAppId(),
			"secret=" + config.getSecret(), "code=" + code, "grant_type=authorization_code")
						 .map(JSON::parseObject);
	}

	/**
	 * 创建二维码
	 * @param data
	 * @return
	 */
	public Mono<JSONObject> createQRCode(final QRCodeRequest data)
	{
		return post("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=#", data);
	}

	/**
	 * 创建短链接
	 * @param json
	 * @return
	 */
	public Mono<JSONObject> createShortURI(final JSONObject json)
	{
		return post("https://api.weixin.qq.com/cgi-bin/shorturl?access_token=#", json);
	}



	/**
	 * 初始化微信
	 */
	public void initWechat()
	{
		//获取AccessToken
		accessToken().subscribe(s ->
		{
			//获取JsApiTicket
			if (config.isEnableJsApi()) jsApiTicket().subscribe();
			//获取ApiTicket
			if (config.isEnableApiTicket()) apiTicket().subscribe();
		});
	}
}
