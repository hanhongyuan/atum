package com.oasis.atum.wechat.infrastructure.constant;

/**
 * 事件类型
 */
public final class EventType
{
	/**
	 * 订阅
	 */
	public static final String SUBSCRIBE             = "subscribe";
	/**
	 * 取消订阅
	 */
	public static final String UNSUBSCRIBE           = "unsubscribe";
	/**
	 * 点击
	 */
	public static final String CLICK                 = "CLICK";
	/**
	 * 跳转
	 */
	public static final String VIEW                  = "VIEW";
	/**
	 * 地理位置
	 */
	public static final String LOCATION              = "LOCATION";
	/**
	 * 用户扫描带场景值二维码,如果关注了.
	 */
	public static final String SCAN                  = "SCAN";
	public static final String SCANCODEPUSH          = "scancode_push";
	public static final String SCANCODEWAITMSG       = "scancode_waitmsg";
	public static final String PICSYSPHOTO           = "pic_sysphoto";
	public static final String PICPHOTOORALBUM       = "pic_photo_or_album";
	public static final String PICWEIXIN             = "pic_weixin";
	public static final String LOCATIONSELECT        = "location_select";
	public static final String TEMPLATESENDJOBFINISH = "TEMPLATESENDJOBFINISH";
	public static final String MASSSENDJOBFINISH     = "MASSSENDJOBFINISH";

}