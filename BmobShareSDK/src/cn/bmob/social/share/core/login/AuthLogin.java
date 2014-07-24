package cn.bmob.social.share.core.login;

import cn.bmob.social.share.core.util.Util;
import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

/**
 * 授权登录
 * @author youtui 
 * @since 14/6/19
 */
public  class AuthLogin {
	/**
	 * 新浪授权登录
	 * @param act 
	 */
	public void sinaAuth(Activity act,AuthListener listener) {
		if (Util.isNetworkConnected(act)) {
			Intent sinaLogin = new Intent(act, AuthActivity.class);
			sinaLogin.putExtra("flag", "sina");
			AuthActivity.authListener = listener;
			act.startActivity(sinaLogin);
		} else {
			Toast.makeText(act, "无网络连接...", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * qq授权登录
	 * @param act
	 */
	public void qqAuth(Activity act,AuthListener listener) {
		if (Util.isNetworkConnected(act)) {
			Intent qqLogin = new Intent(act, AuthActivity.class);
			qqLogin.putExtra("flag", "qq");
			AuthActivity.authListener = listener;
			act.startActivity(qqLogin);
			
		} else {
			Toast.makeText(act, "无网络连接...", Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * 腾讯微博授权登录
	 * @param act
	 */

	public void tencentWbAuth(Activity act,AuthListener listener) {
		if (Util.isNetworkConnected(act)) {
			Intent tencentWbLogin = new Intent(act, AuthActivity.class);
			tencentWbLogin.putExtra("flag", "tencentWb");
			AuthActivity.authListener = listener;
			act.startActivity(tencentWbLogin);
		} else {
			Toast.makeText(act, "无网络连接...", Toast.LENGTH_SHORT).show();
		}

	}

}
