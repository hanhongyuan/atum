package com.oasis.atum.commons.interfaces.assembler;

import com.oasis.atum.commons.domain.entity.CallUpRecord;
import com.oasis.atum.commons.interfaces.dto.CallUpRecordDTO;

/**
 * 通话记录DTO转换
 */
public interface CallUpRecordAssembler
{
	static CallUpRecordDTO toDTO(final CallUpRecord data)
	{
		return CallUpRecordDTO.builder()
						 .id(data.getId())
						 .callMobile(data.getCallMobile())
						 .callToMobile(data.getCallToMobile())
						 .maxCallTime(data.getMaxCallTime())
						 .callTime(data.getCallTime())
						 .callType(data.getCallType())
						 .ringTime(data.getRingTime())
						 .beginTime(data.getBeginTime())
						 .endTime(data.getEndTime())
						 .callState(data.getCallState())
						 .build();
	}
}
