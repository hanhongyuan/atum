package com.oasis.atum.commons.domain.handler;

import com.oasis.atum.commons.domain.cmd.CallUpRecordCmd;
import com.oasis.atum.commons.domain.entity.CallUpRecord;
import com.oasis.atum.commons.domain.request.CallUpRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Repository;
import org.springframework.stereotype.Component;

/**
 * 通话记录命令处理
 */
@Slf4j
@Component
@AllArgsConstructor
public class CallUpRecordCmdHandler
{
	private final Repository<CallUpRecord> repository;

	@CommandHandler
	public CallUpRequest handle(final CallUpRecordCmd.Create cmd)
	{
		log.info("通话记录创建命令处理");
		try
		{
			return repository.newInstance(() -> new CallUpRecord(cmd))
							 .invoke(data -> CallUpRequest.builder()
																 .actionId(data.getId())
																 .exten(data.getCallMobile())
																 .variable(data.getCallToMobile())
																 .maxCallTime(data.getMaxCallTime())
																 .build());
		}
		catch (Exception e)
		{
			log.error("通话记录创建出错", e);
			return null;
		}
	}

	@CommandHandler
	public CallUpRecord handle(final CallUpRecordCmd.Update cmd)
	{
		log.info("通话记录修改命令处理");
		return repository.load(cmd.id).invoke(d -> d.update(cmd));
	}
}
