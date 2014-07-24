package cn.bmob.social.share.core.social;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;
import cn.bmob.social.share.core.BMShareListener;
import cn.bmob.social.share.core.activity.ShareActivity;
import cn.bmob.social.share.core.data.PlatformKeyInfo;
import cn.bmob.social.share.core.data.ShareData;
import cn.bmob.social.share.core.util.PlatformAppHelper;
import cn.bmob.social.share.core.util.Util;

import com.renn.rennsdk.RennClient;
import com.renn.rennsdk.RennClient.LoginListener;
import com.renn.rennsdk.RennExecutor.CallBack;
import com.renn.rennsdk.RennResponse;
import com.renn.rennsdk.exception.RennException;
import com.renn.rennsdk.param.PutFeedParam;

/**
 * 人人分享和回调
 * @author youtui
 * @since 14/6/19
 */
public class RennShare {
	/**传入的activity*/
	private Activity act;
	/**人人分享接口*/
	private RennClient client;
	/**传入的Handler，用于人人分享结果监听*/
	private Handler handler;
	/**待分享数据*/
	private ShareData shareData;

	public RennShare(Activity act,Handler handler,BMShareListener listener,ShareData shareData) {
		this.act = act;
		this.handler = handler;
		this.shareData = shareData;
	}

	/**
	 * 分享到人人
	 * 
	 * @throws IOException
	 * @throws HttpException
	 */

	public void shareToRenn() {
		client = RennClient.getInstance(act);
		client.init(PlatformKeyInfo.renren_AppId, PlatformKeyInfo.renren_AppKey, PlatformKeyInfo.renren_SecretKey);

		client.setScope(PlatformAppHelper.RENREN_SCOPE);
		client.setLoginListener(new RennLoginListener());

		if (!client.isLogin()) {
			client.login(act);
			return;
		}
		doShare();
	}
	/**
	 * 分享到人人,分享文字过多时进行剪裁
	 */
	private void doShare() {
		String text = shareData.getText();

		if(text.length()>110){
			text = text.substring(0, 109);
			text += "...";
		}
		
		text += shareData.getTarget_url();
		
		if(TextUtils.isEmpty(shareData.getImagePath())){
			try {
				test();
			} catch (RennException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			doRennShare(text, client);
		}
	}
	
	private void test() throws RennException{
		PutFeedParam param = new PutFeedParam();
		param.setTitle(shareData.getTitle());
		param.setMessage(shareData.getText());
		param.setDescription(shareData.getDescription());
		param.setTargetUrl("http://www.56.com/u72/v_OTAyNTkxMDk.html");
		
		client.getRennService().sendAsynRequest(param, new CallBack() {
						@Override
						public void onSuccess(RennResponse response){
							// TODO Auto-generated method stub
							Message successMsg = Message.obtain(handler, ShareActivity.RENN_SHARE_SUCCESS, response.toString());
							handler.sendMessage(successMsg);
						}
						@Override
						public void onFailed(String errorCode, String errorMessage) {
							// TODO Auto-generated method stub
							Message msg = Message.obtain();
							msg.what=ShareActivity.RENN_SHARE_ERROR;
							msg.obj = errorMessage;
							handler.sendMessage(msg);
						}

					});
	}
	
	
	/**
	 * 分享到人人操作
	 * @param text
	 * @param client
	 */
	private void doRennShare(final String text, final RennClient client) {
		new Thread() {
			public void run() {
				try {
					PostMethod post = new PostMethod("https://api.renren.com/v2/photo/upload");
					//设置图片描述
					StringPart description = new StringPart("description",text,"utf-8");
					//设置access_token
					StringPart access_token = new StringPart("access_token", client.getAccessToken().accessToken,"utf-8");
					File file = null;
					FilePart filePart = null;
					if (shareData.getImagePath() != null) {
						file = new File(shareData.getImagePath());
						//设置图片
						filePart = new FilePart("file", file);

					}
					HttpClient httpClient = new HttpClient();
					Part[] parts = { description, filePart, access_token };
					MultipartRequestEntity mulEntity = new MultipartRequestEntity(parts, post.getParams());			

					post.setRequestEntity(mulEntity);
					httpClient.executeMethod(post);
					Message msg = Message.obtain();
					String respJson = post.getResponseBodyAsString();
					if(respJson.startsWith("{\"response\"")){
						Message successMsg = Message.obtain(handler, ShareActivity.RENN_SHARE_SUCCESS, respJson);
						handler.sendMessage(successMsg);
					}else if(respJson.startsWith("{\"error\"")){
						
						//发动到UI线程处理
						msg.what=ShareActivity.RENN_SHARE_ERROR;
						msg.obj = post.getResponseBodyAsString();
						handler.sendMessage(msg);
					}
					//Log.i("--rennShare http--", post.getResponseBodyAsString());
					
				} catch (FileNotFoundException e) {
					handler.sendEmptyMessage(ShareActivity.RENN_PIC_NOTFOUND);
					Util.dismissDialog();
					e.printStackTrace();
				} catch (HttpException e) {
					handler.sendEmptyMessage(ShareActivity.RENN_HTTP_ERROR);
					e.printStackTrace();
				} catch (IOException e) {
					handler.sendEmptyMessage(ShareActivity.RENN_HTTP_ERROR);
					e.printStackTrace();
				}
			};

		}.start();
	}
	/**
	 * 登录回调
	 * 
	 */
	class RennLoginListener implements LoginListener {

		@Override
		public void onLoginCanceled() {
			Toast.makeText(act, "登录取消", Toast.LENGTH_SHORT).show();
			act.finish();
		}

		@Override
		public void onLoginSuccess() {
			Toast.makeText(act, "登录成功,请再点击进行分享", Toast.LENGTH_SHORT).show();
			act.finish();
		}

	}

}
