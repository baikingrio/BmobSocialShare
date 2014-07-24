package cn.bmob.social.share.view;

import java.util.ArrayList;

import junit.framework.Test;
import cn.bmob.social.share.core.util.Util;
import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ListPopupAdapter extends BaseAdapter {
	
	private Activity act;
	private ArrayList<String> list;
	
	public ListPopupAdapter(Activity act, ArrayList<String> list){
		this.act = act;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		if (convertView == null) {
			// LayoutInflater.from(act).inflate(YtCore.res.getIdentifier("yt_sharelist_item", "layout", YtCore.packName), null);
			LinearLayout mainLinear = new LinearLayout(act);
			mainLinear.setOrientation(LinearLayout.HORIZONTAL);
			mainLinear.setPadding(0, 4, 0, 4);
			mainLinear.setGravity(Gravity.CENTER_VERTICAL);
			
			ImageView imageView = new ImageView(act);
			imageView.setId(888);
			LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(Util.dip2px(act, 40), Util.dip2px(act, 40));
			imageParams.leftMargin = 15;
			
			TextView textView = new TextView(act);
			textView.setId(999);
			textView.setPadding(18, 0, 0, 0);
			textView.setTextSize(14);
			LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			
			mainLinear.addView(imageView, imageParams);
			mainLinear.addView(textView, textViewParams);
			
			convertView = mainLinear;
			
		}
		
		ImageView imageView = (ImageView) convertView.findViewById(888);
		TextView textView = (TextView) convertView.findViewById(999);
		
		// 设置社交平台logo
		imageView.setImageResource(ShareList.getLogo(list.get(position), act));
//		imageView.setImageBitmap(Util.getImageFromAssetsFile(act, "yt_more.png"));
//		imageView.setImageBitmap(Util.getImageFromAssetsFile(act, "yt_left_arrow.png"));
		// 积分textview
		textView.setText(ShareList.getTitle(list.get(position)));
		
		return convertView;
	}

}
