package com.oasis.atum.wechat.interfaces.api.v2;

import com.oasis.atum.base.infrastructure.util.Restful;
import com.oasis.atum.wechat.application.service.TagService;
import com.oasis.atum.wechat.interfaces.dto.TagDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 微信用户标签接口
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("v2/tags")
public class TagApi
{
	private final TagService service;

	@PostMapping
	public Mono<ResponseEntity> create(@RequestBody final Mono<TagDTO> data)
	{
		log.info("创建标签");
		return data.flatMap(service::create).map(v -> Restful.created());
	}
}
