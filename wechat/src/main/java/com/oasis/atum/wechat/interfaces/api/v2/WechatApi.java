package com.oasis.atum.wechat.interfaces.api.v2;

import com.oasis.atum.base.infrastructure.service.RedisClient;
import com.oasis.atum.base.infrastructure.util.Restful;
import com.oasis.atum.wechat.application.service.TagService;
import com.oasis.atum.wechat.infrastructure.config.WechatConfiguration;
import com.oasis.atum.wechat.infrastructure.constant.EventType;
import com.oasis.atum.wechat.infrastructure.constant.MsgType;
import com.oasis.atum.wechat.infrastructure.service.WechatClient;
import com.oasis.atum.wechat.infrastructure.util.XMLUtil;
import com.oasis.atum.wechat.interfaces.request.WechatRequest;
import com.oasis.atum.wechat.interfaces.response.OpenId;
import com.oasis.atum.wechat.interfaces.response.WechatResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.beans.EventHandler;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 微信公众号接口
 */
@Slf4j
@RestController
@RequestMapping("v2")
public class WechatApi
{
	private final RedisClient         redis;
	private final WechatClient        client;
	private final WechatConfiguration config;
	private final TagService          tagService;

	public WechatApi(final RedisClient redis, final WechatClient client, final WechatConfiguration config, final TagService tagService)
	{
		this.redis = redis;
		this.client = client;
		this.config = config;
		this.tagService = tagService;
	}

	@GetMapping("{openId}")
	public Mono<ResponseEntity> openId(@PathVariable final String openId)
	{
		log.info("用户微信相关信息 =====> {}", openId);
		return redis.getData(openId, OpenId.class).map(Restful::ok);
	}

	/**
	 * 确认消息来自微信服务器
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @param echostr
	 * @return
	 */
	@GetMapping
	public Mono<Void> get(@RequestParam final String signature, @RequestParam final String timestamp,
												@RequestParam final String nonce, @RequestParam final String echostr,
												final ServerHttpResponse response)
	{
		log.info("校验消息来源是否微信");
		// 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
		return client.check(signature, timestamp, nonce).filter(b -> b).flatMap(b ->
		{
			val buffer = response.bufferFactory().allocateBuffer().write(echostr.getBytes(StandardCharsets.UTF_8));
			return response.writeWith(Mono.just(buffer)).then();
		});
	}
//	@GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)

	/**
	 * 处理微信服务器发来的消息(XML格式)
	 * @param request
	 * @return
	 */
	@PostMapping
	public Mono<String> post(final ServerHttpRequest request)
	{
		log.info("处理微信消息(XML)");
		//解析成Wechat
		return XMLUtil.parseXML(request)
			.flatMap(r ->
			{
				val openId = r.FromUserName;
				log.info("openId =====> {}", openId);
				val msgType = r.MsgType;
				log.info("消息类型 =====> {}", msgType);

				switch (msgType)
				{
					//文本
					case MsgType.TEXT:
						//图片
					case MsgType.IMAGE:
						//链接
					case MsgType.LINK:
						//语音
					case MsgType.VOICE:
						//视频
					case MsgType.VIDEO:
						//短视频
					case MsgType.SHORT_VIDEO:
						log.info("临时统一处理");
						return Mono.just(textResponse(r, "泓华医疗,让健康更简单!"));
					//事件
					case MsgType.EVENT:
						return eventHandle(r);
					//地理位置
					case MsgType.LOCATION:
						val latitude = r.Location_X;
						val longitude = r.Location_Y;
						val scale = r.Scale;
						val label = r.Label;

						//用户信息
						return redis.getData(openId, OpenId.class).defaultIfEmpty(new OpenId())
										 //用户地理位置
										 .map(d ->
										 {
											 val l = d.getLocation();
											 if (Objects.nonNull(l)) d.createLocation(latitude, longitude, scale, label, l.precision);
											 else d.createLocation(latitude, longitude, scale, label, null);
											 return d;
										 }).map(d -> redis.put(openId, d))
										 .map(b -> "success");
					default:
						return Mono.empty();
				}
			});
	}

