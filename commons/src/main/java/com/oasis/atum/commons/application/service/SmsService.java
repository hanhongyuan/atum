package com.oasis.atum.commons.application.service;

import com.oasis.atum.commons.interfaces.dto.SmsDTO;
import com.oasis.atum.commons.interfaces.request.SmsCallBack;

/**
 * 短信应用服务
 */
public interface SmsService
{
	/**
	 * 发送验证码
	 * @param data
	 */
	void sendCaptcha(SmsDTO data);

	/**
	 * 阿里云短信成功回调
	 * @param data
	 */
	void success(SmsCallBack data);

	/**
	 * 阿里云短信失败回调
	 * @param data
	 */
	void fail(SmsCallBack data);

	/**
	 * 阿里云短信回复回调
	 * @param data
	 */
	void reply(SmsCallBack data);
}
