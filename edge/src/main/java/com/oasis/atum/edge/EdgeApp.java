package com.oasis.atum.edge;

import lombok.val;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 网关
 */
@EnableZuulProxy
@SpringBootApplication
public class EdgeApp
{
	public static void main(final String[] args)
	{
		SpringApplication.run(EdgeApp.class, args);
	}

	/**
	 * 跨域设置 nginx里面配了这里就不用
	 * @return
	 */
	@Bean
	public FilterRegistrationBean corsFilter()
	{
		val source = new UrlBasedCorsConfigurationSource();

		val config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		config.addAllowedOrigin("*");
		source.registerCorsConfiguration("/**", config);
		val bean = new FilterRegistrationBean(new CorsFilter(source));
		bean.setOrder(0);
		return bean;
	}
}
