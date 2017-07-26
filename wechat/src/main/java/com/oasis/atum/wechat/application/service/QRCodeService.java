package com.oasis.atum.wechat.application.service;

import com.oasis.atum.wechat.interfaces.dto.QRCodeDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 二维码应用服务
 */
public interface QRCodeService
{
	/**
	 * 主键查询
	 * @param id
	 * @return
	 */
	Mono<QRCodeDTO> getQRCodeById(String id);

	/**
	 * 创建二维码
	 * @param dto
	 */
	Mono<Void> create(QRCodeDTO dto);

	/**
	 * 二维码列表
	 * @param pageable
	 * @return
	 */
	Flux<QRCodeDTO> getQRCodes(Pageable pageable);
}
