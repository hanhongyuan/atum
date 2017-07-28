package com.oasis.atum.wechat.domain.enums;

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
