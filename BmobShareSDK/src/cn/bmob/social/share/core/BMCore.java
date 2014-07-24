package cn.bmob.social.share.core;

import java.io.File;
import java.io.IOException;

import cn.bmob.social.share.core.activity.ShareActivity;
import cn.bmob.social.share.core.data.BMPlatform;
import cn.bmob.social.share.core.data.PlatformKeyInfo;
import cn.bmob.social.share.core.data.ShareData;
import cn.bmob.social.share.core.login.AuthListener;
import cn.bmob.social.share.core.login.AuthLogin;
import cn.bmob.social.share.core.login.AuthUserInfo;
import cn.bmob.social.share.core.social.OtherShare;
import cn.bmob.social.share.core.util.AccessTokenKeeper;
import cn.bmob.social.share.core.util.BMLog;
import cn.bmob.social.share.core.util.DownloadImage;
import cn.bmob.social.share.core.util.PlatformAppHelper;
import cn.bmob.social.share.core.util.Util;
import cn.bmob.social.share.core.wxapi.WXEntryActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * 分享操作类
 * @author 稻草人
 * @date 2014年7月18日 下午3:58:05
 *
 */
public class BMCore {
	/**应用包名*/
	public static String packName;
	/**应用资源*/
	public static Resources res;
	/**应用AppContent*/
	public static Context appContext;
	/**实例*/
	public static BMCore bmCore;
	/** 获取应用分享分享信息成功 */
	private final int GET_APPSHAREDATA_SUCCESS = 0;
	/** 获取应用分享内容失败 */
	private final int GET_APPSHAREDATA_FAIL = 1;
	/** 获取内容分享信息成功 */
	private final int GET_CONTENTSHAREDATA_SUCCESS = 3;
	/** 获取内容分享内容失败 */
	private final int GET_CONTENTSHAREDATA_FAIL = 4;
	/**传入的activity*/
	private Activity act;
	/**分享平台*/
	private BMPlatform platform;
	/**分享监听*/
	private BMShareListener listener;
	/**处理获取待分享信息后的操作,获取到待分享信息就进行分享,没有获取到待分享信息则提醒用户*/
	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Util.dismissDialog();
			switch (msg.what) {
			//获取应用分享信息成功后进行分享操作
			case GET_APPSHAREDATA_SUCCESS:
				ShareData shareData = (ShareData) msg.obj;
				doShare(act, platform, listener,shareData);
				break;
				//获取应用分享信息失败,提醒用户
			case GET_APPSHAREDATA_FAIL:
				Toast.makeText(appContext, "获取分享内容失败,请检查分享的网络地址和网络连接情况...", Toast.LENGTH_SHORT).show();
				break;
				//获取内容分享信息成功,进行分享操作
			case GET_CONTENTSHAREDATA_SUCCESS:
				ShareData contentShareData = (ShareData) msg.obj;
				doShare(act, platform, listener,contentShareData);
				break;
				//获取内容分享信息失败,提醒用户
			case GET_CONTENTSHAREDATA_FAIL:
				Toast.makeText(appContext, "获取分享内容失败,请检查分享的网络地址和网络连接情况...", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		};
	};
	
	/** 获取友推sdk的实例 */
	public static BMCore getInstance() {
		if (bmCore == null) {
			bmCore = new BMCore();
		}
		return bmCore;
	}
	
	/** ytcore初始化操作 */
	public static void init(final Context context) {
		// 读取手机信息
		getPhoneInfo(context);
	}
	
	/** 读取手机和应用信息 */
	private static void getPhoneInfo(Context context) {
		packName = context.getPackageName();
		res = context.getResources();
		appContext = context.getApplicationContext();
	}
	
	/** 分享到社交平台 */
	public void share(Activity act, BMPlatform platform, BMShareListener listener,final ShareData shareData) {
		// 分享前操作
		this.act = act;
		this.platform = platform;
		this.listener = listener;
		
		Util.showProgressDialog(act, "获取分享数据中...", false);
		if (listener != null) {
			listener.onPreShare();
		}
		// 获取分享信息
		new Thread() {
			public void run() {
				getShareData(shareData);
			};
		}.start();
	}
	
