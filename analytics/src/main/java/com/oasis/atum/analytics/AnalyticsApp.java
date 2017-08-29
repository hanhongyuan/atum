package com.oasis.atum.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 数据分析模块
 */
@EnableDiscoveryClient
@SpringBootApplication
public class AnalyticsApp
{

	public static void main(final String[] args)
	{
		SpringApplication.run(AnalyticsApp.class, args);
	}

}
