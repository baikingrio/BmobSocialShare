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
//		// ShareData使用内容分享类型分享类型
		ShareData shareData = new ShareData();
//		shareData.isAppShare = false;
		shareData.setTitle("滴滴打车分享");
		shareData.setDescription("抢滴滴红包，赶回家看球~");
		shareData.setText("滴滴打车给我11个红包，分你1个，打车回家看球吧。 ");
		shareData.setTarget_url("http://www.xiaojukeji.com/");
		shareData.setImageUrl("http://file.bmob.cn/M00/02/B4/wKhkA1OuzHeATJ5KAAAMie-ZImI961.jpg");
//		shareData.setImagePath(Environment.getExternalStorageDirectory()+"/youtui/default.png");
		BMShareListener whiteViewListener = new BMShareListener() {

			@Override
			public void onSuccess() {
//				YtLog.e("--onSuccess--", error.getErrorMessage());
				Log.d("bmob", "分享成功");
				Toast.makeText(MainActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onPreShare() {
				Log.d("bmob", "开始分享");
				Toast.makeText(MainActivity.this, "开始分享", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onError(ErrorInfo error) {
//				YtLog.e("--onError--", error.getErrorMessage());
				Log.d("bmob", "分享失败"+error.getErrorMessage());
				Toast.makeText(MainActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCancel() {
				Log.d("bmob", "取消分享");
				Toast.makeText(MainActivity.this, "取消分享", Toast.LENGTH_SHORT).show();
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

}
