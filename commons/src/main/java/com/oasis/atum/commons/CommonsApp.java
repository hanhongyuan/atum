package com.oasis.atum.commons;

import com.oasis.atum.base.infrastructure.config.AxonConfiguration;
import com.oasis.atum.base.infrastructure.config.RedisConfiguration;
import com.oasis.atum.base.infrastructure.config.ServerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * 公用模块
 */
@EnableMongoAuditing
@SpringCloudApplication
@ImportAutoConfiguration(value = {AxonConfiguration.class, RedisConfiguration.class, ServerConfiguration.class})
public class CommonsApp
{
	public static void main(final String[] args)
	{
		SpringApplication.run(CommonsApp.class, args);
	}
}
