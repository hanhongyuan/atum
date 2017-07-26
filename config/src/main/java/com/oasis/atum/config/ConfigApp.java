package com.oasis.atum.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * 配置管理
 */
@EnableConfigServer
@EnableDiscoveryClient
@SpringBootApplication
public class ConfigApp
{
	public static void main(final String[] args)
	{
		SpringApplication.run(ConfigApp.class,args);
	}
}
