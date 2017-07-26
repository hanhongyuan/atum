package com.oasis.atum.wechat.infrastructure.repository;

import com.oasis.atum.wechat.domain.entity.QRCode;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * 二维码持久化
 */
public interface QRCodeRepository extends ReactiveMongoRepository<QRCode,String>
{
}
