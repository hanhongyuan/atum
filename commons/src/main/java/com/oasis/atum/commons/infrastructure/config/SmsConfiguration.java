package com.oasis.atum.commons.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云短信配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "sms")
public class SmsConfiguration
{
	/**
	 * 阿里云Key
	 */
	private String accessKey = "LTAI8bGX5GE39FF5";

	/**
	 * 阿里云密钥
	 */
	//FJeiAjpjMbM6Ac5lSOGC5e0ShHjLnR"
	private String accessSecret;

	/**
	 * 访问端点
	 */
	private String endPoint = "https://1126869279253886.mns.cn-beijing.aliyuncs.com/";

	/**
	 * 短信使用主题
	 */
	private String topic = "sms.topic-cn-beijing";

	/**
	 * 短信签名
	 */
	private String sign = "泓华医疗";

	/**
	 * 阿里云短信模版
	 */
	@Data
	@Configuration
	@ConfigurationProperties(prefix = "sms.template")
	public class TemplateConfiguration
	{
		/**
		 * 验证码模版
		 */
		private String captcha = "SMS_71161028";
	}
}