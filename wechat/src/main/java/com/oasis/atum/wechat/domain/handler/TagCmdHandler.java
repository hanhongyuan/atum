package com.oasis.atum.wechat.domain.handler;

import com.oasis.atum.wechat.domain.cmd.TagCmd;
import com.oasis.atum.wechat.domain.entity.Tag;
import com.oasis.atum.wechat.domain.request.TagRequest;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Repository;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

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
		return Try.of(() -> repository.newInstance(() -> new Tag(cmd))
														.invoke(d ->
														{
															//转成微信请求格式数据
															val tag = new TagRequest.Create(d.getId());
															tag.setName(cmd.name);

															return tag;
														})
		).getOrElseThrow((Supplier<RuntimeException>) RuntimeException::new);
	}
}
