package com.oasis.atum.wechat.interfaces.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OpenId相关信息
 */
@Data
@NoArgsConstructor
public class OpenId
{
	private String   clinic;
	private Location location;
	private boolean  isFollow;

	/**
	 * 创建用户地理位置信息
	 * @param latitude
	 * @param longitude
	 * @param scale
	 * @param label
	 * @param precision
	 * @return
	 */
	public Location createLocation(final String latitude, final String longitude, final String scale, final String label, final String precision)
	{
		location = Location.builder().latitude(latitude).longitude(longitude).scale(scale).label(label).precision(precision).build();
		return location;
	}
}
