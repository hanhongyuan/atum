package com.oasis.atum.commons.interfaces.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;

/**
 * 通话回调
 * Created by ryze on 2017/6/28.
 */
@Builder
@ToString
public class CallUpCallBack
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
