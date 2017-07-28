package com.oasis.atum.wechat.infrastructure.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

import java.io.Writer;

/**
 * 当前线程下常用工具
 * 一个线程只需要一个就好
 * 减少new开销和噪声
 * Created by ryze on 2017/4/29.
 */
public class CommonUtil extends com.oasis.atum.base.infrastructure.util.CommonUtil
{
	/**
	 * 线程工具 XStream
	 */
	private static final ThreadLocal<XStreamHolder> X_STREAM_THREAD_LOCAL = ThreadLocal.withInitial(XStreamHolder::new);


	public static XStream getXStream()
	{
		return X_STREAM_THREAD_LOCAL.get().getXStream();
	}

	private static class XStreamHolder
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
}
