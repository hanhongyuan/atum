package com.oasis.atum.wechat.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oasis.atum.wechat.domain.enums.MenuType;
import lombok.Builder;
import lombok.ToString;

/**
 * 菜单DTO
 */
@Builder
@ToString
public class MenuDTO
{
	public final String   id;
	public final String   name;
	public final MenuType type;
	public final String   menuKey;
	public final String   uri;
	public final String   mediaId;
	public final String   parentId;
	public final String   content;
	public final String   contentType;
	public final Boolean  isShow;
	public final Integer  sort;
	public final String   comment;

	@JsonCreator
	public MenuDTO(@JsonProperty("id") final String id, @JsonProperty("name") final String name,
								 @JsonProperty("type") final MenuType type, @JsonProperty("menuKey") final String menuKey,
								 @JsonProperty("uri") final String uri, @JsonProperty("mediaId") final String mediaId,
								 @JsonProperty("parentId") final String parentId, @JsonProperty("content") final String content,
								 @JsonProperty("contentType") final String contentType, @JsonProperty("isShow") final Boolean isShow,
								 @JsonProperty("sort") final Integer sort, @JsonProperty("comment") final String comment)
	{
		this.id = id;
		this.name = name;
		this.type = type;
		this.menuKey = menuKey;
		this.uri = uri;
		this.mediaId = mediaId;
		this.parentId = parentId;
		this.content = content;
		this.contentType = contentType;
		this.isShow = isShow;
		this.sort = sort;
		this.comment = comment;
	}
}
