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
public interface Restful
{
	static ResponseEntity ok()
	{
		return ResponseEntity.ok()
							 .contentType(MediaType.APPLICATION_JSON_UTF8)
							 .cacheControl(CacheControl.noCache())
							 .build();
	}

	static <T> ResponseEntity ok(final String key, final T data)
	{
		val json = new JSONObject();
		json.put(key, data);
		return ok(json);
	}

	/**
	 * 资源存在 => 200
	 * 不存在 => 404
	 * @param data
	 * @return
	 */
	static <T> ResponseEntity ok(final T data)
	{
		return ok(Optional.ofNullable(data));
	}

	static <T> ResponseEntity ok(final Optional<T> data)
	{
		return data.map(o -> ResponseEntity.ok()
														 .contentType(MediaType.APPLICATION_JSON_UTF8)
														 .cacheControl(CacheControl.noCache())
														 .body(JSONObject.toJSON(data))
		).orElseGet(() -> ResponseEntity.notFound().cacheControl(CacheControl.noCache()).build());
	}

	/**
	 * 201 创建资源
	 * @return
	 */
	static ResponseEntity created()
	{
		return ResponseEntity.created(null)
							 .cacheControl(CacheControl.noCache())
							 .contentType(MediaType.APPLICATION_JSON_UTF8)
							 .build();
	}

	/**
	 * 204 删除资源
	 * @return
	 */
	static ResponseEntity noContent()
	{
		return ResponseEntity.noContent()
							 .cacheControl(CacheControl.noCache())
							 .build();
	}

	/**
	 * 400请求
	 * @return
	 */
	static ResponseEntity badRequest()
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
	static <T> ResponseEntity badRequest(final T data)
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
	static ResponseEntity badRequest(final int code, final String msg)
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
	 * 404资源不存在
	 * @return
	 */
	static ResponseEntity notFound()
	{
		return ResponseEntity.notFound().cacheControl(CacheControl.noCache()).build();
	}
}
