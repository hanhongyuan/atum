package com.oasis.atum.wechat;

import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * 微信模块
 */
@EnableMongoAuditing
@SpringCloudApplication
public class WechatApp implements CommandLineRunner
{
	@Override
	public void run(final String... args) throws Exception
	{

	}
}
