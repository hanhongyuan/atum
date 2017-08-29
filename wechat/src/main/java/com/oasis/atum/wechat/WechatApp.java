package com.oasis.atum.wechat;

import com.oasis.atum.base.infrastructure.config.AxonConfiguration;
import com.oasis.atum.base.infrastructure.config.RedisConfiguration;
import com.oasis.atum.base.infrastructure.config.ServerConfiguration;
import com.oasis.atum.wechat.infrastructure.service.WechatClient;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * 微信模块
 */
@AllArgsConstructor
@EnableMongoAuditing
@SpringCloudApplication
@ImportAutoConfiguration(value = {AxonConfiguration.class, ServerConfiguration.class, RedisConfiguration.class})
public class WechatApp implements CommandLineRunner
{
	private final WechatClient client;

	public static void main(final String[] args)
	{
		SpringApplication.run(WechatApp.class, args);
	}

	@Override
	public void run(final String... args) throws Exception
	{
		//每隔110分钟运行一次
		Flux.interval(Duration.ofMinutes(110L))
				.subscribe(l -> client.initWechat());

	}
}
