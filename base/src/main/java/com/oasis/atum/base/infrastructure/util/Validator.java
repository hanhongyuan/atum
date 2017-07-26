package com.oasis.atum.base.infrastructure.util;

import lombok.NonNull;

import java.util.Objects;

/**
 * 校验器
 * Created by ryze on 2017/5/14.
 */
public final class Validator
{
	/**
	 * 手机号校验
	 * @param s
	 * @return
	 */
	public static boolean isMobile(@NonNull final String s)
	{
		return s.matches(
			"^(1[3,5,8,7]{1}[\\d]{9})|(((400)-(\\d{3})-(\\d{4}))|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-" +
				"(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)$");
	}

	/**
	 * 是否是IP
	 * @param s
	 * @return
	 */
	public static boolean isIP(@NonNull final String s)
	{
		return s.matches("^((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)$");
	}

	/**
	 * true a false b
	 * @param a
	 * @param b
	 * @param <T>
	 * @return
	 */
	public static <T> T either(final T a, final T b)
	{
		return Objects.isNull(a) ? b : a;
	}
}
