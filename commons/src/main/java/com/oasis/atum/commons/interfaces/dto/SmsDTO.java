package com.oasis.atum.commons.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oasis.atum.commons.domain.enums.SmsType;
import lombok.Builder;
import lombok.ToString;

/**
 * 短信记录DTO
 * Created by ryze on 2017/7/7.
 */
@Builder
@ToString
public class SmsDTO
{
	public final String   mobile;
	public final SmsType  smsType;
	public final String captcha;

	@JsonCreator
	public SmsDTO(@JsonProperty("mobile") final String mobile, @JsonProperty("smsType") final SmsType smsType,
								@JsonProperty("captcha") final String captcha)
	{
		this.mobile = mobile;
		this.smsType = smsType;
		this.captcha = captcha;
	}
}