	/**
	 * 处理微信事件
	 * @param wechat 微信消息
	 */
	private Mono<String> eventHandle(final WechatRequest wechat)
	{
		val openId = wechat.FromUserName;
		val event  = wechat.Event;
		log.info("事件 =====> {}", event);
		val key = wechat.EventKey;
		log.info("事件Key =====> {}", key);
		switch (event)
		{
			//已关注
			case EventType.SCAN:
				log.info("已关注扫码");
				//关注
			case EventType.SUBSCRIBE:
				log.info("谢谢您的关注");
				Mono.justOrEmpty(key)
					//诊所二维码
					.filter(s -> s.contains("clinic"))
					/**
					 * 诊所ID
					 * 首次关注格式 qrscene_clinidId=1
					 * 已关注格式 clinicId=1
					 */
					.map(s -> event.equals(EventType.SCAN) ? s.split("=")[1] : s.substring(8).split("=")[1])
					.flatMap(id ->
					{
						//标签添加粉丝
						tagService.addFans(config.getWxId(), Arrays.asList(openId)).subscribe();
						//更新用户信息
						redis.getData(openId, OpenId.class).defaultIfEmpty(new OpenId())
							.flatMap(d ->
							{
								//覆盖之前关注诊所
								d.setClinic(id);
								//是否关注
								d.setFollow(true);
								//存储用户信息
								return redis.put(openId, d);
							});
						//返回绑定绑定图文消息
						return newsHandle(wechat, true);
					})
					//没有绑定诊所图文
					.switchIfEmpty(newsHandle(wechat, false));
				//上报地理位置
			case EventType.LOCATION:
				log.info("用户地理位置");
				//维度
				val latitude = wechat.Latitude;
				//经度
				val longitude = wechat.Longitude;
				//精度
				val precision = wechat.Precision;
				//获取用户信息
				return redis.getData(openId, OpenId.class).defaultIfEmpty(new OpenId())
								 //用户地理位置
								 .map(d ->
								 {
									 val l = d.getLocation();
									 if (Objects.nonNull(l)) d.createLocation(latitude, longitude, l.scale, l.label, precision);
									 else d.createLocation(latitude, longitude, null, null, precision);
									 return d;
								 })
								 //更新用户信息
								 .flatMap(d -> redis.put(openId, d))
								 .map(b -> "success");
			case EventType.VIEW:
				log.info("跳转地址 =====> {}", wechat.EventKey);
			default:
				return Mono.empty();
		}
	}

	/**
	 * 处理微信图文消息
	 * @param wechat
	 * @return
	 */
	private Mono<String> newsHandle(final WechatRequest wechat, boolean type)
	{
		log.info("图文消息处理");
		//正式6Xjge0ynQGVPMd5ib0xckh76O4Unu0uYXJPv5OCVqfE
		//测试utYF-mbxCfQjXVfATrQAb29mVbtQB0ldJvq96tJszac
		//转成微信需要的图文消息格式
		return client.getNewsMaterials("utYF-mbxCfQjXVfATrQAb29mVbtQB0ldJvq96tJszac")
						 .map(s -> s.map(d ->
						 {
							 val builder = WechatResponse.NewsArticle.builder().Title(d.title).Description(d.digest)
															 .PicUrl(d.thumbUri);
							 //有诊所返回诊所详情
							 if (type) builder.Url("https://mtest.oasiscare.cn/wxofficial/clinicdetails.html");
								 //没有
							 else builder.Url(d.uri);
							 return builder.build();
						 }))
						 .flatMap(Flux::collectList)
						 .map(d -> newsResponse(wechat, d));
	}

	/**
	 * 响应消息基类
	 * @param wechatResponse
	 * @return
	 */
	private String response(final WechatResponse wechatResponse)
	{
		val xml = XMLUtil.toXML(wechatResponse);
		log.info("返回给微信 =====> {}", xml);
		return xml;
	}

	/**
	 * 文本消息响应
	 * @param wechat
	 * @param content
	 * @return
	 */
	private String textResponse(final WechatRequest wechat, String content)
	{
		val data = WechatResponse.Text.builder()
								 .ToUserName(wechat.FromUserName)
								 .FromUserName(wechat.ToUserName)
								 .CreateTime(wechat.CreateTime)
								 .MsgType(MsgType.TEXT)
								 .Content(content)
								 .build();

		return response(data);
	}

	/**
	 * 图文消息响应
	 * @param wechat
	 * @return
	 */
	private String newsResponse(final WechatRequest wechat, final List<WechatResponse.NewsArticle> articles)
	{
		val data = WechatResponse.News.builder()
								 .ToUserName(wechat.FromUserName)
								 .FromUserName(wechat.ToUserName)
								 .CreateTime(wechat.CreateTime)
								 .MsgType(MsgType.NEWS)
								 .ArticleCount(articles.size())
								 .Articles(articles)
								 .build();

		return response(data);
	}
}
