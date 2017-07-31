package com.oasis.atum.commons.interfaces.request;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.ToString;

/**
 * 阿里云短信回调
 * Created by ryze on 2017/7/10.
 */
@Builder
@ToString
public class SmsCallBack
{
	//消息编号
	public final String messageId;
	//模板code
	public final String templateCode;
	//短信条数
	public final String smsCount;
	//手机号
	public final String receiver;
	//发送状态（1成功，2失败）
	public final String state;
	//短信接收时间
	public final String receiveTime;
	//错误码
	public final String errCode;
	//触发的事件类型
	public final String event;
	//回复消息的人
	public final String sender;
	//回复消息的内容
	public final String content;
	//回复消息的时间
	//用户定义的扩展CODE
	public final String extendCode;
	//版本号
	public final String ver;
	//意义不明字段
	public final String extra;
	public final String bizId;

	@JSONCreator
	public SmsCallBack(@JSONField(name = "messageID") final String messageId, @JSONField(name = "template_code") final String templateCode,
										 @JSONField(name = "sms_count") final String smsCount, @JSONField(name = "receiver") final String receiver,
										 @JSONField(name = "state") final String state, @JSONField(name = "receive_time") final String receiveTime,
										 @JSONField(name = "err_code") final String errCode, @JSONField(name = "event") final String event,
										 @JSONField(name = "sender") final String sender, @JSONField(name = "content") final String content,
										 @JSONField(name = "extend_code") final String extendCode, @JSONField(name = "ver") final String ver,
										 @JSONField(name = "extra") final String extra, @JSONField(name = "biz_id") final String bizId)
	{
		this.messageId = messageId;
		this.templateCode = templateCode;
		this.smsCount = smsCount;
		this.receiver = receiver;
		this.state = state;
		this.receiveTime = receiveTime;
		this.errCode = errCode;
		this.event = event;
		this.sender = sender;
		this.content = content;
		this.extendCode = extendCode;
		this.ver = ver;
		this.extra = extra;
		this.bizId = bizId;
	}
}
