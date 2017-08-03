package com.oasis.atum.monitor;

import de.codecentric.boot.admin.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 应用监控
 */
@EnableAdminServer
//@EnableTurbineStream
@EnableDiscoveryClient
@SpringBootApplication
public class MonitorApp
{
	public static void main(String[] args)
	{
		SpringApplication.run(MonitorApp.class, args);
	}

	/**
	 * 权限设置
	 */
	@Configuration
	public static class SecurityConfiguration extends WebSecurityConfigurerAdapter
	{
		@Override
		protected void configure(final HttpSecurity http) throws Exception
		{
			//登录
			http.formLogin().loginPage("/login.html").loginProcessingUrl("/login").permitAll().and()
					//注册
					.logout().logoutUrl("/logout").and()
					//关闭CSRF
					.csrf().disable()
					//登录页面以及静态资源不需要鉴权
					.authorizeRequests()
					.antMatchers("/login.html", "/**/*.css", "/img/**", "/third-party/**").permitAll()
					//其他需要鉴权
					.antMatchers("/**").authenticated().and()
					//基础鉴权
					.httpBasic();
		}
	}
}
