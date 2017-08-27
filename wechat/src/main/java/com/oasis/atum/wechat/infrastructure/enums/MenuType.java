package com.oasis.atum.wechat.infrastructure.enums;

/**
 * 菜单类型
 */
public enum MenuType
{
	click("CLICK"), view("VIEW");

	public final String name;

	MenuType(final String name)
	{
		this.name = name;
	}
}
