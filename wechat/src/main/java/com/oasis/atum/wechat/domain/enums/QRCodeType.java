package com.oasis.atum.wechat.domain.enums;

/**
 * 二维码类型
 */
public enum QRCodeType
{
	QR_SCENE("临时的整型参数"),QR_STR_SCENE("临时的字符串参数"),
	QR_LIMIT_SCENE("永久的整型参数"), QR_LIMIT_STR_SCENE("永久的字符串参数");

	public final String name;

	QRCodeType(final String name)
	{
		this.name = name;
	}
}