	/** 跳转到分享页面 */
	private void doShare(Activity act, final BMPlatform platform, final BMShareListener listener,final ShareData shareData) {
		String shortUrl = null ;
		final String realUrl = shareData.getTarget_url();
		if (!shareData.isAppShare && shareData.getTarget_url() != null && !shareData.getTarget_url().equals("")) {
			// 如果是应用分享，则从服务端获取应用分享信息
//			shortUrl = CMyEncrypt.shortUrl(shareData.getTarget_url())[0];
//			sendUrl(KeyInfo.youTui_AppKey, platform.getChannleId(), shareData.getTarget_url(), !shareData.isAppShare, shortUrl);
		}

		// 处理链接
//		dealWithUrl(platform.getChannleId(), shortUrl,shareData);
		if (platform == BMPlatform.PLATFORM_WECHAT || platform == BMPlatform.PLATFORM_WECHATMOMENTS) {
			// 微信和朋友圈
			if (PlatformAppHelper.isWeixinExisted(act)) {
				try {
					Intent it = new Intent(act, Class.forName(packName + ".wxapi.WXEntryActivity"));
					WXEntryActivity.listener = listener;
					it.putExtra("platform", platform);
					it.putExtra("fromShare", true);
					it.putExtra("shortUrl", shortUrl);
					it.putExtra("realUrl", realUrl);
					WXEntryActivity.shareData = shareData;
					act.startActivity(it);
				} catch (ClassNotFoundException e) {
					BMLog.e(packName + ".wxapi.WXEntryActivity cann't be found");
					e.printStackTrace();
				}
			} else {
				Toast.makeText(act, "未安装微信。。。", Toast.LENGTH_SHORT).show();
			}

		} else if (platform == BMPlatform.PLATFORM_EMAIL) {
			//分享到Email
			if(shareData.getTarget_url()!=null){
				new OtherShare(act).sendMail(shareData.getText()+shareData.getTarget_url());
			}else{
				new OtherShare(act).sendMail(shareData.getText());
			}			
		} else if (platform == BMPlatform.PLATFORM_MESSAGE) {
			//分享到短信
			if(shareData.getTarget_url()!=null){
				new OtherShare(act).sendSMS(shareData.getText()+shareData.getTarget_url());
			}else{
				new OtherShare(act).sendSMS(shareData.getText());
			}		
		} else if (platform == BMPlatform.PLATFORM_MORE_SHARE) {
			//更多分享
			moreShare(shareData);
		} else if (platform == BMPlatform.PLATFORM_TENCENTWEIBO) {
			//finalShortUrl用于传递shortUrl
			final String finalShortUrl = shortUrl;
			//分享到腾讯微博
			if (AccessTokenKeeper.isTencentWbAuthExpired(act)) {
				//如果腾讯微博授权过期,先获取授权
				AuthLogin tencentWbLogin = new AuthLogin();
				AuthListener tencentWbListener = new AuthListener() {
					@Override
					public void onAuthSucess(Activity act, AuthUserInfo userInfo) {
						Intent qqWBIt = new Intent(act, ShareActivity.class);
						qqWBIt.putExtra("platform", platform);
						ShareActivity.shareData = shareData;
						WXEntryActivity.listener = listener;
						qqWBIt.putExtra("shortUrl", finalShortUrl);
						qqWBIt.putExtra("realUrl", realUrl);
						act.startActivityForResult(qqWBIt, PlatformKeyInfo.tencentWeiboIndex);
					}

					@Override
					public void onAuthFail(Activity act) {
						Toast.makeText(act, "授权失败...", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onAuthCancel(Activity act) {
						Toast.makeText(act, "授权取消...", Toast.LENGTH_SHORT).show();
					}

				};

				tencentWbLogin.tencentWbAuth(act, tencentWbListener);
			} else {
				//如果已授权,进行分享
				Intent it = new Intent(act, ShareActivity.class);
				ShareActivity.listener = listener;
				it.putExtra("platform", platform);
				it.putExtra("shortUrl", shortUrl);
				it.putExtra("realUrl", realUrl);
				ShareActivity.shareData = shareData;
				act.startActivity(it);
			}

		} else if (platform == BMPlatform.PLATFORM_QQ || platform == BMPlatform.PLATFORM_QZONE) {
			//分享到qq和qq空间 
			if(PlatformAppHelper.isTencentQQExisted(act)){
				Intent it = new Intent(act, ShareActivity.class);
				ShareActivity.listener = listener;
				it.putExtra("platform", platform);
				it.putExtra("shortUrl", shortUrl);
				it.putExtra("realUrl", realUrl);
				ShareActivity.shareData = shareData;
				act.startActivity(it);
			}else{
				Toast.makeText(act, "未安装QQ。。。", Toast.LENGTH_SHORT).show();
			}

		} else if (platform == BMPlatform.PLATFORM_SINAWEIBO) {
			//分享到新浪微博
			if(PlatformAppHelper.isSinaWeiboExisted(act)){
				Intent it = new Intent(act, ShareActivity.class);
				ShareActivity.listener = listener;
				it.putExtra("platform", platform);
				it.putExtra("shortUrl", shortUrl);
				it.putExtra("realUrl", realUrl);
				ShareActivity.shareData = shareData;
				act.startActivity(it);
			}else{
				Toast.makeText(act, "未安装新浪微博。。。", Toast.LENGTH_SHORT).show();
				//如果已授权,进行分享
				Intent it = new Intent(act, ShareActivity.class);
				ShareActivity.listener = listener;
				it.putExtra("platform", platform);
				it.putExtra("shortUrl", shortUrl);
				it.putExtra("realUrl", realUrl);
				ShareActivity.shareData = shareData;
				act.startActivity(it);
			}
			
		} else if (platform == BMPlatform.PLATFORM_RENN) {
			//分享到人人网
			if(PlatformAppHelper.isRenrenExisted(act)){
				Intent it = new Intent(act, ShareActivity.class);
				ShareActivity.listener = listener;
				it.putExtra("platform", platform);
				it.putExtra("shortUrl", shortUrl);
				it.putExtra("realUrl", realUrl);
				ShareActivity.shareData = shareData;
				act.startActivity(it);
			}else{
				Toast.makeText(act, "未安装人人网。。。", Toast.LENGTH_SHORT).show();
			}
		}else if(platform == BMPlatform.PLATFORM_COPYLINK){
			//复制链接 
			if(shareData.getTarget_url()!=null){
				Util.copyLink(mHandler, act, shareData.getTarget_url());
			}		
		}
	}
	
	/** 获取分享信息 */
	private void getShareData(ShareData shareData) {
		Log.d("bmob", "isAppShare = "+shareData.isAppShare);
		if (shareData.isAppShare) {
			//如果是应用分享，则从服务器获取预先设置好的分享内容
//			getAppShareData(shareData);
		} else {
			// 如果是内容分享,设置了网络图片,则下载到本地再进行分享,并将imagePath设为下载到本地的图片路径
			if(!TextUtils.isEmpty(shareData.getImageUrl())){
				String picPath = shareData.getImageUrl().substring(shareData.getImageUrl().lastIndexOf("/") + 1, shareData.getImageUrl().length());
				try {
					DownloadImage.down_file(shareData.getImageUrl(), DownloadImage.FILE_SAVE_PATH, picPath);
					shareData.setImagePath(Environment.getExternalStorageDirectory() + DownloadImage.FILE_SAVE_PATH + picPath);
					BMLog.d("BMCore:" + "网络图片保存到本地的路径: "+shareData.getImagePath());
				} catch (IOException e) {
					mHandler.sendEmptyMessage(GET_CONTENTSHAREDATA_FAIL);
					e.printStackTrace();
					return;
				}
			}
			Message msg = Message.obtain(mHandler, GET_CONTENTSHAREDATA_SUCCESS, shareData);
			mHandler.sendMessage(msg);
		}
	}
	
	/** 系统分享 */
	private void moreShare(ShareData shareData) {
		Intent it = new Intent(Intent.ACTION_SEND);
		it.setType("image/*");
		if (shareData.getImagePath() != null) {
			File file = new File(shareData.getImagePath());
			Uri uri = Uri.fromFile(file);
			it.putExtra(Intent.EXTRA_STREAM, uri);
		}
		it.putExtra(Intent.EXTRA_SUBJECT, shareData.getTitle());
		it.putExtra(Intent.EXTRA_TEXT, shareData.getText());
		it.putExtra(Intent.EXTRA_TITLE, shareData.getTitle());
		act.startActivity(Intent.createChooser(it,shareData.getTitle()));
	}
	
}
