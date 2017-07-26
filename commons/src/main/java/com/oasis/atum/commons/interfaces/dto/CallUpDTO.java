package com.oasis.atum.commons.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;

/**
 * 打电话DTO
 */
@Builder
@ToString
public class CallUpDTO
{
	public final String callMobile;
	public final String callToMobile;
	public final Long   maxCallTime;

	@JsonCreator
	public CallUpDTO(@JsonProperty("callMobile") final String callMobile, @JsonProperty("callToMobile") final String callToMobile,
									 @JsonProperty("maxCallTime") final Long maxCallTime)
	{
		this.callMobile = callMobile;
		this.callToMobile = callToMobile;
		this.maxCallTime = maxCallTime;
	}
}
