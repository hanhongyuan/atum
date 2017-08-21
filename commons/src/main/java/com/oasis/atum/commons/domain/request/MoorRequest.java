package com.oasis.atum.commons.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * 容联七陌请求集
 */
public interface MoorRequest
{
	/**
	 * 打电话请求
	 */
	@Builder
	final class CallUp
	{
		public final String actionId;
		public final String exten;
		public final String variable;
		public final Long   maxCallTime;
	}

	/**
	 * 挂断请求
	 */
	@Builder
	final class HangUp
	{
		@JsonProperty("CallID")
		public final String callId;
		@JsonProperty("Agent")
		public final String agent;
		@JsonProperty("ActionID")
		public final String actionId;
	}

}
