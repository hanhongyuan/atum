//package com.oasis.atum.wechat;
//
//import com.alibaba.fastjson.JSONObject;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import lombok.val;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
//import static org.springframework.restdocs.payload.PayloadDocumentation.*;
//import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
//import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//
///**
// * 微信模块测试
// */
//@Slf4j
//@SpringBootTest
//@AutoConfigureMockMvc
//@RunWith(SpringRunner.class)
//@AutoConfigureRestDocs(uriScheme = "https", uriHost = "wxtest.oasisapp.cn/wechat", uriPort = 443, outputDir = "target/generated-docs/snippets")
//public class WechatAppTest
//{
//	@Autowired
//	private MockMvc mvc;
//
//	@Test
//	@SneakyThrows(Exception.class)
//	public void getSignature()
//	{
//		log.info("获取Js-SDK签名测试");
//
//		mvc.perform(get("/v2/js-sdk")
//									.param("uri", "https://mtest.oasiscare.cn/wxofficial/aboutour/doctorclinic.html?type=yygh")
//									.accept(MediaType.APPLICATION_JSON_UTF8)
//									.contentType(MediaType.APPLICATION_JSON_UTF8))
//			.andDo(document("js-sdk",
//				requestParameters(
//					parameterWithName("uri").description("当前网页的uri,不包含#及其后面部分(必传)")
//				),
//				relaxedResponseFields(
//					subsectionWithPath("appId").description("appId")
////					fieldWithPath("appId").description("微信账号应用ID"),
////					fieldWithPath("timeStamp").description("时间戳"),
////					fieldWithPath("nonceStr").description("随机字符串"),
////					fieldWithPath("signature").description("js-sdk签名")
//				)
//			));
//	}
//
//	@Test
//	@SneakyThrows(Exception.class)
//	public void openId()
//	{
//		log.info("OpenId测试");
//
//		//创建
//		mvc.perform(get("/v2/oauth2/openId")
//									.param("code", "123123")
//									.accept(MediaType.APPLICATION_JSON_UTF8)
//									.contentType(MediaType.APPLICATION_JSON_UTF8))
//			.andDo(document("oauth2",
//				requestParameters(
//					parameterWithName("code").description("从微信获取的Oauth2授权码(必传)")
//				),
//				relaxedResponseFields(
//					fieldWithPath("openid").description("用户OpenId")
//				)
//			));
//
//	}
//}
