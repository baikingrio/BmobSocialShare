package cn.bmob.social.share.core.social;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import cn.bmob.social.share.core.BMCore;
import cn.bmob.social.share.core.data.PlatformKeyInfo;
import cn.bmob.social.share.core.data.ShareData;
import cn.bmob.social.share.core.util.AccessTokenKeeper;
import cn.bmob.social.share.core.util.PlatformAppHelper;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
/**
 * 新浪微博分享操作以及分享回调
 * @author youtui
 * @since 14/4/25 
 */
public class SinaShare {
	/**新浪微博分享接口*/
	private IWeiboShareAPI iWeiboShareAPI;
	/**传入的activity*/
	private Activity activity;
	/**新浪微博授权AccessToken*/
	private Oauth2AccessToken oauth2AccessToken;
	/**分享微博授权接口*/
	private WeiboAuth mWeiboAuth;
	/**新浪微博sso类*/
	private SsoHandler mSsoHandler;
	/**待分享数据*/
	private ShareData shareData;
	
	public SinaShare(Activity activity,ShareData shareData) {
		this.activity = activity;
		iWeiboShareAPI = WeiboShareSDK.createWeiboAPI(activity, PlatformKeyInfo.sinaWeibo_AppKey );
		this.shareData = shareData;
	}

	/**
	 * 发送共享到新浪微博
	 * @param shareData
	 * @return 该次分享的标示
	 */
	public void shareToSina() {
		// 新浪微博在10351以上版本支持多条微博发送
		if (iWeiboShareAPI.getWeiboAppSupportAPI() >= 10351) {
			sendMultiMessage();
		} else {
			sendSingleMessage();
		}

	}
	
	/**
	 * 新浪微博sso授权
	 */
	public void sinaAuth() {
		mWeiboAuth = new WeiboAuth(activity, PlatformKeyInfo.sinaWeibo_AppKey, PlatformKeyInfo.sinaWeibo_RedirectUrl, PlatformAppHelper.SINA_WEIBO_SCOPE);
		mSsoHandler = new SsoHandler(activity, mWeiboAuth);
		mSsoHandler.authorize(new AuthListener());
	}

	/**
	 * sso回调需要
	 */

	public void sinaResult(int requestCode, int resultCode, Intent data) {
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	/**
	 * 第三方应用发送请求消息到微博，唤起微博分享界面 < 10351 时，只支持分享单条消息，该方法发送一条网页消息
	 * 
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	private boolean sendSingleMessage() {

		// 1. 初始化微博的分享消息
		WeiboMessage weiboMessage = new WeiboMessage();
		weiboMessage.mediaObject = getImageObj(shareData);
		// 2. 初始化从第三方到微博的消息请求
		SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.message = weiboMessage;
		// 3. 发送请求消息到微博，唤起微博分享界面
		return iWeiboShareAPI.sendRequest(request);
	}

	/**
	 * 第三方应用发送请求消息到微博，唤起微博分享界面 > 10351 时，支持分享多条消息，该方法发送一条网页消息和一条图片消息
	 */

	private void sendMultiMessage() {	
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
		weiboMessage.imageObject = getImageObj(shareData);
		weiboMessage.textObject = getTextObject(shareData);
		
//		weiboMessage.imageObject.actionUrl = "http://www.baidu.com";
//		weiboMessage.textObject.actionUrl = "http://www.baidu.com";
//		weiboMessage.imageObject.imagePath = "http://file.bmob.cn/M00/02/B4/wKhkA1OuzHeATJ5KAAAMie-ZImI961.jpg";
//		Log.d("bmob", "******55****"+weiboMessage.imageObject.actionUrl+" ---- "+weiboMessage.imageObject.imagePath);
		Log.d("bmob", "******88****"+weiboMessage.textObject.actionUrl+" ---- "+weiboMessage.textObject.text+" --- "+weiboMessage.textObject.title+" --- "+weiboMessage.textObject.description);
		// 2. 初始化从第三方到微博的消息请求
		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = weiboMessage;
		// 3. 发送请求消息到微博，唤起微博分享界面
		iWeiboShareAPI.sendRequest(request);
	}

	/**
	 * 获得要分享的文字信息
	 * 
	 * @return
	 */
	private TextObject getTextObject(ShareData shareData) {

		TextObject textObject = new TextObject();
		textObject.title = shareData.getTitle();
		textObject.description = shareData.getDescription();
		String text = shareData.getText();
		//如果文字太长，截取部分，不然微博无法发送
		if(text.length()>110){
			text = text.substring(0, 109);
			text += "...";
		}
		//YtLog.i("shareData.getTarget_url()", shareData.getTarget_url());
		textObject.text = TextUtils.isEmpty(shareData.getTarget_url()) ? text : text+shareData.getTarget_url();
		textObject.actionUrl = shareData.getTarget_url();

		return textObject;
	}

	/**
	 * 获得要分享的图片信息
	 * 
	 * @return
	 */
	private ImageObject getImageObj(ShareData shareData) {
		ImageObject image = new ImageObject();
		Bitmap bitmap = null;
		if (shareData == null) {
		} else if (shareData.getImagePath() != null) {
			bitmap = BitmapFactory.decodeFile(shareData.getImagePath());
		} else if (shareData.getImageUrl() != null) {
			try {
				bitmap = BitmapFactory.decodeStream(new URL(shareData.getImageUrl()).openStream());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (bitmap == null) {
			return null;
//			bitmap = BitmapFactory.decodeResource(activity.getResources(), BMCore.res.getIdentifier("loadfail", "drawable", BMCore.packName));
		}
		image.setImageObject(bitmap);		
		image.actionUrl = shareData.getTarget_url();
		image.description = shareData.getText();
		return image;
	}

	/**
	 * 监听新浪微博授权结果
	 */

	class AuthListener implements WeiboAuthListener {

		@Override
		public void onCancel() {
			Toast.makeText(activity, "授权取消", Toast.LENGTH_SHORT).show();
			activity.finish();
		}

		@Override
		public void onComplete(Bundle bundle) {
			oauth2AccessToken = Oauth2AccessToken.parseAccessToken(bundle);
			if (oauth2AccessToken.isSessionValid()) {
				AccessTokenKeeper.writeAccessToken(activity, oauth2AccessToken);
			}
			iWeiboShareAPI.registerApp();
			Toast.makeText(activity, "授权成功,请点击进行分享", Toast.LENGTH_SHORT).show();
			activity.finish();
		}

		@Override
		public void onWeiboException(WeiboException arg0) {
			Toast.makeText(activity, "授权错误", Toast.LENGTH_SHORT).show();
			activity.finish();
		}
	}
}
