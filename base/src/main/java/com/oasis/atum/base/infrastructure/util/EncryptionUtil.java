package com.oasis.atum.base.infrastructure.util;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密工具
 * Created by ryze on 2017/4/28.
 */
@Slf4j
public final class EncryptionUtil
{
	/**
	 * MD5加密
	 * @param s
	 * @return
	 */
	public static String MD5(final String s)
	{
		try
		{
			val md5 = MessageDigest.getInstance("MD5");
			md5.update(StandardCharsets.UTF_8.encode(s));
			return String.format("%032x", new BigInteger(1, md5.digest()));
		}
		catch (NoSuchAlgorithmException e)
		{
			log.error("MD5加密失败", e);
			return null;
		}
	}

	/**
	 * SHA1加密
	 * @param s
	 * @return
	 */
	public static String SHA1(final String s)
	{
		try
		{
			val sha1 = MessageDigest.getInstance("SHA1");
			sha1.update(StandardCharsets.UTF_8.encode(s));
			return String.format("%032x", new BigInteger(1, sha1.digest()));
		}
		catch (NoSuchAlgorithmException e)
		{
			log.error("SHA1加密失败", e);
			return null;
		}
	}
}