package com.oasis.atum.wechat.interfaces.response;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;

import java.util.List;

/**
 * 微信响应消息（普通用户 => 公众帐号）
 * Created by ryze on 2017/7/4.
 */
public interface WechatResponse
{
	/**
	 * 文本消息
	 */
	@Builder
	final class Text implements WechatResponse
	{
		// 接收方帐号（收到的OpenID）
		public final String ToUserName;
		// 开发者微信号
		public final String FromUserName;
		// 消息创建时间 （整型）
		public final String CreateTime;
		// 消息类型
		public final String MsgType;
		// 文本
		public final String Content;
	}

	/**
	 * 图文消息
	 */
	@Builder
	final class News implements WechatResponse
	{
		// 接收方帐号（收到的OpenID）
		public final String            ToUserName;
		// 开发者微信号
		public final String            FromUserName;
		// 消息创建时间 （整型）
		public final String            CreateTime;
		// 消息类型
		public final String            MsgType;
		// 图文消息个数，限制为8条以内
		public final int               ArticleCount;
		// 多条图文消息信息，默认第一个item为大图
		public final List<NewsArticle> Articles;
	}

	/**
	 * 图文消息图文
	 */
	@Builder
	final class NewsArticle
	{
		// 图文消息名称
		public final String Title;
		// 图文消息描述
		public final String Description;
		// 图片链接，支持JPG、PNG格式，较好的效果为大图640*320，小图80*80，限制图片链接的域名需要与开发者填写的基本资料中的Url一致
		public final String PicUrl;
		// 点击图文消息跳转链接
		public final String Url;
	}

	/**
	 * 图文消息素材
	 */
	@Builder
	final class NewsItem
	{
		public final String title;
		public final String author;
		public final String digest;
		public final String content;
		@JSONField(name = "content_source_url")
		public final String contentSourceUri;
		@JSONField(name = "thumb_media_id")
		public final String thumbMediaId;
		@JSONField(name = "thumb_url")
		public final String thumbUri;
		@JSONField(name = "url")
		public final String uri;

//		@JSONCreator
//		public NewsItem(@JSONField(name = "title") final String title, @JSONField(name = "author") final String author,
//										@JSONField(name = "digest") final String digest, @JSONField(name = "content") final String content,
//										@JSONField(name = "content_source_url") final String contentSourceUri,
//										@JSONField(name = "thumb_media_id") final String thumbMediaId,
//										@JSONField(name = "thumb_url") final String thumbUri, @JSONField(name = "url") final String uri)
//		{
//			this.title = title;
//			this.author = author;
//			this.digest = digest;
//			this.content = content;
//			this.contentSourceUri = contentSourceUri;
//			this.thumbMediaId = thumbMediaId;
//			this.thumbUri = thumbUri;
//			this.uri = uri;
//		}
	}
}
