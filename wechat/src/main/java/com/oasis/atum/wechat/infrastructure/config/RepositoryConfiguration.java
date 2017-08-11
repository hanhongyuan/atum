package com.oasis.atum.wechat.infrastructure.config;

import com.oasis.atum.wechat.domain.entity.Menu;
import com.oasis.atum.wechat.domain.entity.QRCode;
import com.oasis.atum.wechat.domain.entity.Tag;
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
	public Repository<QRCode> qrCodeAggregateRepository()
	{
		return new EventSourcingRepository<>(QRCode.class, eventStore);
	}

	@Bean
	public Repository<Menu> menuAggregateRepository()
	{
		return new EventSourcingRepository<>(Menu.class, eventStore);
	}

	@Bean
	public Repository<Tag> tagAggregateRepository()
	{
		return new EventSourcingRepository<>(Tag.class, eventStore);
	}
}
