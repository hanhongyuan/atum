package com.oasis.atum.wechat.interfaces.assembler;

import com.oasis.atum.wechat.domain.entity.QRCode;
import com.oasis.atum.wechat.interfaces.dto.QRCodeDTO;

/**
 * 二维码聚合根DTO转换器
 */
public interface QRCodeAssembler
{
	static QRCodeDTO toDTO(final QRCode data)
	{
		return QRCodeDTO.builder().id(data.getId()).type(data.getType()).sceneId(data.getSceneId())
							 .sceneStr(data.getSceneStr()).expireSeconds(data.getExpireSeconds()).ticket(data.getTicket())
							 .uri(data.getUri()).build();
	}
}
