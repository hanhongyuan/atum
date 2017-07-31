package com.oasis.atum.wechat.infrastructure.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.oasis.atum.base.infrastructure.service.RedisClient;
import com.oasis.atum.base.infrastructure.util.CommonUtil;
import com.oasis.atum.base.infrastructure.util.EncryptionUtil;
import com.oasis.atum.wechat.domain.request.MenuRequest;
import com.oasis.atum.wechat.domain.request.QRCodeRequest;
import com.oasis.atum.wechat.domain.request.TagRequest;
import com.oasis.atum.wechat.infrastructure.config.WechatConfiguration;
import com.oasis.atum.wechat.interfaces.request.TemplateRequest;
import com.oasis.atum.wechat.interfaces.response.WechatResponse;
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

import java.net.URI;
import java.nio.charset.StandardCharsets;
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

	public <REQ> Mono<JSONObject> post(final String uri, final REQ data)
	{
		return post(uri, data, JSONObject.class);
	}

	public <REQ, REP> Mono<REP> post(final String uri, final REQ data, final Class<REP> clazz)
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
															 .body(data);
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
																log.info("获取微信JsApiTicket =====> {}", jsApiTicket);
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
																log.info("获取微信ApiTicket =====> {}", apiTicket);
																redis.put(REDIS_KEY_API_TICKET, apiTicket, 109L).subscribe();
																return apiTicket;
															}));
	}

	/**
	 * 获取OAuth2
	 * @param code
	 * @return
	 */
	public Mono<JSONObject> oauth2(final String code)
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
	public Mono<JSONObject> createQRCode(final QRCodeRequest.Create data)
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
	 * 发送模版
	 * @param data
	 * @return
	 */
	public Mono<JSONObject> sendTemplate(final TemplateRequest.Send data)
	{
		return post("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=#", data);
	}

	/**
	 * 重置菜单
	 * @param data
	 * @return
	 */
	public Mono<JSONObject> resetMenu(final MenuRequest.Create data)
	{
		return post("https://api.weixin.qq.com/cgi-bin/menu/create?access_token=#", data);
	}

	/**
	 * 客制化菜单
	 * @param data
	 * @return
	 */
	public Mono<JSONObject> customMenu(final MenuRequest.Create data)
	{
		return post("https://api.weixin.qq.com/cgi-bin/menu/addconditional?access_token=#", data);
	}

	/**
	 * 创建标签
	 * @param data
	 * @return
	 */
	public Mono<JSONObject> createTag(final TagRequest.Create data)
	{
		return post("https://api.weixin.qq.com/cgi-bin/tags/create?access_token=#", data);
	}

	/**
	 * 修改标签
	 * @param data
	 * @return
	 */
	public Mono<JSONObject> updateTag(final TagRequest.Update data)
	{
		return post("https://api.weixin.qq.com/cgi-bin/tags/update?access_token=#", data);
	}

	/**
	 * 添加标签粉丝
	 * @param data
	 * @return
	 */
	public Mono<JSONObject> addTagFans(final TagRequest.AddFans data)
	{
		return post("https://api.weixin.qq.com/cgi-bin/tags/members/batchtagging?access_token=#", data);
	}

	/**
	 * 获取指定素材
	 * @param mediaId
	 * @return
	 */
	public Mono<Flux<WechatResponse.NewsItem>> getNewsMaterials(final String mediaId)
	{
		val request = new JSONObject();
		request.put("media_id", mediaId);
		return post("https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=#", request, String.class)
						 //微信数据编码格式需要转换
						 .map(s -> new String(s.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8))
						 //转成JSON
						 .map(JSON::parseObject)
						 //获取JSONArray
						 .map(j -> j.getJSONArray("news_item").stream()
												 .map(JSON::toJSONString)
												 //转成NewsItem对象
												 .map(s -> JSONObject.parseObject(s, WechatResponse.NewsItem.class))
						 )
						 .map(Flux::fromStream);
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

	/**
	 * js-sdk签名
	 * @param timeStamp
	 * @param nonceStr
	 * @param uri
	 * @return
	 */
	public Mono<String> jsSign(final long timeStamp, final String nonceStr, final String uri)
	{
		return jsApiTicket()
						 .map(s -> CommonUtil.getStringBuilder().append("jsapi_ticket=").append(s))
						 .map(sb -> sb.append("&noncestr=").append(nonceStr).append("&timestamp=")
													.append(timeStamp).append("&url=")
													.append(uri).toString())
						 .map(EncryptionUtil::SHA1);
	}

	/**
	 * 校验微信服务器签名
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @return
	 */
	public Mono<Boolean> check(final String signature, final String timestamp, final String nonce)
	{
		return Flux.just(config.getToken(), timestamp, nonce)
						 // 将token、timestamp、nonce三个参数进行字典序排序
						 .sort()
						 // 将三个参数字符串拼接成一个字符串进行sha1加密
						 .reduce((x, y) -> x + y)
						 //sha1加密后
						 .map(EncryptionUtil::SHA1)
						 // 字符串与signature对比，标识该请求来源于微信
						 .map(s -> s.equalsIgnoreCase(signature));
	}
}
