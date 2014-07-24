package cn.bmob.social.share.view;

import java.util.ArrayList;

import cn.bmob.social.share.core.BMCore;
import cn.bmob.social.share.core.data.BMPlatform;
import cn.bmob.social.share.core.data.ShareData;
import cn.bmob.social.share.core.util.BMLog;
import cn.bmob.social.share.core.util.Util;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class ListPopup extends PopupWindow implements OnItemClickListener{
	
	/** 主分享界面实例 */
	protected static ListPopup instance;
	/**传入的activity*/
	protected static Activity act;
	private BMShare bmShare;
	private ShareData shareData;
	private ListPopupAdapter adapter;
	/**用于判断分享页面是否正在运行*/
	private ArrayList<String> enList = new ArrayList<String>();
	
	public ListPopup(Activity act,BMShare bmShare, ShareData shareData){
		this.act = act;
		this.bmShare = bmShare;
		this.shareData = shareData;
		instance = this;
		
		enList.add(BMPlatform.getPlatfornName(BMPlatform.PLATFORM_WECHAT));
		enList.add(BMPlatform.getPlatfornName(BMPlatform.PLATFORM_WECHATMOMENTS));
		enList.add(BMPlatform.getPlatfornName(BMPlatform.PLATFORM_TENCENTWEIBO));
		enList.add(BMPlatform.getPlatfornName(BMPlatform.PLATFORM_EMAIL));
		enList.add(BMPlatform.getPlatfornName(BMPlatform.PLATFORM_MESSAGE));
		enList.add(BMPlatform.getPlatfornName(BMPlatform.PLATFORM_COPYLINK));
		enList.add(BMPlatform.getPlatfornName(BMPlatform.PLATFORM_MORE_SHARE));
		enList.add(BMPlatform.getPlatfornName(BMPlatform.PLATFORM_SINAWEIBO));
		enList.add(BMPlatform.getPlatfornName(BMPlatform.PLATFORM_RENN));
		enList.add(BMPlatform.getPlatfornName(BMPlatform.PLATFORM_QQ));
		enList.add(BMPlatform.getPlatfornName(BMPlatform.PLATFORM_QZONE));
		
	}
	
	public void show(){
		LinearLayout mainLinear = new LinearLayout(act);
		mainLinear.setOrientation(LinearLayout.VERTICAL);
		
		LinearLayout.LayoutParams mainLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, Util.dip2px(act, 350));
		mainLinear.setLayoutParams(mainLayoutParams);
		
		
		LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, Util.dip2px(act, 48));
		TextView titleView = new TextView(act);
		titleView.setText("请选择分享平台");
		titleView.setLayoutParams(titleParams);
		titleView.setGravity(Gravity.CENTER);
		titleView.setTextSize(14);
		titleView.setTextColor(Color.parseColor("#9BC7E4"));
//		titleView.setBackgroundColor(Color.CYAN);
		
		ListView listView = new ListView(act);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		listView.setLayoutParams(layoutParams);
		
		adapter = new ListPopupAdapter(act, enList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		
		LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1);
		
		View view = new View(act);
		View view2 = new View(act);
		view.setBackgroundColor(Color.GRAY);
		view2.setBackgroundColor(Color.GRAY);
		view.setLayoutParams(viewParams);
		view2.setLayoutParams(viewParams);
		
		mainLinear.addView(view);
		mainLinear.addView(titleView);
		mainLinear.addView(view2);
		mainLinear.addView(listView);
		
		// 设置popupwindow的属
		setFocusable(true);
		setOutsideTouchable(true);
//		setBackgroundDrawable(BMCore.res.getDrawable(BMCore.res.getIdentifier("yt_side", "drawable", BMCore.packName)));
		setBackgroundDrawable(new BitmapDrawable());
		setContentView(mainLinear);
		setWidth(act.getWindowManager().getDefaultDisplay().getWidth());
		setHeight(Util.dip2px(act, 350));
		setAnimationStyle(android.R.style.Animation_InputMethod);
		
		showAtLocation(getContentView(), Gravity.BOTTOM, 0, 0);
//		BMLog.d("显示pop窗口");
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (Util.isNetworkConnected(act)) {
			this.bmShare.doListShare(BMPlatform.getBMPlatformByName(enList.get(position)), shareData);
			dismiss();
		} else {
			Toast.makeText(act, "无网络连接，请查看您的网络情况", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	/**
	 * 关闭分享界面
	 */
	@Override
	public void dismiss() {
		super.dismiss();
	}
	/**关闭 分享界面*/
	public static void  close(){
		if(instance!= null){
			instance.dismiss();
		}
	}
}
