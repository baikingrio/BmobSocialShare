package cn.bmob.social.share.core.data;

/**
 * 
 * 分享平台信息
 * @author 稻草人
 * @date 2014年7月18日 下午3:59:32
 *
 */
public enum BMPlatform {
	PLATFORM_SINAWEIBO, PLATFORM_TENCENTWEIBO, PLATFORM_QZONE, PLATFORM_WECHAT, PLATFORM_RENN, PLATFORM_QQ, PLATFORM_MESSAGE, PLATFORM_EMAIL, PLATFORM_WECHATMOMENTS, PLATFORM_MORE_SHARE,
	PLATFORM_COPYLINK;
	
	/**
	 * 通过平台ID获取平台名字，如果没有该ID则返回null
	 * @param platform
	 * @return
	 */
	public static String getPlatfornName(BMPlatform platform) {
		switch (platform) {
		case PLATFORM_SINAWEIBO:
			return "SinaWeibo";
		case PLATFORM_TENCENTWEIBO:
			return "TencentWeibo";
		case PLATFORM_QZONE:
			return "QZone";
		case PLATFORM_WECHAT:
			return "Wechat";
		case PLATFORM_RENN:
			return "Renren";
		case PLATFORM_QQ:
			return "QQ";
		case PLATFORM_MESSAGE:
			return "ShortMessage";
		case PLATFORM_EMAIL:
			return "Email";
		case PLATFORM_MORE_SHARE:
			return "More";
		case PLATFORM_WECHATMOMENTS:
			return "WechatMoments";
		case PLATFORM_COPYLINK:
			return "CopyLink";
		default:
			break;
		}
		return null;
	}
	
	public static BMPlatform getBMPlatformByName(String name){
		if("Wechat".equals(name)){
			return PLATFORM_WECHAT;
		}else if("WechatMoments".equals(name)){
			return PLATFORM_WECHATMOMENTS;
		}else if("QQ".equals(name)){
			return PLATFORM_QQ;
		}else if("QZone".equals(name)){
			return PLATFORM_QZONE;
		}else if("TencentWeibo".equals(name)){
			return PLATFORM_TENCENTWEIBO;
		}else if("SinaWeibo".equals(name)){
			return PLATFORM_SINAWEIBO;
		}else if("Renren".equals(name)){
			return PLATFORM_RENN;
		}else if("ShortMessage".equals(name)){
			return PLATFORM_MESSAGE;
		}else if("Email".equals(name)){
			return PLATFORM_EMAIL;
		}else if("CopyLink".equals(name)){
			return PLATFORM_COPYLINK;
		}else if("More".equals(name)){
			return PLATFORM_MORE_SHARE;
		}else {
			return null;
		}
	}
	
}
