package com.oasis.atum.base.infrastructure.util;

import lombok.val;

import java.util.Random;
import java.util.stream.Stream;

/**
 * 当前线程下常用工具
 * 一个线程只需要一个就好
 * 减少new开销和噪声
 * Created by ryze on 2017/4/29.
 */
public interface BaseUtil
{
	/**
	 * 线程工具 String
	 */
	ThreadLocal<StringHolder> STRING_HOLDER_THREAD_LOCAL = ThreadLocal.withInitial(StringHolder::new);

	static StringBuilder getStringBuilder()
	{
		return STRING_HOLDER_THREAD_LOCAL.get().getStringBuilder();
	}

	static StringBuffer getStringBuffer()
	{
		return STRING_HOLDER_THREAD_LOCAL.get().getStringBuffer();
	}

	final class StringHolder
	{
		private final StringBuilder sBuilder;
		private final StringBuffer  sBuffer;
		//创建时候指定默认长度为16
		private final int size = 16;
		private final int zero = 0;

		StringHolder()
		{
			sBuilder = new StringBuilder(size);
			sBuffer = new StringBuffer(size);
		}

		StringBuilder getStringBuilder()
		{
			//将上次使用数据清空 线程重复使用单个StringBuilder
			sBuilder.setLength(zero);
			return sBuilder;
		}

		StringBuffer getStringBuffer()
		{
			sBuffer.setLength(zero);
			return sBuffer;
		}
	}

	/**
	 * 产生指定位数纯数字随机数
	 * @param length
	 * @return
	 */
	static String randomNum(final int length)
	{
		val r = new Random();
		return Stream
							 .iterate(r.nextInt(10) + "", x -> r.nextInt(10) + "")
							 .limit(length)
							 .reduce((x, y) -> x + y)
							 .get();
	}

	/**
	 * 产生指定位数随机字符串
	 * @return
	 */
	static String random(final int length)
	{
		val r = new Random();

		return Stream
							 .iterate(String.valueOf(r.nextInt(10)), s ->
							 {
								 // 输出字母还是数字
								 val output = r.nextInt(2) % 2 == 0 ? "char" : "int";
								 // 小写
								 val small = 97;
								 return "char".equals(output) ? String.valueOf((char) (r.nextInt(26) + small))
														: String.valueOf(r.nextInt(10));
							 })
							 .limit(length)
							 .reduce((x, y) -> x + y)
							 .get();
	}
}
