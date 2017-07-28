package com.oasis.atum.wechat.infrastructure.util;

import com.alibaba.fastjson.JSON;
import com.oasis.atum.wechat.interfaces.request.WechatRequest;
import com.oasis.atum.wechat.interfaces.response.WechatResponse;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 微信XML生成工具
 * Created by ryze on 2017/4/29.
 */
@Slf4j
public class XMLUtil
{
	private XMLUtil() {}

	public static String toXML(final Object object)
	{
		val xStream = CommonUtil.getXStream();
		xStream.alias("xml", object.getClass());
		xStream.registerConverter(new MapEntryConverter());
		xStream.alias("item", WechatResponse.NewsArticle.class);
		return CommonUtil.getXStream().toXML(object);
	}

	public static <T> T parseXML(final String xml, final Class<T> clazz)
	{
		if (Objects.isNull(xml)) return null;

		val xStream = CommonUtil.getXStream();
		xStream.alias("xml", clazz);
		return (T) xStream.fromXML(xml);
	}

	/**
	 * 解析微信服务器请求XML
	 * @param request 微信请求
	 * @return Map数据
	 */
	public static Mono<WechatRequest> parseXML(final ServerHttpRequest request)
	{
		return request.getBody()
						 //转流
						 .map(DataBuffer::asInputStream)
						 //SAX解析
						 .map(is ->
						 {
							 try
							 {
								 val document = new SAXReader().read(is);
								 return document.getRootElement().elements().stream();
							 }
							 catch (Exception e)
							 {
								 return null;
							 }
						 })
						 //转Map
						 .map(st -> (Map<String, String>) st.collect(Collectors.toMap(Element::getName, Element::getText)))
						 //转JSONString
						 .map(JSON::toJSONString)
						 //转对象
						 .map(s -> JSON.parseObject(s, WechatRequest.class))
						 .elementAt(0);
	}

	/**
	 * Map解析转换
	 */
	static class MapEntryConverter implements Converter
	{
		public boolean canConvert(Class clazz)
		{
			return Map.class.isAssignableFrom(clazz);
		}

		public void marshal(final Object value, final HierarchicalStreamWriter writer, final MarshallingContext context)
		{
			val map = (Map) value;
			map.entrySet().forEach(o ->
			{
				Map.Entry<String, String> entry = (Map.Entry<String, String>) o;
				writer.startNode(entry.getKey());
				writer.setValue(entry.getValue());
				writer.endNode();
			});
		}

		public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context)
		{
			val map = new HashMap<String, String>();
			while (reader.hasMoreChildren())
			{
				reader.moveDown();
				map.put(reader.getNodeName(), reader.getValue());
				reader.moveUp();
			}
			return map;
		}
	}
}

