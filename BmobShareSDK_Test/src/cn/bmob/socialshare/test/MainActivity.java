package cn.bmob.socialshare.test;

import cn.bmob.social.share.core.BMShareListener;
import cn.bmob.social.share.core.ErrorInfo;
import cn.bmob.social.share.core.data.BMPlatform;
import cn.bmob.social.share.core.data.ShareData;
import cn.bmob.social.share.view.BMShare;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_openShare:
			testShare();
			break;

		default:
			break;
		}
	}

	private void testShare() {
		// ShareData使用内容分享类型分享类型
		ShareData shareData = new ShareData();
		shareData.setTitle("Bmob分享");
		shareData.setDescription("Bmob社会化分享功能");
		shareData.setText("Bmob提供的多平台社会化分享功能，目前支持QQ、QQ空间、微信、微信朋友圈、腾讯微博、新浪微博、人人网平台的分享功能。 ");
		shareData.setTarget_url("http://www.codenow.cn/");
		shareData.setImageUrl("http://assets3.chuangyepu.com/system/startup_contents/logos/000/003/395/medium/data.jpeg");
		
		BMShareListener whiteViewListener = new BMShareListener() {

			@Override
			public void onSuccess() {
				tost("分享成功");
			}

			@Override
			public void onPreShare() {
				tost("开始分享");
			}

			@Override
			public void onError(ErrorInfo error) {
				tost("分享失败"+error.getErrorMessage());
			}

			@Override
			public void onCancel() {
				tost("取消分享");
			}

		};
		
		BMShare share = new BMShare(this);
		share.setShareData(shareData);
		share.addListener(BMPlatform.PLATFORM_WECHAT, whiteViewListener);
		share.addListener(BMPlatform.PLATFORM_WECHATMOMENTS, whiteViewListener);
		share.addListener(BMPlatform.PLATFORM_SINAWEIBO, whiteViewListener);
		share.addListener(BMPlatform.PLATFORM_RENN, whiteViewListener);
		share.addListener(BMPlatform.PLATFORM_TENCENTWEIBO, whiteViewListener);
		share.addListener(BMPlatform.PLATFORM_QQ, whiteViewListener);
		share.addListener(BMPlatform.PLATFORM_QZONE, whiteViewListener);
		share.show();
		Log.d("bmob", "分享end");
	}
	
	private void tost(String msg){
		Log.d("bmob", msg);
		Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
	}

}
