package com.oasis.atum.wechat.interfaces.api.v2;


import com.alibaba.fastjson.JSONObject;
import com.oasis.atum.base.infrastructure.util.Restful;
import com.oasis.atum.wechat.infrastructure.service.WechatClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * 网页OAuth2授权接口
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("v2/oauth2")
public class OAuth2Api
{
	private final WechatClient client;

	@GetMapping("openId")
	public Mono<ResponseEntity> openId(@RequestParam final String code)
	{
		log.info("获取用户openId =====> {}", code);

		//第二步：通过code换取网页授权access_token
		return client.oauth2(code)
							 .map(j -> Optional.ofNullable(j.getString("openid"))
														 .map(s ->
														 {
															 val json = new JSONObject();
															 json.put("openid", s);
															 return Restful.ok(json);
														 })
														 .orElseGet(Restful::ok)
							 );
	}
}
