package com.oasis.atum.commons.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 容联七陌配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "moor")
public class MoorConfiguration
{
	/**
	 * 颁发的用户编号
	 */
	private String account   = "N00000014027";
	private String PBX       = "bj.ali.8.8";
	/**
	 * 服务号，想要被叫进入的服务号码
	 */
	private String serviceNo = "01025270023";

	/**
	 * 打电话
	 */
	private String callUp       = "http://121.40.153.25/command";
	/**
	 * 异步回调
	 */
	private String webCallType  = "WebCallType=asynchronous";
	/**
	 * 回调方式
	 */
	private String callbackType = "CallBackType=post";
	/**
	 * 回调地址
	 */
	private String callbackUrl  = "http://wechat.oasiscare.cn/wechat/v2/7moor/call-up/back";
}

