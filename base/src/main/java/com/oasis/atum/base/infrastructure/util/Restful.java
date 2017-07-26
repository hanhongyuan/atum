package com.oasis.atum.base.infrastructure.util;

import com.alibaba.fastjson.JSONObject;
import lombok.val;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

/**
 * Restful风格返回工具
 * 现在Spring-Cloud ReactiveFunctional拿不到单线程Request,暂不做超媒体.
 */
public final class Restful
{
	public static ResponseEntity ok()
	{
		return ResponseEntity.ok()
						 .contentType(MediaType.APPLICATION_JSON_UTF8)
						 .cacheControl(CacheControl.noCache())
						 .build();
	}

	/**
	 * 资源存在 => 200
	 * 不存在 => 404
	 * @param data
	 * @return
	 */
	public static <T> ResponseEntity ok(final T data)
	{
		return ok(Optional.ofNullable(data));
	}

	public static <T> ResponseEntity ok(final Optional<T> data)
	{
		return data.map(o -> ResponseEntity.ok()
													 .contentType(MediaType.APPLICATION_JSON_UTF8)
													 .cacheControl(CacheControl.noCache())
													 .body(JSONObject.toJSON(data))
		).orElseGet(() -> ResponseEntity.notFound().cacheControl(CacheControl.noCache()).build());
	}

	/**
	 * 400请求
	 * @return
	 */
	public static ResponseEntity badRequest()
	{
		return ResponseEntity.badRequest()
						 .cacheControl(CacheControl.noCache())
						 .contentType(MediaType.APPLICATION_JSON_UTF8)
						 .build();
	}

	/**
	 * 400请求
	 * @param data 返回错误描述
	 * @param <T>
	 * @return
	 */
	public static <T> ResponseEntity badRequest(final T data)
	{
		return ResponseEntity.badRequest()
						 .cacheControl(CacheControl.noCache())
						 .contentType(MediaType.APPLICATION_JSON_UTF8)
						 .body(data);
	}

	/**
	 * 400请求
	 * @param code 错误码
	 * @param msg  错误信息
	 * @return
	 */
	public static ResponseEntity badRequest(final int code, final String msg)
	{
		val json = new JSONObject();
		json.put("code", code);
		json.put("msg", msg);
		return ResponseEntity.badRequest()
						 .cacheControl(CacheControl.noCache())
						 .contentType(MediaType.APPLICATION_JSON_UTF8)
						 .body(json);
	}

	/**
	 * 删除资源
	 * @return
	 */
	public static ResponseEntity noContent()
	{
		return ResponseEntity.noContent()
						 .cacheControl(CacheControl.noCache())
						 .build();
	}
}
