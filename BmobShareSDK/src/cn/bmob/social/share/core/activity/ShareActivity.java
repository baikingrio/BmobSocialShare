package cn.bmob.social.share.core.activity;

import java.io.IOException;

import cn.bmob.social.share.core.BMShareListener;
import cn.bmob.social.share.core.ErrorInfo;
import cn.bmob.social.share.core.data.BMPlatform;
import cn.bmob.social.share.core.data.PlatformKeyInfo;
import cn.bmob.social.share.core.data.ShareData;
import cn.bmob.social.share.core.social.QQOpenShare;
import cn.bmob.social.share.core.social.RennShare;
import cn.bmob.social.share.core.social.SinaShare;
import cn.bmob.social.share.core.social.TencentWbShare;
import cn.bmob.social.share.core.util.BMLog;
import cn.bmob.social.share.core.util.Util;

import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 分享界面
 * 
 * @author youtui
 * @since 14/6/11
 */
public class ShareActivity extends Activity implements IWeiboHandler.Response {
	/** 分享的平台 */
	private BMPlatform platform;
	/** 新浪微博分享操作类 */
	private SinaShare sinaShare;
	/** 新浪微博分享接口 */
	private IWeiboShareAPI iWeiboShareAPI;
	/** 返回按钮id */
	private final int BACK_ID = 140901;
	/** 分享按钮id */
	private final int SHAREBT_ID = 140902;
	/** 人人分享成功 */
	public static final int RENN_SHARE_SUCCESS = 0;
	/** 人人分享错误 */
	public static final int RENN_SHARE_ERROR = 1;
	/** 人人分享网络错误 */
	public static final int RENN_HTTP_ERROR = 2;
	/** 人人分享图片未找到 */
	public static final int RENN_PIC_NOTFOUND = 3;
	/** 待分享数据 */
	public static ShareData shareData;
	/** 分享监听 */
	public static BMShareListener listener;
	/** 短链接 */
	private String shortUrl;
	/** 长连接 */
	private String realUrl;
	/** 处理人人分享回调 */
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 人人分享成功
			case RENN_SHARE_SUCCESS:
//				if (shareData != null) {
//					BMShareListener.sharePoint(ShareActivity.this, PlatformKeyInfo.youTui_AppKey, ChannelId.RENN, realUrl, !shareData.isAppShare, shortUrl);
//				}
				if (listener != null) {
					ErrorInfo error = new ErrorInfo();
					String errorMessage = (String) msg.obj;
					error.setErrorMessage(errorMessage);
					listener.onSuccess();
				}
				ShareActivity.this.finish();
				break;
			/** 处理人人分享错误 */
			case RENN_SHARE_ERROR:
				if (listener != null) {
					ErrorInfo error = new ErrorInfo();
					String errorMessage = (String) msg.obj;
					error.setErrorMessage(errorMessage);
					listener.onError(error);
				}
				ShareActivity.this.finish();
				break;
			/** 处理人人分享网络错误 */
			case RENN_HTTP_ERROR:
				Toast.makeText(ShareActivity.this, "连接到服务器错误...", Toast.LENGTH_SHORT).show();
				ShareActivity.this.finish();
				break;
			/** 处理人人分享图片未找到 */
			case RENN_PIC_NOTFOUND:
				Toast.makeText(ShareActivity.this, "未找到分享图片，请重新设置分享图片路径...", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		doShare();
	}

