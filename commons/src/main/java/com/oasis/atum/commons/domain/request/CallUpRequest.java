package com.oasis.atum.commons.domain.request;

import lombok.Builder;

/**
 * 打电话请求
 */
@Builder
public class CallUpRequest
{
	public final String actionId;
	public final String exten;
	public final String variable;
	public final Long   maxCallTime;
}
