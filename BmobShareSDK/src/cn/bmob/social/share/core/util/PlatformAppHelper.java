package cn.bmob.social.share.core.util;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;


/**
 * 判断分享平台是否被安装
 * @author youtui
 * @since 14/6/19
 */
public class PlatformAppHelper {
	
	/**人人网授权权限*/
	public static final String RENREN_SCOPE = "read_user_blog read_user_photo read_user_status read_user_album read_user_comment read_user_share publish_blog publish_share send_notification photo_upload status_update create_album publish_comment publish_feed";
	/**新浪微博授权权限*/
	public static final String SINA_WEIBO_SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
			+ "follow_app_official_microblog," + "invitation_write";
	/**腾讯微博授权权限*/
	public static final String TENCENT_SCOPE = "all";
	
	/**新浪微博包名*/
	public static final String PACKAGE_NAME_SINA_WEIBO = "com.sina.weibo";
	/**qq包名*/
	public static final String PACKAGE_NAME_TENCENT_QQ = "com.tencent.mobileqq";
	/**人人网包名*/
	public static final String PACKAGE_NAME_RENREN = "com.renren.mobile.android";
	/**微信包名*/
	public static final String PACKAGE_NAME_WEIXIN = "com.tencent.mm";
	
	/**
	 * 通过package name检查APP是否已经安装
	 */
	private static boolean checkApp(Context context, String packageName) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packs = pm.getInstalledPackages(0);
		for (PackageInfo pi : packs) {
			if (pi.applicationInfo.packageName.equals(packageName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检查新浪微博是否已经安装
	 */
	public static boolean isSinaWeiboExisted(Context context) {
		if (checkApp(context, PACKAGE_NAME_SINA_WEIBO)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检查腾讯QQ是否已经安装
	 */
	public static boolean isTencentQQExisted(Context context) {
		if (checkApp(context, PACKAGE_NAME_TENCENT_QQ)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检查人人客户端是否已经安装
	 */
	public static boolean isRenrenExisted(Context context) {
		if (checkApp(context, PACKAGE_NAME_RENREN)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检查微信是否已经安装
	 */
	public static boolean isWeixinExisted(Context context) {
		if (checkApp(context, PACKAGE_NAME_WEIXIN)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 用于检测该intent能否可以使用
	 */
	public static boolean isIntentAvailable(Context context, Intent intent) {
		final PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.GET_ACTIVITIES);
		return list.size() > 0;
	}
}
