package com.oasis.atum.commons.domain.enums;

/**
 * 短信类型
 */
public enum SmsType
{
	register("注册"), login("登录"), invitation("邀请");

	public final String name;

	SmsType(final String name)
	{
		this.name = name;
	}
}