	/**
	 * 分享操作
	 */
	private void doShare() {
		platform = (BMPlatform) getIntent().getExtras().get("platform");
		shortUrl = getIntent().getExtras().getString("shortUrl");
		realUrl = getIntent().getExtras().getString("realUrl");
		boolean sinaWeiboIsNoKeyShare = getIntent().getExtras().getBoolean("sinaWeiboIsNoKeyShare");

		switch (platform) {
		// 分享到新浪微博
		case PLATFORM_SINAWEIBO:
			if (sinaWeiboIsNoKeyShare) {
				initView("新浪微博", platform);
			} else {
				iWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, PlatformKeyInfo.sinaWeibo_AppKey);
				sinaShare = new SinaShare(ShareActivity.this, shareData);
				sinaShare.shareToSina();
			}
			break;
		// 分享到qq
		case PLATFORM_QQ:
			new QQOpenShare(this, "QQ", listener, shareData).shareToQQ();
			break;
		// 分享到qq空间
		case PLATFORM_QZONE:
			new QQOpenShare(this, "Qzone", listener, shareData).shareToQzone();
			break;
		// 分享到腾讯微博
		case PLATFORM_TENCENTWEIBO:
			initView("腾讯微博", platform);
			break;
		// 分享到人人网
		case PLATFORM_RENN:
			initView("人人网", platform);
			break;
		default:
			break;
		}

	}

	/**
	 * 新浪微博分享完会调用该方法
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		if (platform == BMPlatform.PLATFORM_SINAWEIBO) {
			iWeiboShareAPI.handleWeiboResponse(intent, this);
		}
		super.onNewIntent(intent);
	}

	/**
	 * 新浪分享回调
	 */
	@Override
	public void onResponse(BaseResponse baseResp) {
		switch (baseResp.errCode) {
		// 分享成功
		case WBConstants.ErrorCode.ERR_OK:
//			if (shareData != null) {
//				BMShareListener.sharePoint(this, PlatformKeyInfo.youTui_AppKey, ChannelId.SINAWEIBO, realUrl, !shareData.isAppShare, shortUrl);
//			}
			if (listener != null) {
				ErrorInfo error = new ErrorInfo();
				error.setErrorMessage(baseResp.errMsg);
				listener.onSuccess();
			}
			break;
		// 分享取消
		case WBConstants.ErrorCode.ERR_CANCEL:
			if (listener != null) {
				listener.onCancel();
			}

			break;
		// 分享错误
		case WBConstants.ErrorCode.ERR_FAIL:
			// 新浪微博分享在这里有bug
			if ("auth faild!!!!".equals(baseResp.errMsg)) {
				Toast.makeText(this, "授权失败,请重新获取授权...", Toast.LENGTH_SHORT).show();
				iWeiboShareAPI.registerApp();
			} else {
				if (listener != null) {
					ErrorInfo error = new ErrorInfo();
					error.setErrorMessage(baseResp.errMsg);
					listener.onError(error);
				}
			}
			break;

		default:
			break;
		}

		finish();

	}

	@Override
	protected void onDestroy() {
		// activity摧毁时释放listener
		Util.dismissDialog();
		shareData = null;
		listener = null;
		super.onDestroy();
	}

	/** 腾讯微博和人人以及新浪的无key分享需要自己写分享编辑界面 */
	private void initView(String platformName, final BMPlatform platform) {
		LinearLayout mainLinear = new LinearLayout(this);
		mainLinear.setOrientation(LinearLayout.VERTICAL);
		mainLinear.setBackgroundColor(0xffe9ecff);
		//
		RelativeLayout headerLayout = new RelativeLayout(this);
		RelativeLayout.LayoutParams headerLinearParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, Util.dip2px(this, 50));
		headerLayout.setLayoutParams(headerLinearParams);
		headerLayout.setBackgroundColor(0xff66c0ff);

		// 返回键
		LinearLayout back = new LinearLayout(this);
		RelativeLayout.LayoutParams backParams = new RelativeLayout.LayoutParams(Util.dip2px(this, 50), RelativeLayout.LayoutParams.FILL_PARENT);
		backParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		back.setHorizontalGravity(Gravity.CENTER);
		back.setVerticalGravity(Gravity.CENTER);
		back.setId(BACK_ID);
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ShareActivity.this.finish();

			}

		});
		// 返回键图片
		ImageView backImage = new ImageView(this);
		LayoutParams backImageParams = new LayoutParams(Util.dip2px(this, 20), Util.dip2px(this, 20));
		backImage.setLayoutParams(backImageParams);
		AssetManager asset = getAssets();
		Bitmap backBitmap = null;
		try {
			backBitmap = BitmapFactory.decodeStream(asset.open("yt_left_arrow.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		backImage.setImageBitmap(backBitmap);
		back.addView(backImage);
		// 标题栏
		TextView title = new TextView(this);
		RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
		titleParams.addRule(RelativeLayout.RIGHT_OF, BACK_ID);
		titleParams.addRule(RelativeLayout.LEFT_OF, SHAREBT_ID);
		title.setGravity(Gravity.CENTER_VERTICAL);
		title.setText(platformName);
		title.setTextSize(16);
		title.setTextColor(0xffffffff);

		// 分享按钮
		TextView shareBt = new TextView(this);
		shareBt.setId(SHAREBT_ID);
		RelativeLayout.LayoutParams shareBtParams = new RelativeLayout.LayoutParams(Util.dip2px(this, 50), RelativeLayout.LayoutParams.FILL_PARENT);
		shareBtParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		shareBt.setText("分享");
		shareBt.setGravity(Gravity.CENTER_VERTICAL);
		shareBt.setTextColor(0xffffffff);

		shareBt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (platform == BMPlatform.PLATFORM_RENN) {
					Util.showProgressDialog(ShareActivity.this, "分享中...", true);
					new RennShare(ShareActivity.this, mHandler, listener, shareData).shareToRenn();
				} else if (platform == BMPlatform.PLATFORM_TENCENTWEIBO) {
					Util.showProgressDialog(ShareActivity.this, "分享中...", true);
					new TencentWbShare(ShareActivity.this, listener, shareData).shareToTencentWb();
				} else if (platform == BMPlatform.PLATFORM_SINAWEIBO) {
					// 没有key的情况下进行新浪微博分享
					Util.showProgressDialog(ShareActivity.this, "分享中...", true);
//					SinaNoKeyShare.shareToSina(ShareActivity.this, shareData, listener, realUrl, shortUrl);
				}
			}
		});

		headerLayout.addView(back, backParams);
		headerLayout.addView(title, titleParams);
		headerLayout.addView(shareBt, shareBtParams);
		// 分享内容框
		LinearLayout bodyLayout = new LinearLayout(this);
		bodyLayout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, Util.dip2px(this, 270));
		bodyLayout.setLayoutParams(bodyParams);
		bodyLayout.setBackgroundColor(0xfff4f4f4);
		// 分享的文字
		EditText editText = new EditText(this);
		LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, Util.dip2px(this, 160));
		editParams.setMargins(8, 8, 8, 8);
		editText.setLayoutParams(editParams);
		if (shareData != null && shareData.getText() != null) {
			editText.setText(shareData.getText());
		}
		editText.setGravity(Gravity.TOP);
		editText.setTextColor(0xffa1a1a1);
		editText.setTextSize(13);
		editText.setBackgroundDrawable(null);
		// 分享的图片
		ImageView shareImage = new ImageView(this);
		LinearLayout.LayoutParams shareImageParams = new LinearLayout.LayoutParams(Util.dip2px(this, 100), Util.dip2px(this, 100));
		shareImageParams.setMargins(8, 0, 8, 8);
		shareImage.setLayoutParams(shareImageParams);
		if (shareData != null && shareData.getImagePath() != null) {
			Bitmap imageBit = BitmapFactory.decodeFile(shareData.getImagePath());
			BitmapDrawable bitDraw = new BitmapDrawable(imageBit);
			shareImage.setBackgroundDrawable(bitDraw);
		}

		bodyLayout.addView(editText);
		bodyLayout.addView(shareImage);

		mainLinear.addView(headerLayout, headerLinearParams);
		mainLinear.addView(bodyLayout, bodyParams);

		setContentView(mainLinear);
	}

	@Override
	protected void onRestart() {
		Util.dismissDialog();
		finish();
		super.onRestart();
	}

}
