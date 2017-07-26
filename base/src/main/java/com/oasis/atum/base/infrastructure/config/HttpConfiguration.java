package com.oasis.atum.base.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.text.SimpleDateFormat;

/**
 * Http配置
 * Server配置 => Netty
 */
@Configuration
public class HttpConfiguration
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
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

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
				configurer.defaultCodecs().jackson2Decoder(new Jackson2JsonDecoder(mapper));
				configurer.defaultCodecs().jackson2Encoder(new Jackson2JsonEncoder(mapper));
			}
		};
	}
}
