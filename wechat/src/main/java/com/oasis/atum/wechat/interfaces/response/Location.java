package com.oasis.atum.wechat.interfaces.response;

import lombok.Builder;

/**
 * 用户微信地理位置信息
 */
@Builder
public class Location
{
	public final String latitude;
	public final String longitude;
	public final String scale;
	public final String label;
	public final String precision;

//	@JSONCreator
//	public Location(@JSONField(name = "latitude") final String latitude, @JSONField(name = "longitude") final String longitude,
//									@JSONField(name = "scale") final String scale, @JSONField(name = "label") final String label,
//									@JSONField(name = "precision") final String precision)
//	{
//		this.latitude = latitude;
//		this.longitude = longitude;
//		this.scale = scale;
//		this.label = label;
//		this.precision = precision;
//	}
}
