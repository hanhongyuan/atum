package com.oasis.atum.wechat.interfaces.api.v2;

import com.oasis.atum.base.infrastructure.constant.RequestField;
import com.oasis.atum.base.infrastructure.util.Restful;
import com.oasis.atum.wechat.application.service.QRCodeService;
import com.oasis.atum.wechat.interfaces.dto.QRCodeDTO;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 二维码接口
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("v2/qrcodes")
public class QRCodeApi
{
	private final QRCodeService service;

	@GetMapping(params = {RequestField.PAGE, RequestField.SIZE})
	public Mono<ResponseEntity> list(@RequestParam final Integer page, @RequestParam final Integer size)
	{
		log.info("二维码列表");
		val pageable = PageRequest.of(page, size);
		return service.getQRCodes(pageable)
						 .collectList()
						 .map(Restful::ok);
	}

	@PostMapping
	public Mono<ResponseEntity> create(@RequestBody final Mono<QRCodeDTO> data)
	{
		log.info("创建二维码");
		return data.flatMap(service::create).map(Restful::ok);
	}

	@GetMapping(RequestField.PK)
	public Mono<ResponseEntity> show(@PathVariable final String id)
	{
		log.info("查询二维码");
		return service.getQRCodeById(id).map(Restful::ok);
	}
}
