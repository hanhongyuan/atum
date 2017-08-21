package com.oasis.atum.commons.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;

/**
 * 容联七陌DTO集
 */
public interface MoorDTO
{
	/**
	 * 挂断DTO
	 */
	@Builder
	@ToString
	final class HangUp
	{
		public final String callId;
		public final String actionId;

		@JsonCreator
		public HangUp(@JsonProperty("callId") String callId, @JsonProperty("actionId") String actionId)
		{
			this.callId = callId;
			this.actionId = actionId;
		}
	}

	/**
	 * 绑定DTO
	 */
	@Builder
	@ToString
	final class Binding
	{
		public final String thirdId;
		public final String call;
		public final String to;
		public final Long   maxCallTime;
		public final String noticeUri;

		@JsonCreator
		public Binding(@JsonProperty("thirdId") final String thirdId, @JsonProperty("call") final String call,
									 @JsonProperty("to") final String to, @JsonProperty("maxCallTime") final Long maxCallTime,
									 @JsonProperty("noticeUri") final String noticeUri)
		{
			this.thirdId = thirdId;
			this.call = call;
			this.to = to;
			this.maxCallTime = maxCallTime;
			this.noticeUri = noticeUri;
		}
	}

	/**
	 * 通话回调
	 */
	@Builder
	@ToString
	final class CallUpCallBack
	{
		public final String  command;
		public final String  response;
		public final String  actionId;
		public final boolean isSuccess;
		public final String  message;

		@JsonCreator
		public CallUpCallBack(@JsonProperty("Command") final String command, @JsonProperty("Response") final String response,
													@JsonProperty("ActionID") final String actionId, @JsonProperty("Succeed") final boolean isSuccess,
													@JsonProperty("Message") final String message)
		{
			this.command = command;
			this.response = response;
			this.actionId = actionId;
			this.isSuccess = isSuccess;
			this.message = message;
		}
	}


}
