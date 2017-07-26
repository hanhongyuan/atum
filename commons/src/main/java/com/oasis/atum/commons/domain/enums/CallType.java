package com.oasis.atum.commons.domain.enums;

/**
 * 通话类型
 */
public enum CallType
{
	dialout("外呼通话"), normal("普通来电"), transfer("转接电话"), dialTransfer("外呼转接");

	public final String name;

	CallType(final String name)
	{
		this.name = name;
	}
}
