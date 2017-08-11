package com.oasis.atum.commons.infrastructure.config;

import com.oasis.atum.commons.domain.entity.CallUpRecord;
import com.oasis.atum.commons.domain.entity.SmsRecord;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 持久化配置
 */
@Configuration
@AllArgsConstructor
public class RepositoryConfiguration
{
	private final EventStore eventStore;

	@Bean
	public Repository<SmsRecord> smsRecordAggregateRepository()
	{
		return new EventSourcingRepository<>(SmsRecord.class, eventStore);
	}

	@Bean
	public Repository<CallUpRecord> callUpRecordAggregateRepository()
	{
		return new EventSourcingRepository<>(CallUpRecord.class, eventStore);
	}

}
