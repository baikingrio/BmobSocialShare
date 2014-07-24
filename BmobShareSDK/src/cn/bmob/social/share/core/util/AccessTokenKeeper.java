
package cn.bmob.social.share.core.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;

/**
 * 该类定义了AccessToken相关的操作
 * @author youtui
 * @since 14/3/25 
 */
public class AccessTokenKeeper {
	/**新浪微博保存的授权信息文件名*/
    private static final String PREFERENCES_NAME = "com_weibo_sdk_android";
    /**新浪微博授权文件保存用户id的字段*/
    private static final String KEY_UID           = "uid";
    /**新浪微博授权文件保存access_token的字段*/
    private static final String KEY_ACCESS_TOKEN  = "access_token";
    /**新浪微博授权文件保存过期时间的字段*/
    private static final String KEY_EXPIRES_IN    = "expires_in";
    /**腾讯QQ授权文件保存openid的字段*/
    private static final String KEY_OPENID = "openid";
    
    /**
     * 读取腾讯开放平台AccessToken
     */
    public static String readQQAccessToken(Context context){
    	SharedPreferences sp = context.getSharedPreferences("tencent_open_access", 0);
		return sp.getString(KEY_ACCESS_TOKEN, null);
    }
    /**
     * 读取腾讯开放平台Expires
     */
    public static String readQQExpires(Context context){
    	SharedPreferences sp = context.getSharedPreferences("tencent_open_access", 0);
		return sp.getString(KEY_EXPIRES_IN, null);
    }
    /**
     * 读取腾讯开放平台openid
     */
    
    public static String readQQOpenid(Context context){
    	SharedPreferences sp = context.getSharedPreferences("tencent_open_access", 0);
		return sp.getString(KEY_OPENID, null);
    }
    
    
    
    
    /**
     * 保存 Token 对象到 SharedPreferences。
     * 
     * @param context 应用程序上下文环境
     * @param token   Token 对象
     */
    public static void writeAccessToken(Context context, Oauth2AccessToken token) {
        if (null == context || null == token) {
            return;
        }
        
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.putString(KEY_UID, token.getUid());
        editor.putString(KEY_ACCESS_TOKEN, token.getToken());
        editor.putLong(KEY_EXPIRES_IN, token.getExpiresTime());
        editor.commit();
    }

    /**
     * 从 SharedPreferences 读取 Token 信息。
     * 
     * @param context 应用程序上下文环境
     * 
     * @return 返回 Token 对象
     */
    public static Oauth2AccessToken readAccessToken(Context context) {
        if (null == context) {
            return null;
        }
        
        Oauth2AccessToken token = new Oauth2AccessToken();
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        token.setUid(pref.getString(KEY_UID, ""));
        token.setToken(pref.getString(KEY_ACCESS_TOKEN, ""));
        token.setExpiresTime(pref.getLong(KEY_EXPIRES_IN, 0));
        return token;
    }

    /**
     * 清空 SharedPreferences 中 Token信息。
     * 
     * @param context 应用程序上下文环境
     */
    public static void clear(Context context) {
        if (null == context) {
            return;
        }
        
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
    /**
     * 判断腾讯微博授权是否过期
     */
    public static boolean isTencentWbAuthExpired(Context context){
		boolean expired = true;
		SharedPreferences preference = context.getSharedPreferences("ANDROID_SDK", 0);
		String authorizeTimeStr = preference.getString("AUTHORIZETIME", null);
		String expiresTime = preference.getString( "EXPIRES_IN",null);
		long currentTime = System.currentTimeMillis() / 1000;
		if (expiresTime != null&&expiresTime!="" && authorizeTimeStr != null&&authorizeTimeStr!="") {
			if ((Long.valueOf(authorizeTimeStr) + Long.valueOf(expiresTime)) > currentTime) {
				expired = false;
			}
		}
		return expired;
    } 
    
    
}
