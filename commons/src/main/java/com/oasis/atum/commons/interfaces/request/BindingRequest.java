package com.oasis.atum.commons.interfaces.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;

/**
 * 绑定请求
 * Created by ryze on 2017/6/28.
 */
@Builder
@ToString
public class BindingRequest
{
	public final String call;
	public final String to;

	@JsonCreator
	public BindingRequest(@JsonProperty("call") final String call, @JsonProperty("to") final String to)
	{
		this.call = call;
		this.to = to;
	}
}
