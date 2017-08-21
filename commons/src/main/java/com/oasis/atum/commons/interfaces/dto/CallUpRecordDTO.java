package com.oasis.atum.commons.interfaces.dto;

import com.oasis.atum.commons.domain.enums.CallEventState;
import com.oasis.atum.commons.domain.enums.CallState;
import com.oasis.atum.commons.domain.enums.CallType;
import lombok.Builder;
import lombok.ToString;

import java.util.Date;

/**
 * 通话记录DTO
 */
@Builder
@ToString
public class CallUpRecordDTO
{
	public final String         id;
	public final String         callMobile;
	public final String         callToMobile;
	public final Long           maxCallTime;
	public final Long           callTime;
	public final CallType       callType;
	public final Date           ringTime;
	public final Date           beginTime;
	public final Date           endTime;
	//接听状态
	public final CallState      callState;
	//事件状态
	public final CallEventState state;
	public final String         thirdId;

}
