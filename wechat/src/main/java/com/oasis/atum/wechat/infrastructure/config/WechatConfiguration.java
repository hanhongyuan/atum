package com.oasis.atum.wechat.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信基本配置以及接口
 * Created by ryze on 2017/4/28.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wechat")
public class WechatConfiguration
{
	/**
	 * 应用ID
	 */
	private String appId    = "wx846dd6977628a1e6";
	/**
	 * 微信小程序ID
	 */
	private String appletId = "wx1deb8f7f1a3ca5cf";
	/**
	 * 密钥
	 */
	private String secret;
	/**
	 * 商户号
	 */
	private String mchId       = "1295374701";
	/**
	 * 小程序商户号
	 */
	private String appletMchId = "1238751702";
	/**
	 * 在公众账号后台设置的Key
	 */
	private String key         = "16109f8c2f625b25bb6d4a15bf8449b4";
	/**
	 * 小程序签名
	 */
	private String appletKey   = "theonlythingwehavetofearisfearit";
	/**
	 * 证书路径
	 */
	private String certPath;
	/**
	 * 证书密码 默认是商户号
	 */
	private String certPassword;
	/**
	 * 在公众账号后台设置的令牌
	 */
	private String  token            = "OasisWechatTokenByRyze";
	/**
	 * 微信支付下单接口
	 */
	private String  pay              = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	/**
	 * 微信支付查询接口
	 */
	private String  query            = "https://api.mch.weixin.qq.com/pay/orderquery";
	/**
	 * 微信支付订单关闭接口
	 */
	private String  close            = "https://api.mch.weixin.qq.com/pay/closeorder";
	/**
	 * 微信支付退款接口(需要Https认证)
	 */
	private String  refund           = "https://api.mch.weixin.qq.com/secapi/pay/refund";
	/**
	 * 微信支付退款查询接口
	 */
	private String  refundQuery      = "https://api.mch.weixin.qq.com/pay/refundquery";
	/**
	 * 微信对账单下载接口
	 */
	private String  downloadBill     = "https://api.mch.weixin.qq.com/pay/downloadbill";
	/**
	 * 微信网关地址
	 */
	private String  wechatUri        = "https://api.weixin.qq.com/";
	/**
	 * 是否启动JsApi 默认不启动
	 */
	private boolean enableJsApi      = false;
	/**
	 * 是否启动ApiTicket 默认不启动
	 */
	private boolean enableApiTicket  = false;
	/**
	 * 微信支付回调地址
	 */
	private String  paymentNotifyUri = "https://buztest190.oasisapp.cn/honghclient/servlet/WeChatTrade";
	/**
	 * 关注标签
	 */
	private Integer wxId             = 102;

	/**
	 * 微信模版消息ID
	 */
	@Data
	@Configuration
	@ConfigurationProperties(prefix = "wechat.template")
	public class TemplateConfiguration
	{
		/**
		 * 预约挂号成功
		 */
		private String register = "6SIZiS5jp8FM42GYKov76YWGTZBGSGdoqMe44vUwpZQ";

		/**
		 * 订单支付完成
		 */
		private String success = "GXCh4lcBDLkzPYncwTuoU4Ge0jUcRt5KdQqzxMdNHVQ";

		/**
		 * 订单取消
		 */
		private String cancel = "JWztyYZd_dZtCoj63lJvXCLsNtHkegBKnLr0G--MgJk";

		/**
		 * 订单提交完成
		 */
		private String submit = "uZd43h_K_u3Vma6_A9hU7B4trR6tf6LYh5TM9UjMmbk";
	}
}
