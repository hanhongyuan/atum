package com.oasis.atum.wechat.infrastructure.util;

import com.alibaba.fastjson.JSON;
import com.oasis.atum.wechat.interfaces.request.WechatRequest;
import com.oasis.atum.wechat.interfaces.response.WechatResponse;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import lombok.val;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 微信XML生成工具
 * Created by ryze on 2017/4/29.
 */
public interface XMLUtil
{
	/**
	 * 线程工具 XStream
	 */
	ThreadLocal<XStreamHolder> X_STREAM_THREAD_LOCAL = ThreadLocal.withInitial(XStreamHolder::new);

	static XStream getXStream()
	{
		return X_STREAM_THREAD_LOCAL.get().getXStream();
	}

	final class XStreamHolder
	{
		private final XStream xstream;

		XStreamHolder()
		{
			xstream = new XStream(new XppDriver()
			{
				public HierarchicalStreamWriter createWriter(Writer out)
				{
					return new PrettyPrintWriter(out)
					{
						// 对所有xml节点的转换都增加CDATA标记
						boolean cdata = true;

						public void startNode(String name, @SuppressWarnings("rawtypes") Class clazz)
						{
							super.startNode(name, clazz);
						}

						/**
						 * 双下划线问题
						 * @param name
						 * @return
						 */
						@Override
						public String encodeNode(final String name)
						{
							return name;
						}

						protected void writeText(QuickWriter writer, String text)
						{
							if (cdata)
							{
								writer.write("<![CDATA[");
								writer.write(text);
								writer.write("]]>");
							}
							else writer.write(text);
						}
					};
				}
			});
			xstream.autodetectAnnotations(true);
		}

		XStream getXStream()
		{
			return xstream;
		}
	}

	static String toXML(final Object object)
	{
		val xStream = getXStream();
		xStream.alias("xml", object.getClass());
		xStream.registerConverter(new MapEntryConverter());
		xStream.alias("item", WechatResponse.NewsArticle.class);
		return getXStream().toXML(object);
	}

	static <T> T parseXML(final String xml, final Class<T> clazz)
	{
		if (Objects.isNull(xml)) return null;

		val xStream = getXStream();
		xStream.alias("xml", clazz);
		return (T) xStream.fromXML(xml);
	}

	/**
	 * 解析微信服务器请求XML
	 * @param request 微信请求
	 * @return Map数据
	 */
	static Mono<WechatRequest> parseXML(final ServerHttpRequest request)
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
	final class MapEntryConverter implements Converter
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

