package com.oasis.atum.base.infrastructure.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter4;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.oasis.atum.base.infrastructure.constant.DateField;
import com.oasis.atum.base.infrastructure.service.HttpClient;
import lombok.val;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.text.SimpleDateFormat;

/**
 * 服务器配置
 */
@Configuration
public class ServerConfiguration
{
	@Bean
	public ObjectMapper objectMapper()
	{
		val mapper = new ObjectMapper();
		//格式化输出
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		//禁用日期时间戳
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		//不输出null,"",空字段
		mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		//日期格式化
		mapper.setDateFormat(new SimpleDateFormat(DateField.FULLDATE));

		return mapper;
	}

	@Bean
	public WebFluxConfigurer webFluxConfigurer(final ObjectMapper mapper)
	{
		return new WebFluxConfigurer()
		{
			@Override
			public void configureHttpMessageCodecs(final ServerCodecConfigurer configurer)
			{
				configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(mapper));
				configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(mapper));
			}
		};
	}

	/**
	 * 同步Http
	 * @return
	 */
	@Bean
	public RestTemplate restTemplate()
	{
		val jsonConvert = new FastJsonHttpMessageConverter4();
		// 初始化一个转换器配置
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteDateUseDateFormat, SerializerFeature.PrettyFormat);
		// 将配置设置给转换器并添加到HttpMessageConverter转换器列表中
		jsonConvert.setFastJsonConfig(fastJsonConfig);

		return new RestTemplateBuilder().additionalMessageConverters(jsonConvert).build();
	}

	@Bean
	public HttpClient httpClient()
	{
		return new HttpClient();
	}

//	/**
//	 * 异步调度线程池
//	 * @return
//	 */
//	@Bean
//	public Executor asyncExecutor()
//	{
//		val executor = new ThreadPoolTaskExecutor();
//		//核心数
//		val core = Runtime.getRuntime().availableProcessors();
//		executor.setCorePoolSize(core);
//		executor.setMaxPoolSize(core * 2);
//		executor.setQueueCapacity(1024);
//		executor.initialize();
//		return executor;
//	}
}
