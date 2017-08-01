package com.oasis.atum.commons;

import com.oasis.atum.base.infrastructure.config.AxonConfiguration;
import com.oasis.atum.base.infrastructure.config.HttpConfiguration;
import com.oasis.atum.base.infrastructure.config.RedisConfiguration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * 公用模块
 */
@EnableMongoAuditing
@SpringCloudApplication
@ImportAutoConfiguration(value = {AxonConfiguration.class, RedisConfiguration.class, HttpConfiguration.class})
public class CommonsApp implements CommandLineRunner
{

	public static void main(final String[] args)
	{
		SpringApplication.run(CommonsApp.class, args);
	}

	@Override
	public void run(final String... args) throws Exception
	{
	}
}
