package com.oasis.atum.wechat.domain.request;

import lombok.Builder;

import java.util.List;

/**
 * 菜单请求
 */
@Builder
public class MenuRequest
{
	public final List<Button> button;
	public final Matchrule    matchrule;
}
