package com.oasis.atum.wechat.infrastructure.config;

import com.oasis.atum.wechat.domain.entity.Menu;
import com.oasis.atum.wechat.domain.entity.QRCode;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 持久化配置
 */
@Configuration
public class RepositoryConfiguration
{
	private final EventStore eventStore;

	public RepositoryConfiguration(final EventStore eventStore)
	{
		this.eventStore = eventStore;
	}

	@Bean
	public Repository<QRCode> qrCodeAggregateRepository()
	{
		return new EventSourcingRepository<>(QRCode.class, eventStore);
	}

	@Bean
	public Repository<Menu> menuAggregateRepository()
	{
		return new EventSourcingRepository<>(Menu.class, eventStore);
	}
}
