package com.oasis.atum.base.infrastructure.util;

import lombok.val;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.util.Date;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 日期工具包
 */
public interface DateUtil
{
	String FULLDATE = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 年龄
	 * @param birthday 生日
	 * @return
	 */
	static Integer getAge(final Date birthday)
	{
		val now = DateTime.now().getYear();
		val bir = new DateTime(birthday).getYear();
		return now - bir;
	}

	/**
	 * 时间戳
	 * @return
	 */
	static long timeStamp()
	{
		return System.currentTimeMillis() / 1000;
	}

	/**
	 * 默认格式 yyyy-MM-dd HH:mm:ss
	 * @param str
	 * @return
	 */
	static Date toDate(final String str)
	{
		return toDate(str, FULLDATE);
	}

	/**
	 * 按指定格式解析日期字符串
	 * @param str
	 * @param pattern
	 * @return
	 */
	static Date toDate(final String str, final String pattern)
	{
		if (Objects.nonNull(str) && !str.equals("")) return DateTime.parse(str, DateTimeFormat.forPattern(pattern)).toDate();
		return null;
	}

	/**
	 * 获取本周的日期
	 * yyyy-MM-dd
	 * @return
	 */
	static Stream<LocalDate> getWeekDays()
	{
		//本周一开始循环
		return Stream.iterate(LocalDate.parse("2017-08-02").dayOfWeek().withMinimumValue(), d -> d.plusDays(1)).limit(7);
	}
}
