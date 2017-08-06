package com.oasis.atum.commons;

import com.alibaba.fastjson.JSONObject;
import com.oasis.atum.commons.domain.enums.SmsType;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

/**
 * 公用模块测试
 */
@Slf4j
//@SpringBootTest
//@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "wxtest.oasisapp.cn/commons", uriPort = 443, outputDir = "target/generated-docs/snippets")
public class CommonsAppTest
{
	//		@Autowired
	private MockMvc       mvc;
	private WebTestClient client;

	@Test
	public void captcha()
	{
		log.info("发送验证码测试");

		val json = new JSONObject();
		json.put("mobile", "18516966501");
		json.put("smsType", SmsType.login);

		client = WebTestClient.bindToServer()
							 .baseUrl("https://wxtest.oasisapp.cn/commons/v2/")
							 .build();
		client.post().uri("sms")
			.syncBody(json)
			.exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			.consumeWith(d ->
			{
				System.out.println("d =====> " + d);
				document("sms/captcha", requestFields(
					fieldWithPath("mobile").description("手机号(必传)"),
					fieldWithPath("smsType").description("短信类型(必传) login(登录) invitation(邀请) register(注册)")
				));
			});

	}


//	@Test
//	@SneakyThrows(Exception.class)
//	public void captcha()
//	{
//		log.info("发送验证码测试");
//
//		val json = new JSONObject();
//		json.put("mobile", "18516966501");
//		json.put("smsType", SmsType.login);
//
//		mvc.perform(post("/v2/sms")
//									.content(JSON.toJSONString(json))
//									.accept(MediaType.APPLICATION_JSON_UTF8)
//									.contentType(MediaType.APPLICATION_JSON_UTF8))
//			.andDo(document("sms/captcha", requestFields(
//				fieldWithPath("mobile").description("手机号(必传)"),
//				fieldWithPath("smsType").description("短信类型(必传) login(登录) invitation(邀请) register(注册)")
//			)));
//	}

//	@Test
//	@SneakyThrows(Exception.class)
//	public void validation()
//	{
//		log.info("校验验证码测试");
//
//		val json = new JSONObject();
//		json.put("mobile", "18516966501");
//		json.put("smsType", SmsType.login);
//		json.put("captcha", "1234");
//
//		mvc.perform(post("/v2/sms/validation")
//									.content(JSON.toJSONString(json))
//									.accept(MediaType.APPLICATION_JSON_UTF8)
//									.contentType(MediaType.APPLICATION_JSON_UTF8))
//			.andDo(document("sms/validation",
//				requestFields(
//					fieldWithPath("mobile").description("手机号(必传)"),
//					fieldWithPath("smsType").description("短信类型(必传) login(登录) invitation(邀请) register(注册)"),
//					fieldWithPath("captcha").description("验证码(必传)")
//				)));
//	}
//
//	@Test
//	@SneakyThrows(Exception.class)
//	public void binding()
//	{
//		log.info("临时绑定电话关系测试");
//
//		val json = new JSONObject();
//		json.put("call", "18516966501");
//		json.put("to", "18516966502");
//
//		mvc.perform(put("/v2/7moor/binding")
//									.content(JSON.toJSONString(json))
//									.accept(MediaType.APPLICATION_JSON_UTF8)
//									.contentType(MediaType.APPLICATION_JSON_UTF8))
//			.andDo(document("7moor/binding",
//				requestFields(
//					fieldWithPath("call").description("拨打方手机号(必传)"),
//					fieldWithPath("to").description("接收方手机号(必传)")
//				)));
//	}
//
//	@Test
//	@SneakyThrows(Exception.class)
//	public void unbinding()
//	{
//		log.info("解除绑定关系测试");
//
//		val json = new JSONObject();
//		json.put("call", "18516966501");
//
//		mvc.perform(delete("/v2/7moor/unbinding")
//									.content(JSON.toJSONString(json))
//									.accept(MediaType.APPLICATION_JSON_UTF8)
//									.contentType(MediaType.APPLICATION_JSON_UTF8))
//			.andDo(document("7moor/unbinding",
//				requestFields(
//					fieldWithPath("call").description("拨打方手机号(必传)")
//				)));
//	}
//
//	@Test
//	@SneakyThrows(Exception.class)
//	public void callUp()
//	{
//		log.info("打电话测试");
//
//		val json = new JSONObject();
//		json.put("callMobile", "18516966501");
//		json.put("callToMobile", "18516966502");
//		json.put("maxCallTime", 30);
//
//		mvc.perform(post("/v2/7moor/call-up")
//									.content(JSON.toJSONString(json))
//									.accept(MediaType.APPLICATION_JSON_UTF8)
//									.contentType(MediaType.APPLICATION_JSON_UTF8))
//			.andDo(document("7moor/callUp",
//				requestFields(
//					fieldWithPath("callMobile").description("拨打方手机号(必传)"),
//					fieldWithPath("callToMobile").description("接收方手机号(必传)"),
//					fieldWithPath("maxCallTime").description("通话时长(秒)").type(long.class)
//				)));
//	}
}
