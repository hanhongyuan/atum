package com.oasis.atum.base.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.oasis.atum.base.infrastructure.util.BaseUtil;
import lombok.val;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.distributed.AnnotationRoutingStrategy;
import org.axonframework.commandhandling.distributed.CommandBusConnector;
import org.axonframework.commandhandling.distributed.CommandRouter;
import org.axonframework.commandhandling.distributed.DistributedCommandBus;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.mongo.eventsourcing.eventstore.DefaultMongoTemplate;
import org.axonframework.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.mongo.eventsourcing.eventstore.MongoTemplate;
import org.axonframework.mongo.eventsourcing.eventstore.documentperevent.DocumentPerEventStorageStrategy;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.axonframework.springcloud.commandhandling.SpringCloudCommandRouter;
import org.axonframework.springcloud.commandhandling.SpringHttpCommandBusConnector;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestOperations;

/**
 * Axon框架配置
 * 数据库 => MongoDB
 * 分布式命令分发 => SpringCloudCommand
 */
@Configuration
public class AxonConfiguration
{
	@Value("${spring.data.mongodb.host}")
	private String  mongoUri;
	@Value("${spring.data.mongodb.port}")
	private Integer port;
	@Value("${spring.data.mongodb.database}")
	private String  dbName;
	@Value("${spring.data.mongodb.username}")
	private String  username;
	@Value("${spring.data.mongodb.password}")
	private String  password;
	@Value("${spring.data.mongodb.events.collection.name}")
	private String  eventsCollectionName;
	@Value("${spring.data.mongodb.events.snapshot.collection.name}")
	private String  snapshotCollectionName;

	@Bean
	public JacksonSerializer axonJsonSerializer(final ObjectMapper mapper)
	{
		return new JacksonSerializer(mapper);
	}

	@Bean
	public EventStorageEngine eventStorageEngine(final Serializer serializer)
	{
		return new MongoEventStorageEngine(serializer, null, axonMongoTemplate(), new DocumentPerEventStorageStrategy());
	}

	@Bean(name = "axonMongoTemplate")
	public MongoTemplate axonMongoTemplate()
	{
		return new DefaultMongoTemplate(mongoClient(), dbName, eventsCollectionName, snapshotCollectionName);
	}

	@Bean
	public MongoClient mongoClient()
	{
		val uri = BaseUtil.getStringBuilder()
								.append("mongodb://")
								.append(username).append(":")
								.append(password).append("@")
								.append(mongoUri).append(":")
								.append(port).append("/")
								.append(dbName).toString();
		val clientUri = new MongoClientURI(uri);
		return new MongoClient(clientUri);
	}

	@Bean
	public CommandRouter springCloudCommandRouter(final DiscoveryClient discoveryClient)
	{
		return new SpringCloudCommandRouter(discoveryClient, new AnnotationRoutingStrategy());
	}

	@Bean
	public CommandBusConnector springHttpCommandBusConnector(@Qualifier("localSegment") final CommandBus localSegment,
																													 final RestOperations restOperations, final Serializer serializer)
	{
		return new SpringHttpCommandBusConnector(localSegment, restOperations, serializer);
	}

	@Bean
	@Primary
	public DistributedCommandBus springCloudDistributedCommandBus(final CommandRouter commandRouter, final CommandBusConnector commandBusConnector)
	{
		return new DistributedCommandBus(commandRouter, commandBusConnector);
	}
}
