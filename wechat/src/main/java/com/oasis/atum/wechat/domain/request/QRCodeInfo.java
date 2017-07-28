package com.oasis.atum.wechat.domain.request;

import com.alibaba.fastjson.JSONObject;

/**
 * 二维码信息值对象
 */
public class QRCodeInfo
{
	public final JSONObject scene = new JSONObject();

	public void setSceneStr(final String sceneStr)
	{
		scene.put("scene_str", sceneStr);
	}

	public void setSceneId(final String sceneId)
	{
		scene.put("scene_id", sceneId);
	}
}