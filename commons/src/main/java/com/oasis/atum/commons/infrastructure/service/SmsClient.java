package com.oasis.atum.commons.infrastructure.service;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.BatchSmsAttributes;
import com.aliyun.mns.model.MessageAttributes;
import com.aliyun.mns.model.RawTopicMessage;
import com.oasis.atum.commons.infrastructure.config.SmsConfiguration;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


/**
 * 阿里云短信基础服务
 */
@Slf4j
@Component
@AllArgsConstructor
public class SmsClient
{
	private final SmsConfiguration                       config;
	private final SmsConfiguration.TemplateConfiguration templateConfig;

	/**
	 * 发送验证码
	 * @param code
	 * @param mobiles
	 * @return
	 */
	public Mono<String> sendCaptcha(final String code, final List<String> mobiles)
	{
//		val json = new JSONObject();
//		json.put("code", code);
//		json.put("product", config.getSign());

		val map = HashMap.of("code", code, "product", config.getSign());

		return sendSms(templateConfig.getCaptcha(), map, mobiles);
	}

	private Mono<String> sendSms(final String templateId, final HashMap<String, String> map, final List<String> mobiles)
	{
		try
		{
			/**
			 * Step 1. 获取主题引用
			 */
			val account = new CloudAccount(config.getAccessKey(), config.getAccessSecret(), config.getEndPoint());
			@Cleanup
			val client = account.getMNSClient();
			val topic = client.getTopicRef(config.getTopic());
			/**
			 * Step 2. 设置SMS消息体（必须）
			 *
			 * 注：目前暂时不支持消息内容为空，需要指定消息内容，不为空即可。
			 */
			val msg = new RawTopicMessage();
			msg.setMessageBody("sms-message");
			/**
			 * Step 3. 生成SMS消息属性
			 */
			val messageAttributes  = new MessageAttributes();
			val batchSmsAttributes = new BatchSmsAttributes();
			// 3.1 设置发送短信的签名（SMSSignName）
			batchSmsAttributes.setFreeSignName(config.getSign());
			// 3.2 设置发送短信使用的模板（SMSTempateCode）
			batchSmsAttributes.setTemplateCode(templateId);
			// 3.3 设置发送短信所使用的模板中参数对应的值（在短信模板中定义的，没有可以不用设置）
			val smsReceiverParams = new BatchSmsAttributes.SmsReceiverParams();
			map.forEach(smsReceiverParams::setParam);
			// 3.4 增加接收短信的号码
			mobiles.forEach(s -> batchSmsAttributes.addSmsReceiver(s, smsReceiverParams));
			messageAttributes.setBatchSmsAttributes(batchSmsAttributes);
			/**
			 * Step 4. 发布SMS消息
			 */
			val ret = topic.publishMessage(msg, messageAttributes);
			return Mono.just(ret.getMessageId());
		}
		catch (ServiceException se)
		{
			log.error(se.getErrorCode() + se.getRequestId());
			log.error(se.getMessage());
			return Mono.empty();
		}
	}
}
