package com.oasis.atum.wechat.domain.entity;

import com.oasis.atum.base.infrastructure.util.IdWorker;
import com.oasis.atum.wechat.domain.cmd.TagCmd;
import com.oasis.atum.wechat.domain.event.TagEvent;
import com.oasis.atum.wechat.domain.request.TagRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static io.vavr.API.Option;
import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

/**
 * 用户标签聚合根
 */
@Data
@Slf4j
@Document
@Aggregate
@NoArgsConstructor
public class Tag
{
	@Id
	@AggregateIdentifier
	private String       id;
	//微信标签ID
	private Integer      wxId;
	private String       name;
	//粉丝数
	private Long         fans;
	//粉丝
	private List<String> openIds;
	@CreatedDate
	private Date         createTime;
	@LastModifiedDate
	private Date         updateTime;

	/**
	 * 创建标签
	 * @param cmd
	 */
	public Tag(final TagCmd.Create cmd)
	{
		val id = IdWorker.getFlowIdWorkerInstance().nextSID();
		//发布标签创建事件
		apply(TagEvent.Created.builder().id(id).name(cmd.name).createTime(cmd.createTime).build());
	}

	/**
	 * 标签添加粉丝
	 * @param cmd
	 */
	@CommandHandler
	public TagRequest.AddFans addFans(final TagCmd.AddFans cmd)
	{
		log.info("标签添加粉丝命令处理");
		//过滤掉已经是粉丝的
		val ids = cmd.openIds.stream().filter(s -> !openIds.contains(s)).collect(Collectors.toList());
		//不为空发起请求
		if (!ids.isEmpty())
		{
			//标签添加粉丝请求
			val request = TagRequest.AddFans.builder().openIds(cmd.openIds).wxId(wxId).build();
			//发布标签添加粉丝事件
			val event = TagEvent.AddedFans.builder().id(id).openIds(cmd.openIds).build();
			apply(event);
			return request;
		}
		return null;
	}

	/**
	 * 标签修改
	 * @param cmd
	 * @return
	 */
	@CommandHandler
	public TagRequest.Update update(final TagCmd.Update cmd)
	{
		log.info("标签修改粉丝命令处理");
		//标签修改请求
		val request = new TagRequest.Update();
		request.setId(wxId);
		request.setName(cmd.name);
		//发布标签修改事件
		apply(TagEvent.Updated.builder().id(id).wxId(cmd.wxId).name(cmd.name).build());
		return request;
	}

	@EventSourcingHandler
	public void handle(final TagEvent.Created event)
	{
		id = event.id;
		name = event.name;
		fans = 0L;
		openIds = new ArrayList<>();
		createTime = event.createTime;
	}

	@EventSourcingHandler
	public void handle(final TagEvent.AddedFans event)
	{
		val openIds = event.openIds;
		fans += openIds.size();
		this.openIds.addAll(openIds);
	}

	@EventSourcingHandler
	public void handle(final TagEvent.Updated event)
	{
		wxId = Option(event.wxId).getOrElse(wxId);
		name = Option(event.name).getOrElse(name);
	}
}
