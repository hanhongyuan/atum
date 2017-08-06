package com.oasis.atum.wechat;

import com.oasis.atum.base.infrastructure.config.AxonConfiguration;
import com.oasis.atum.base.infrastructure.config.HttpConfiguration;
import com.oasis.atum.base.infrastructure.config.RedisConfiguration;
import com.oasis.atum.base.infrastructure.service.RedisClient;
import com.oasis.atum.wechat.infrastructure.config.WechatConfiguration;
import com.oasis.atum.wechat.infrastructure.service.WechatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

/**
 * 微信模块
 */
@EnableMongoAuditing
@SpringCloudApplication
@ImportAutoConfiguration(value = {AxonConfiguration.class, HttpConfiguration.class, RedisConfiguration.class})
public class WechatApp implements CommandLineRunner
{
	private final WechatClient client;
	private final RedisClient redis;

	/**
	 * @apiDefine base
	 * @apiHeader {String} [Content-Type=application/json;charset=UTF-8] application/json;charset=UTF-8
	 * @apiHeader {String} [Accept=application/json;charset=UTF-8] application/json;charset=UTF-8
	 * @apiHeaderExample {json} 请求头样例:
	 * {
	 * "Content-Type":"application/json;charset=UTF-8",
	 * "Accept": "application/json;charset=UTF-8"
	 * }
	 */
	public WechatApp(final WechatClient client, final RedisClient redis)
	{
		this.client = client;
		this.redis = redis;
	}

	public static void main(final String[] args)
	{
		SpringApplication.run(WechatApp.class, args);
	}

	@Override
	public void run(final String... args) throws Exception
	{
		System.out.println(redis);

		//每隔110分钟运行一次
		Flux.interval(Duration.ofMinutes(110L), Schedulers.parallel())
			.subscribe(l -> client.initWechat());

		redis.get("AccessToken").subscribe(System.out::println);

	}
}
