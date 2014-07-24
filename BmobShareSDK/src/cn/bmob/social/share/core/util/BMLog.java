package cn.bmob.social.share.core.util;

import android.util.Log;

public class BMLog {
	private static String TAG = "bmob";
	public static boolean showLog = true;
	
	public static void i(String msg){
		if(showLog){
			Log.i(TAG, msg);
		}
	}
	
	public static void d(String msg){
		if(showLog){
			Log.d(TAG, msg);
		}
	}
	
	public static void e(String msg){
		if(showLog){
			Log.e(TAG, msg);
		}
	}
}
