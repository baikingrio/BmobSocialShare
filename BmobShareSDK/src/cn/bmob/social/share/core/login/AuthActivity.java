package cn.bmob.social.share.core.login;

import java.io.IOException;
import java.lang.reflect.Method;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.bmob.social.share.core.data.PlatformKeyInfo;
import cn.bmob.social.share.core.util.AccessTokenKeeper;
import cn.bmob.social.share.core.util.BMLog;
import cn.bmob.social.share.core.util.PlatformAppHelper;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.tencent.weibo.sdk.android.api.util.BackGroudSeletor;
import com.tencent.weibo.sdk.android.api.util.Util;

/**
 * 授权登录Activity 
 * @author youtui
 * @since 14/4/26
 */
public final class AuthActivity extends Activity {
	/** 新浪accessToken */
	private Oauth2AccessToken oauth2AccessToken;
	/** 新浪微博授权类 */
	private WeiboAuth mWeiboAuth;
	/** 标示授权的平台 */
	private String flag;
	/** 授权网页 */
	private WebView webView;
	/** 授权网页状态 */
	public static int WEBVIEWSTATE_1 = 0;
	/** 授权网站地址 */
	private String path;
	/** a授权提醒diaolog */
	private Dialog _dialog;
	/** 让dialog显示加载进度 */
	public static final int PROGRESS_H = 3;
	/** 让dialog显示网络连接情况 */
	public static final int ALERT_NETWORK = 4;
	/** 获取腾讯微博用户信息 */
	public static final int GET_USERINFO_TENCENTWB = 5;
	/** 腾讯微博授权页面布局 */
	private LinearLayout layout = null;
	/** 新浪微博授权回调页 */
	private String redirectUri = null;
	/** 新浪微博授权id */
	private String clientId = null;
	/** 腾讯授权qq类 */
	private Tencent mTencent;
	/** 新浪微博sso授权类 */
	private SsoHandler mSsoHandler;
	/** 腾讯微博accessToken */
	private String tencentWbAccessToken;
	/** 腾讯微博openId */
	private String tencentWbOpenid;
	/** 腾讯微博用户名 */
	private String tencentWbname;
	/** 授权监听 */
	public static AuthListener authListener;
	/**新浪微博授权成功*/
	private static final int SINA_AUTH_SUCCESS = 0;
	/** 新浪微博授权失败 */
	private static final int SINA_AUTH_FAIL = 1;
	//private static final int SINA_AUTH_CANCLE = 2;
	/**QQ授权成功 */
	private static final int QQ_AUTH_SUCCESS = 3;
	//private static final int QQ_AUTH_FAIL = 4;
	//private static final int QQ_AUTH_CANCEL = 5;
	/** 腾讯微博授权成功 */
	private static final int TENCENTWB_AUTH_SUCCESS = 6;
	//private static final int TENCENTWB_AUTH_CANCEL = 7;
	//private static final int TENCENTWB_FAIL = 8;
	/**提醒线程停止sleep*/
	private static final int STOP_SLEEP = 101;
	/**处理分享结果*/
	private Handler mHandler = new Handler() {
		AuthUserInfo userInfo = new AuthUserInfo();

		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 新浪授权成功操作
			case SINA_AUTH_SUCCESS:
				String str = (String) msg.obj;

				try {
					/** 解析获取的用户信息并赋值给保存用户信息的字段 */
					JSONObject sinaJson = new JSONObject(str);
					userInfo.setSinaUid(sinaJson.getString("id"));
					userInfo.setSinaScreenname(sinaJson.getString("screen_name"));
					userInfo.setSinaProfileImageUrl(sinaJson.getString("profile_image_url"));
					if (sinaJson.getString("gender").equals("m")) {
						userInfo.setSinaGender("男");
					} else {
						userInfo.setSinaGender("女");
					}

					userInfo.setSinaName(sinaJson.getString("name"));
					cn.bmob.social.share.core.util.Util.showProgressDialog(AuthActivity.this, "加载中...", true);
					// 跳转的时候等待2秒
					new Thread() {
						public void run() {
							try {
								sleep(1500);
								sendEmptyMessage(STOP_SLEEP);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						};
					}.start();
				} catch (JSONException e) {
					BMLog.e("sinaUesrInfo JSONException : "+((JSONObject) msg.obj).toString());
					if (authListener != null) {
						authListener.onAuthFail(AuthActivity.this);
					}
					e.printStackTrace();
					AuthActivity.this.finish();
				}
				break;
			// qq授权成功操作
			case QQ_AUTH_SUCCESS:
				JSONObject json = (JSONObject) msg.obj;
				userInfo.setQqOpenid(mTencent.getQQToken().getOpenId());
				try {
					userInfo.setQqNickName(json.getString("nickname"));
					userInfo.setQqImageUrl(json.getString("figureurl_qq_1"));
					userInfo.setQqGender(json.getString("gender"));
					new Thread() {
						public void run() {
							try {
								sleep(1500);
								sendEmptyMessage(STOP_SLEEP);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						};
					}.start();
				} catch (JSONException e) {
					BMLog.e("sinaUesrInfo JSONException : "+((JSONObject) msg.obj).toString());
					if (authListener != null) {
						authListener.onAuthFail(AuthActivity.this);
					}
					e.printStackTrace();
					AuthActivity.this.finish();
				}
				break;
			// 腾讯微博授权成功操作
			case TENCENTWB_AUTH_SUCCESS:
				JSONObject tencentWbJson = (JSONObject) msg.obj;
				try {
					userInfo.setTencentWbHead(tencentWbJson.getString("head"));
					userInfo.setTencentWbNick(tencentWbJson.getString("nick"));
					userInfo.setTencentWbName(tencentWbname);
					userInfo.setTencentWbOpenid(tencentWbOpenid);
					userInfo.setTencentWbGender(tencentWbJson.getString("sex"));
					if (tencentWbJson.getString("sex").equals("1")) {
						userInfo.setTencentWbGender("男");
					} else {
						userInfo.setTencentWbGender("女");
					}
					cn.bmob.social.share.core.util.Util.showProgressDialog(AuthActivity.this, "加载中...", true);
					new Thread() {
						public void run() {
							try {
								sleep(2000);
								sendEmptyMessage(STOP_SLEEP);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						};
					}.start();
				} catch (JSONException e) {
					e.printStackTrace();
					AuthActivity.this.finish();
				}
				break;
			case STOP_SLEEP:
				/** 避免用户在授权等待时取消操作造成错误，添加以下判断 */
				if (userInfo != null && authListener != null) {
					Toast.makeText(AuthActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
					authListener.onAuthSucess(AuthActivity.this, userInfo);
				} else {
					Toast.makeText(AuthActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
				}
				cn.bmob.social.share.core.util.Util.dismissDialog();
				AuthActivity.this.finish();
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

		initData();
	}

	/**
	 * 判断授权的平台
	 */
	private void initData() {
		flag = getIntent().getExtras().getString("flag");
		if ("sina".equals(flag)) {
			mWeiboAuth = new WeiboAuth(this, PlatformKeyInfo.sinaWeibo_AppKey, PlatformKeyInfo.sinaWeibo_RedirectUrl, PlatformAppHelper.SINA_WEIBO_SCOPE);
			if (PlatformAppHelper.isSinaWeiboExisted(this)) {
				mSsoHandler = new SsoHandler(this, mWeiboAuth);
				mSsoHandler.authorize(new SinaAuthListener());
			} else {
				mWeiboAuth.anthorize(new SinaAuthListener());
			}
		} else if ("tencentWb".equals(flag)) {
			initTencentWb();
		} else if ("tencentWbShare".equals(flag)) {
			initTencentWb();
		} else if ("qq".equals(flag)) {
			initQQ();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * qq授权
	 */
	private void initQQ() {
		mTencent = Tencent.createInstance(PlatformKeyInfo.qQ_AppId, this);
		mTencent.logout(this);
		mTencent.login(this, "all", listener);
	}

	/**
	 * qq授权监听
	 */
	IUiListener listener = new IUiListener() {

		@Override
		public void onCancel() {
			Toast.makeText(AuthActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
			if (authListener != null) {
				authListener.onAuthCancel(AuthActivity.this);
			}
			AuthActivity.this.finish();
		}

		@Override
		public void onComplete(Object obj) {
			UserInfo info = new UserInfo(AuthActivity.this, mTencent.getQQToken());
			cn.bmob.social.share.core.util.Util.showProgressDialog(AuthActivity.this, "授权中...", true);
			info.getUserInfo(getInfoListener);
		}

		@Override
		public void onError(UiError arg0) {
			Toast.makeText(AuthActivity.this, "授权错误", Toast.LENGTH_SHORT).show();
			if (authListener != null) {
				authListener.onAuthFail(AuthActivity.this);
			}
			AuthActivity.this.finish();
		}

	};

	/**
	 * 获取QQ用户信息监听
	 */
	private IUiListener getInfoListener = new IUiListener() {

		@Override
		public void onCancel() {
			if (authListener != null) {
				authListener.onAuthFail(AuthActivity.this);
			}
			AuthActivity.this.finish();
		}

		@Override
		public void onComplete(Object obj) {
			Message msg = Message.obtain();
			msg.what = QQ_AUTH_SUCCESS;
			msg.obj = obj;
			mHandler.sendMessage(msg);
		}

		@Override
		public void onError(UiError arg0) {
			if (authListener != null) {
				authListener.onAuthFail(AuthActivity.this);
			}
			AuthActivity.this.finish();
		}

	};

	/**
	 * 腾讯微博授权
	 */
	@SuppressWarnings("deprecation")
	private void initTencentWb() {
		cn.bmob.social.share.core.util.Util.showProgressDialog(this, "加载中...", true);
		if (!Util.isNetworkAvailable(this)) {
			this.showDialog(ALERT_NETWORK);
		} else {
			DisplayMetrics displaysMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);
			String pix = displaysMetrics.widthPixels + "x" + displaysMetrics.heightPixels;
			BackGroudSeletor.setPix(pix);

			try {
				clientId = PlatformKeyInfo.tencentWeibo_AppKey;
				redirectUri = PlatformKeyInfo.tencentWeibo_RedirectUrl;
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
				int state = (int) Math.random() * 1000 + 111;
				path = "https://open.t.qq.com/cgi-bin/oauth2/authorize?client_id=" + clientId + "&response_type=token&redirect_uri=" + redirectUri + "&state=" + state;
				this.initLayout();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 初始化腾讯微博授权界面，并设置相应监听
	 * */
	@SuppressWarnings("deprecation")
	public void initLayout() {
		RelativeLayout.LayoutParams fillParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);

		layout = new LinearLayout(this);
		layout.setLayoutParams(fillParams);
		layout.setOrientation(LinearLayout.VERTICAL);
		webView = new WebView(this);

		if (Build.VERSION.SDK_INT >= 11) {
			Class<?>[] name = new Class<?>[] { String.class };
			Object[] rmMethodName = new Object[] { "searchBoxJavaBridge_" };
			Method rji;
			try {
				rji = webView.getClass().getDeclaredMethod("removeJavascriptInterface", name);
				rji.invoke(webView, rmMethodName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		webView.setLayoutParams(fillParams);
		WebSettings webSettings = webView.getSettings();
		webView.setVerticalScrollBarEnabled(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(false);
		webView.loadUrl(path);
		webView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				if (newProgress >= 90) {
					cn.bmob.social.share.core.util.Util.dismissDialog();
				}
			}

		});
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.indexOf("access_token") != -1) {
					jumpResultParser(url);
					webView.setVisibility(View.INVISIBLE);
					webView.destroy();
				}
				return false;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {

				if (url.indexOf("access_token") != -1) {
					jumpResultParser(url);
					webView.setVisibility(View.INVISIBLE);
					webView.destroy();
				}
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				if (url.indexOf("access_token") != -1) {
					jumpResultParser(url);
					webView.setVisibility(View.INVISIBLE);
					webView.destroy();
				}
				super.onPageFinished(view, url);
			}
		});
		layout.addView(webView);
		this.setContentView(layout);
	}

	/**
	 * 获取腾讯微博授权后的返回地址，并对其进行解析
	 */
	public void jumpResultParser(String result) {

		String resultParam = result.split("#")[1];
		String params[] = resultParam.split("&");
		String accessToken = params[0].split("=")[1];
		String expiresIn = params[1].split("=")[1];
		String openid = params[2].split("=")[1];
		String openkey = params[3].split("=")[1];
		String refreshToken = params[4].split("=")[1];
		String state = params[5].split("=")[1];
		String name = params[6].split("=")[1];
		String nick = params[7].split("=")[1];
		Context context = this.getApplicationContext();
		if (accessToken != null && !"".equals(accessToken)) {
			Util.saveSharePersistent(context, "ACCESS_TOKEN", accessToken);
			tencentWbAccessToken = accessToken;
			Util.saveSharePersistent(context, "EXPIRES_IN", expiresIn);
			Util.saveSharePersistent(context, "OPEN_ID", openid);
			tencentWbOpenid = openid;
			Util.saveSharePersistent(context, "OPEN_KEY", openkey);
			Util.saveSharePersistent(context, "REFRESH_TOKEN", refreshToken);
			Util.saveSharePersistent(context, "NAME", name);
			tencentWbname = name;
			Util.saveSharePersistent(context, "NICK", nick);
			Util.saveSharePersistent(context, "CLIENT_ID", clientId);
			Util.saveSharePersistent(context, "AUTHORIZETIME", String.valueOf(System.currentTimeMillis() / 1000l));
			if ("tencentWb".equals(flag)) {
				handle.sendEmptyMessage(GET_USERINFO_TENCENTWB);
			}
		} else {
			if (authListener != null) {
				authListener.onAuthFail(AuthActivity.this);
			}
			AuthActivity.this.finish();
		}
	}

	Handler handle = new Handler() {
		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 100:
				AuthActivity.this.showDialog(ALERT_NETWORK);
				break;
			case GET_USERINFO_TENCENTWB:
				// 获取腾讯微博的用户信息
				new Thread() {
					public void run() {
						String url = "http://open.t.qq.com/api/user/info?format=json" + "&openid=" + tencentWbOpenid + "&oauth_consumer_key=" + PlatformKeyInfo.tencentWeibo_AppKey + "&access_token=" + tencentWbAccessToken + "&clientip=" + Util.getLocalIPAddress(AuthActivity.this) + "&oauth_version=2.a&scope=" + PlatformAppHelper.TENCENT_SCOPE;
						HttpClient client = new DefaultHttpClient();
						HttpGet get = new HttpGet(url);
						try {

							HttpResponse resp = client.execute(get);
							String str = EntityUtils.toString(resp.getEntity());
							JSONObject respJson = new JSONObject(str);
							if (Integer.valueOf(respJson.getString("ret")) != 0) {
							} else {
								JSONObject dataJson = respJson.getJSONObject("data");
								Message msg = Message.obtain();
								msg.what = TENCENTWB_AUTH_SUCCESS;
								msg.obj = dataJson;
								mHandler.sendMessage(msg);
							}
						} catch (ClientProtocolException e) {
							e.printStackTrace();
							if (authListener != null) {
								authListener.onAuthFail(AuthActivity.this);
							}
						} catch (IOException e) {
							e.printStackTrace();
							if (authListener != null) {
								authListener.onAuthFail(AuthActivity.this);
							}
						} catch (JSONException e) {
							e.printStackTrace();
							if (authListener != null) {
								authListener.onAuthFail(AuthActivity.this);
							}
						}
					};
				}.start();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 腾讯微博授权提醒对话框
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {

		case PROGRESS_H:
			_dialog = new ProgressDialog(this);
			((ProgressDialog) _dialog).setMessage("加载中...");
			break;
		case ALERT_NETWORK:
			AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
			builder2.setTitle("网络连接异常，是否重新连接？");
			builder2.setPositiveButton("是", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (Util.isNetworkAvailable(AuthActivity.this)) {
						webView.loadUrl(path);
					} else {
						Message msg = Message.obtain();
						msg.what = 100;
						handle.sendMessage(msg);
					}
				}

			});
			builder2.setNegativeButton("否", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					AuthActivity.this.finish();
				}
			});
			_dialog = builder2.create();
			break;
		}
		return _dialog;
	}

	/**
	 * @author gaopan 新浪微博授权监听
	 */
	class SinaAuthListener implements WeiboAuthListener {
		@Override
		public void onCancel() {
			Toast.makeText(AuthActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
			if (authListener != null) {
				authListener.onAuthCancel(AuthActivity.this);
			}
			AuthActivity.this.finish();
		}

		@Override
		public void onComplete(Bundle bundle) {

			oauth2AccessToken = Oauth2AccessToken.parseAccessToken(bundle);
			if (oauth2AccessToken.isSessionValid()) {
				AccessTokenKeeper.writeAccessToken(AuthActivity.this, oauth2AccessToken);
			}

			/** 获取新浪微博用户信息 */
			new Thread() {
				public void run() {
					HttpClient client = new DefaultHttpClient();
					String url = "https://api.weibo.com/2/users/show.json";
					url += "?" + "access_token=" + oauth2AccessToken.getToken();
					url += "&" + "uid=" + oauth2AccessToken.getUid();
					HttpGet get = new HttpGet(url);
					try {
						HttpResponse resp = client.execute(get);
						String str = EntityUtils.toString(resp.getEntity());
						Message msg = Message.obtain();
						msg.what = SINA_AUTH_SUCCESS;
						msg.obj = str;
						mHandler.sendMessage(msg);
					} catch (ClientProtocolException e) {
						mHandler.sendEmptyMessage(SINA_AUTH_FAIL);
						e.printStackTrace();
					} catch (IOException e) {
						mHandler.sendEmptyMessage(SINA_AUTH_FAIL);
						e.printStackTrace();
					}
				};
			}.start();

		}

		@Override
		public void onWeiboException(WeiboException arg0) {
			Toast.makeText(AuthActivity.this, "授权错误", Toast.LENGTH_SHORT).show();
			if (authListener != null) {
				authListener.onAuthFail(AuthActivity.this);
			}
			AuthActivity.this.finish();
		}
	}

	@Override
	protected void onDestroy() {
		cn.bmob.social.share.core.util.Util.dismissDialog();
		authListener = null;
		super.onDestroy();
	}

}
