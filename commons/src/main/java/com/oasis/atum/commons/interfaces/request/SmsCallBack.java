package com.oasis.atum.commons.interfaces.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;

/**
 * 阿里云短信回调
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

	@JsonCreator
	public SmsCallBack(@JsonProperty("messageID") final String messageId, @JsonProperty("template_code") final String templateCode,
										 @JsonProperty("sms_count") final String smsCount, @JsonProperty("receiver") final String receiver,
										 @JsonProperty("state") final String state, @JsonProperty("receive_time") final String receiveTime,
										 @JsonProperty("err_code") final String errCode, @JsonProperty("event") final String event,
										 @JsonProperty("sender") final String sender, @JsonProperty("content") final String content,
										 @JsonProperty("extend_code") final String extendCode, @JsonProperty("ver") final String ver,
										 @JsonProperty("extra") final String extra, @JsonProperty("biz_id") final String bizId)
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
