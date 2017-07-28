package com.oasis.atum.wechat.interfaces.request;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;

/**
 * 微信回调请求
 * Created by ryze on 2017/6/13.
 */
@Builder
public class WechatRequest
{
	// 公众号
	public final String ToUserName;
	// 用户OpenId
	public final String FromUserName;
	// 消息创建时间 （整型）
	public final String CreateTime;
	// 消息类型
	public final String MsgType;
	// 消息id，64位整型
	public final String MsgId;
	/*******文本消息*******/
	// 文本消息内容
	public final String Content;
	/*******图片消息*******/
	// 图片链接（由系统生成）
	public final String PicUrl;
	// 图片消息媒体id，可以调用多媒体文件下载接口拉取数据。
	public final String MediaId;
	/*******语音消息*******/
	// 语音格式，如amr，speex等
	public final String Format;
	// 语音识别结果，UTF8编码
	public final String Recognition;
	/*******(小)视频消息*******/
	// 视频消息缩略图的媒体id，可以调用多媒体文件下载接口拉取数据。
	public final String ThumbMediaId;
	/*******地理位置消息*******/
	// 地理位置纬度
	public final String Location_X;
	// 地理位置经度
	public final String Location_Y;
	// 地图缩放大小
	public final String Scale;
	// 地理位置信息
	public final String Label;
	// 消息标题
	public final String Title;
	// 消息描述
	public final String Description;
	// 消息链接
	public final String Url;
	/*******接收事件推送*******/
	// 事件类型
	public final String Event;
	// 事件KEY值
	public final String EventKey;
	// 二维码的ticket，可用来换取二维码图片
	public final String Ticket;
	// 纬度
	public final String Latitude;
	// 经度
	public final String Longitude;
	// 精度
	public final String Precision;

	@JSONCreator
	public WechatRequest(@JSONField(name = "toUserName") final String toUserName, @JSONField(name = "fromUserName") final String fromUserName,
											 @JSONField(name = "createTime") final String createTime, @JSONField(name = "msgType") final String msgType,
											 @JSONField(name = "msgId") final String msgId, @JSONField(name = "content") final String content,
											 @JSONField(name = "picUrl") final String picUrl, @JSONField(name = "mediaId") final String mediaId,
											 @JSONField(name = "format") final String format, @JSONField(name = "recognition") final String recognition,
											 @JSONField(name = "ThumbMediaId") final String thumbMediaId, @JSONField(name = "location_X") final String location_X,
											 @JSONField(name = "location_Y") final String location_Y, @JSONField(name = "scale") final String scale,
											 @JSONField(name = "label") final String label, @JSONField(name = "title") final String title,
											 @JSONField(name = "description") final String description, @JSONField(name = "url") final String url,
											 @JSONField(name = "event") final String event, @JSONField(name = "eventKey") final String eventKey,
											 @JSONField(name = "ticket") final String ticket, @JSONField(name = "latitude") final String latitude,
											 @JSONField(name = "longitude") final String longitude, @JSONField(name = "precision") final String precision)
	{
		ToUserName = toUserName;
		FromUserName = fromUserName;
		CreateTime = createTime;
		MsgType = msgType;
		MsgId = msgId;
		Content = content;
		PicUrl = picUrl;
		MediaId = mediaId;
		Format = format;
		Recognition = recognition;
		ThumbMediaId = thumbMediaId;
		Location_X = location_X;
		Location_Y = location_Y;
		Scale = scale;
		Label = label;
		Title = title;
		Description = description;
		Url = url;
		Event = event;
		EventKey = eventKey;
		Ticket = ticket;
		Latitude = latitude;
		Longitude = longitude;
		Precision = precision;
	}
}
