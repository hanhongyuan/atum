package com.oasis.atum.base.infrastructure.util;

import lombok.val;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 加密工具
 * Created by ryze on 2017/4/28.
 */
public interface EncryptionUtil
{
	/**
	 * MD5加密
	 * @param s
	 * @return
	 */
	static String MD5(final String s)
	{
		try
		{
			val md5 = MessageDigest.getInstance("MD5");
			md5.update(StandardCharsets.UTF_8.encode(s));
			return String.format("%032x", new BigInteger(1, md5.digest()));
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * SHA1加密
	 * @param s
	 * @return
	 */
	static String SHA1(final String s)
	{
		try
		{
			val sha1 = MessageDigest.getInstance("SHA1");
			sha1.update(StandardCharsets.UTF_8.encode(s));
			return String.format("%032x", new BigInteger(1, sha1.digest()));
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Base64加密
	 * @param s
	 * @return
	 */
	static String base64Encode(final String s)
	{
		return new String(Base64.getEncoder().encode(s.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
	}

	/**
	 * Base64解密
	 * @param s
	 * @return
	 */
	static String base64Decode(final String s)
	{
		return new String(Base64.getDecoder().decode(s.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
	}
}