package com.oasis.atum.wechat.domain.entity;

import com.oasis.atum.base.infrastructure.util.IdWorker;
import com.oasis.atum.wechat.domain.cmd.MenuCmd;
import com.oasis.atum.wechat.infrastructure.enums.MenuType;
import com.oasis.atum.wechat.domain.event.MenuEvent;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateLifecycle;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

import static io.vavr.API.Option;
import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

/**
 * 菜单聚合根
 */
@Data
@Slf4j
@Document
@Aggregate
@NoArgsConstructor
public class Menu
{
	@Id
	@AggregateIdentifier
	private String   id;
	//菜单名称
	private String   name;
	//菜单类型
	private MenuType type;
	//菜单键值 Key类型必传
	private String   menuKey;
	//菜单uri View类型必传
	private String   uri;
	//多媒体ID
	private String   mediaId;
	//父菜单ID
	private String   parentId;
	//回复内容
	private String   content;
	//回复类型
	private String   contentType;
	//排序
	private Integer  sort;
	private String   comment;
	private boolean  isShow;
	private Date     createTime;
	@LastModifiedDate
	private Date     updateTime;

	/**
	 * 菜单创建
	 * @param cmd
	 */
	@CommandHandler
	public Menu(final MenuCmd.Create cmd)
	{
		log.info("菜单创建命令处理");
		val id = IdWorker.getFlowIdWorkerInstance().nextSID();
		//发布菜单创建事件
		apply(MenuEvent.Created.builder().id(id).cmd(cmd).build());
	}

	/**
	 * 菜单删除
	 */
	@CommandHandler
	public void delete(final MenuCmd.Delete cmd)
	{
		log.info("菜单删除命令处理");
		//发布菜单删除事件
		apply(new MenuEvent.Deleted(cmd.id));
	}

	/**
	 * 菜单修改
	 * @param cmd
	 */
	@CommandHandler
	public void update(final MenuCmd.Update cmd)
	{
		log.info("菜单修改命令处理");
		//发布菜单修改事件
		apply(MenuEvent.Updated.builder().id(id).cmd(cmd).build());
	}

	@EventSourcingHandler
	private void handle(final MenuEvent.Created event)
	{
		id = event.id;
		name = event.cmd.name;
		type = event.cmd.type;
		menuKey = event.cmd.menuKey;
		uri = event.cmd.uri;
		mediaId = event.cmd.mediaId;
		parentId = event.cmd.parentId;
		content = event.cmd.content;
		contentType = event.cmd.contentType;
		sort = event.cmd.sort;
		comment = event.cmd.comment;
		isShow = event.cmd.isShow;
		createTime = event.cmd.createTime;
	}

	@EventSourcingHandler
	private void handle(final MenuEvent.Updated event)
	{
		name = Option(event.cmd.name).getOrElse(name);
		type = Option(event.cmd.type).getOrElse(type);
		menuKey = Option(event.cmd.menuKey).getOrElse(menuKey);
		uri = Option(event.cmd.uri).getOrElse(uri);
		mediaId = Option(event.cmd.mediaId).getOrElse(mediaId);
		parentId = Option(event.cmd.parentId).getOrElse(parentId);
		content = Option(event.cmd.content).getOrElse(content);
		contentType = Option(event.cmd.contentType).getOrElse(contentType);
		sort = Option(event.cmd.sort).getOrElse(sort);
		isShow = Option(event.cmd.isShow).getOrElse(isShow);
		comment = Option(event.cmd.comment).getOrElse(comment);
	}

	@EventSourcingHandler
	private void delete(final MenuEvent.Deleted event)
	{
		AggregateLifecycle.markDeleted();
	}

}
