package com.oasis.atum.wechat.application.service;

import com.alibaba.fastjson.JSONObject;
import com.oasis.atum.wechat.interfaces.dto.TagDTO;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 用户标签应用服务
 */
public interface TagService
{
	/**
	 * 创建标签
	 * @param dto
	 * @return
	 */
	Mono<Void> create(TagDTO dto);

	/**
	 * 给标签添加粉丝
	 * @param wxId
	 * @param openIds
	 * @return
	 */
	Mono<JSONObject> addFans(Integer wxId, List<String> openIds);

	/**
	 * 修改标签
	 * @param dto
	 * @return
	 */
	Mono<JSONObject> update(TagDTO dto);
}
