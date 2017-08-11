package com.oasis.atum.wechat.domain.handler;

import com.oasis.atum.wechat.domain.cmd.TagCmd;
import com.oasis.atum.wechat.domain.entity.Tag;
import com.oasis.atum.wechat.domain.request.TagRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Repository;
import org.springframework.stereotype.Component;

/**
 * 标签命令处理
 */
@Slf4j
@Component
@AllArgsConstructor
public class TagCmdHandler
{
	private final Repository<Tag> repository;

	@CommandHandler
	public TagRequest.Create handle(final TagCmd.Create cmd)
	{
		log.info("标签创建命令处理");
		try
		{
			return repository.newInstance(() -> new Tag(cmd)).invoke(t ->
			{
				//转成微信请求格式数据
				val tag = new TagRequest.Create(t.getId());
				tag.setName(cmd.name);

				return tag;
			});
		}
		catch (Exception e)
		{
			log.error("创建标签出错", e);
			throw new IllegalArgumentException("创建标签出错");
		}
	}
}
