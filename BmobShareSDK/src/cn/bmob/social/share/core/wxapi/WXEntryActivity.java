package cn.bmob.social.share.core.wxapi;

import cn.bmob.social.share.core.BMCore;
import cn.bmob.social.share.core.BMShareListener;
import cn.bmob.social.share.core.ErrorInfo;
import cn.bmob.social.share.core.data.BMPlatform;
import cn.bmob.social.share.core.data.PlatformKeyInfo;
import cn.bmob.social.share.core.data.ShareData;
import cn.bmob.social.share.core.util.BMLog;
import cn.bmob.social.share.core.util.Util;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;

/**
 * 微信分享activity
 * @author youtui
 * @since 14/5/4 
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	/**微信接口*/
	private IWXAPI mIWXAPI;
	/**待分享图片*/
	private Bitmap bitmap;
	/**分享图片的缩略图*/
	private Bitmap bmpThum;
	/**短链接*/
	private String shortUrl;
	/**真实网址*/
	private String realUrl ;
	/**微信是否为分享时打开*/
	private boolean fromShare;
	/**分享事件监听*/
	public static BMShareListener listener;
	/**分享的平台,用于区别微信好友和微信朋友圈*/
	private BMPlatform platform;
	/**待分享数据*/
	public static ShareData shareData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		// 判断是否为朋友圈
		platform = (BMPlatform) getIntent().getExtras().get("platform");
		fromShare = getIntent().getExtras().getBoolean("fromShare");
		shortUrl = getIntent().getExtras().getString("shortUrl");
		 realUrl = getIntent().getExtras().getString("realUrl");
		 
		// 传入的pointArr不为null时
		if (platform == BMPlatform.PLATFORM_WECHATMOMENTS) {
			mIWXAPI = WXAPIFactory.createWXAPI(WXEntryActivity.this, PlatformKeyInfo.wechatMoments_AppId, false);
			mIWXAPI.registerApp(PlatformKeyInfo.wechatMoments_AppId);
		} else {
			mIWXAPI = WXAPIFactory.createWXAPI(WXEntryActivity.this, PlatformKeyInfo.wechat_AppId, false);
			mIWXAPI.registerApp(PlatformKeyInfo.wechat_AppId);
		}
		mIWXAPI.handleIntent(getIntent(), WXEntryActivity.this);
		shareToWx();
	}

	/**
	 * 分享到微信或朋友圈 当微信没有登陆时，分享会先进入登陆界面，登录后再次启动该activity，
	 * 导致通过Intent传入的ShareData.shareData和pointArr读取都为null 此时在shareToWx不需要做操作
	 */
	protected void shareToWx() {
		if (shareData != null) {
			WXMediaMessage msg = new WXMediaMessage();
			if (shareData.getImagePath() != null) {
				bitmap = BitmapFactory.decodeFile(shareData.getImagePath());
			}
			msg.title = shareData.getTitle();
			msg.description = shareData.getText();

			// bitmap为空时微信分享会没有响应，所以要设置一个默认图片让用户知道
			if (bitmap != null) {
				bmpThum = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
			} else {
				bmpThum = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), BMCore.res.getIdentifier("yt_loadfail", "drawable", BMCore.packName)), 150, 150, true);
			}
			
			if(TextUtils.isEmpty(shareData.getImagePath())
					&& TextUtils.isEmpty(shareData.getImageUrl())
					&& TextUtils.isEmpty(shareData.getTarget_url())){
				// 纯文字分享
				WXTextObject textObject = new WXTextObject();
				textObject.text = shareData.getText();
				msg.mediaObject = textObject;
//				msg.description = shareData.getText();
//				BMLog.d("纯文字分享");
			}else if(!TextUtils.isEmpty(shareData.getImagePath())
					&& TextUtils.isEmpty(shareData.getImageUrl())){
				// 纯图片分享
				WXImageObject imageObject = new WXImageObject();
//				imageObject.imageUrl = "(图片地址)";
				imageObject.imagePath = shareData.getImagePath();
				msg.mediaObject = imageObject;
				msg.setThumbImage(bmpThum);
//				BMLog.d("纯图片分享");
			}else if(!TextUtils.isEmpty(shareData.getImageUrl())
					&& TextUtils.isEmpty(shareData.getTarget_url())){
				// 图文分享
				WXWebpageObject pageObject = new WXWebpageObject();
				pageObject.webpageUrl = shareData.getImageUrl();
				msg.mediaObject = pageObject;
				msg.setThumbImage(bmpThum);
//				BMLog.d("图文分享");
			}else if(!TextUtils.isEmpty(shareData.getTarget_url())
					&& TextUtils.isEmpty(shareData.getImagePath())
					&& TextUtils.isEmpty(shareData.getImageUrl())){
				// 链接分享
				WXWebpageObject pageObject = new WXWebpageObject();
				pageObject.webpageUrl = shareData.getTarget_url();
				msg.mediaObject = pageObject;
//				msg.description = shareData.getText();
//				BMLog.d("链接分享");
			}else {
				msg.setThumbImage(bmpThum);
				WXWebpageObject pageObject = new WXWebpageObject();
				pageObject.webpageUrl = shareData.getTarget_url();
				msg.mediaObject = pageObject;
			}
			
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("测试");
			req.message = msg;
			if (fromShare) {
				if (platform == BMPlatform.PLATFORM_WECHATMOMENTS) {
					req.scene = SendMessageToWX.Req.WXSceneTimeline;
				} else  {
					req.scene = SendMessageToWX.Req.WXSceneSession;
				}
				mIWXAPI.sendReq(req);
			}
		} else {
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handIntent(intent);
	}
	/**
	 * 微信监听分享结果
	 * @param intent
	 */
	public void handIntent(Intent intent) {
		setIntent(intent);
		// 监听分享后的返回结果
		mIWXAPI.handleIntent(intent, this);
	}

	/**
	 * 创建唯一标示
	 * @param type
	 * @return 唯一标示字符串
	 */
	protected String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	@Override
	public void onReq(BaseReq req) {

	}
	
	@Override
	protected void onRestart() {
		Util.dismissDialog();
		finish();
		super.onRestart();
	}
	
	@Override
	protected void onDestroy() {
		shareData = null;
		listener = null;
		super.onDestroy();
	}


	@Override
	/**
	 * 微信分享监听
	 */
	public void onResp(BaseResp response) {
		switch (response.errCode) {
		case BaseResp.ErrCode.ERR_OK:	
			BMLog.d("分享到微信成功"+listener);
			if (platform == BMPlatform.PLATFORM_WECHATMOMENTS) {
//				YtShareListener.sharePoint(this, KeyInfo.youTui_AppKey, ChannelId.WECHATFRIEND, realUrl, !shareData.isAppShare, shortUrl);
				if(listener!=null){		
					ErrorInfo error = new ErrorInfo();
					String errorMessage = response.errStr;
					error.setErrorMessage(errorMessage);
					listener.onSuccess();
				}
			} else {
//				YtShareListener.sharePoint(this, KeyInfo.youTui_AppKey, ChannelId.WECHAT, realUrl, !shareData.isAppShare, shortUrl);
				if(listener!= null){
					ErrorInfo error = new ErrorInfo();
					String errorMessage = response.errStr;
					error.setErrorMessage(errorMessage);
					listener.onSuccess();
				}
			}
			break;
		case BaseResp.ErrCode.ERR_SENT_FAILED:
			BMLog.d("分享到微信失败"+listener);
			if(listener!=null){
				ErrorInfo error = new ErrorInfo();
				String errorMessage = response.errStr;
				error.setErrorMessage(errorMessage);
				listener.onError(error);
			}
			break;
		case BaseResp.ErrCode.ERR_COMM:
			if(listener!=null){
				ErrorInfo error = new ErrorInfo();
				String errorMessage = response.errStr;
				error.setErrorMessage(errorMessage);
				listener.onError(error);
			}		
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			BMLog.d("取消分享到微信"+listener);
			if(listener!=null){
				listener.onCancel();
			}
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			break;
		default:
			break;
		}
		finish();
	}
	
}
