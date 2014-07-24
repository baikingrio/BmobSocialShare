package cn.bmob.social.share.core.util;

import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.widget.Toast;

public class Util {
	
	private static ProgressDialog mProgressDialog;
	
	/**
	 * 显示ProgressDialog
	 */
	public static final void showProgressDialog(final Activity act, String message,final boolean isFinishActivity) {
		dismissDialog();
		mProgressDialog = new ProgressDialog(act);
		// 设置进度条风格，风格为圆形，旋转的
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// 设置ProgressDialog 提示信息
		mProgressDialog.setMessage(message);
		// 设置ProgressDialog 的进度条是否不明确
		mProgressDialog.setIndeterminate(false);
		// 设置ProgressDialog 是否可以按退回按键取消
		mProgressDialog.setCancelable(true);
		mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				if(isFinishActivity){
					act.finish();
				}				
			}
		});
		mProgressDialog.show();
	}
	/**
	 * dismiss ProgressDialog
	 */
	public static final void dismissDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}
	
	/**
	 * 判断网络是否连接
	 * 
	 * @param context
	 * @return
	 */
	public static Boolean isNetworkConnected(Context context) {
		ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conn.getActiveNetworkInfo();
		if (info != null&&info.isAvailable()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 复制链接 复制链接 API 11之前用android.text.ClipboardManager; API
	 * 11之后用android.content.ClipboardManager
	 * 
	 * @param mHandler
	 * @param act
	 * @param message
	 */
	public static void copyLink(Handler mHandler, final Context act, final String message) {
		mHandler.post(new Runnable() {
			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi")
			public void run() {
				if (android.os.Build.VERSION.SDK_INT >= 11) {
					android.content.ClipboardManager clip = (android.content.ClipboardManager) act.getSystemService(Context.CLIPBOARD_SERVICE);
					clip.setPrimaryClip(android.content.ClipData.newPlainText("link", message));
					if (clip.hasPrimaryClip()) {
						Toast.makeText(act, "复制成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(act, "复制失败，请手动复制", Toast.LENGTH_SHORT).show();
					}
				} else {
					android.text.ClipboardManager clip = (android.text.ClipboardManager) act.getSystemService(Context.CLIPBOARD_SERVICE);
					clip.setText(message);
					if (clip.hasText()) {
						Toast.makeText(act, "复制成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(act, "复制失败，请手动复制", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}
	
	/**
	 * dp to px
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * px to dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	/**
	 * 读取Assets文件夹中的图片资源 
	 * @param context 
	 * @param fileName 图片名称 
	 * @return 
	 */ 
	public static Bitmap getImageFromAssetsFile(Context context, String fileName) {   
	    Bitmap image = null;   
	    AssetManager am = context.getAssets();   
	    try {   
	        InputStream is = am.open(fileName);   
	        image = BitmapFactory.decodeStream(is);   
	        is.close();   
	    } catch (IOException e) {   
	        e.printStackTrace();   
	    }   
	    return image;   
	}
	
}
