package cn.bmob.social.share.view;

import java.util.HashMap;

import android.app.Activity;
import cn.bmob.social.share.core.BMCore;
import cn.bmob.social.share.core.BMShareListener;
import cn.bmob.social.share.core.data.BMPlatform;
import cn.bmob.social.share.core.data.ShareData;
import cn.bmob.social.share.core.util.BMLog;

public class BMShare {
	private Activity act;
	
	private HashMap<BMPlatform, BMShareListener> listenerMap = new HashMap<BMPlatform, BMShareListener>();
	private HashMap<BMPlatform, ShareData> shareDataMap = new HashMap<BMPlatform, ShareData>();
	
	private ShareData shareData;
	
	public BMShare(Activity act){
		this.act = act;
		BMCore.init(act);
	}
	
	/**
	 * 为单独的平台添加分享数据
	 * @param platform
	 * @param shareData
	 */
	public void addData(BMPlatform platform, ShareData shareData) {
		shareDataMap.put(platform, shareData);
	}
	/**
	 * 获取指定平台的分享信息
	 * @param platform
	 * @return 指定平台的分享信息
	 */
	public ShareData getData(BMPlatform platform){
		return shareDataMap.get(platform);
	}
	/**
	 * 添加分享监听
	 * @param platform
	 * @param listener
	 */
	public void addListener(BMPlatform platform, BMShareListener listener) {
		BMLog.d("**1*** "+platform+" --- "+listener);
		listenerMap.put(platform, listener);
	}
	/**
	 * 获得监听事件
	 * @param platform
	 * @return 监听事件
	 */
	public BMShareListener getListener(BMPlatform platform) {
		return listenerMap.get(platform);
	}
	
	/**调出分享界面*/
	public void show(){
		new ListPopup(act, this, shareData).show();
	}
	
	/**
	 * 该方法用于设置所有平台的待分享数据,如果开发者没有使用addData(YtPlatform platform, ShareData shareData)方法为特定平台设置待分享数据,则平台分享的内容为此处设置的内容
	 * @param shareData
	 */
	public void setShareData(ShareData shareData){
		this.shareData = shareData;
	}
	
	/**
	 * 分享到各个平台, 用于白色列表样式的listview
	 * 
	 * @param position
	 */
	public void doListShare(BMPlatform bmPlatform, ShareData shareData){
		switch (bmPlatform) {
			case PLATFORM_WECHAT:
				// 分享到微信
				// 判断是否有单独设置该平台的分享内容
				if(getData(BMPlatform.PLATFORM_WECHAT)!=null){
					BMCore.getInstance().share(act, BMPlatform.PLATFORM_WECHAT, getListener(BMPlatform.PLATFORM_WECHAT), getData(BMPlatform.PLATFORM_WECHAT));
				}else {
					BMCore.getInstance().share(act, BMPlatform.PLATFORM_WECHAT, getListener(BMPlatform.PLATFORM_WECHAT), shareData);
				}
				break;
			case PLATFORM_WECHATMOMENTS:
				BMLog.d("***** "+listenerMap.size());
				BMLog.d("***** "+getListener(BMPlatform.PLATFORM_WECHATMOMENTS));
				// 分享到微信朋友圈
				if(getData(BMPlatform.PLATFORM_WECHATMOMENTS)!=null){
					BMCore.getInstance().share(act, BMPlatform.PLATFORM_WECHATMOMENTS, getListener(BMPlatform.PLATFORM_WECHATMOMENTS),getData(BMPlatform.PLATFORM_WECHATMOMENTS));
				}else{
					BMCore.getInstance().share(act, BMPlatform.PLATFORM_WECHATMOMENTS, getListener(BMPlatform.PLATFORM_WECHATMOMENTS),shareData);
				}
				break;
			case PLATFORM_QQ:
				// 分享到QQ
				if(getData(BMPlatform.PLATFORM_QQ)!=null){
					BMCore.getInstance().share(act, BMPlatform.PLATFORM_QQ, getListener(BMPlatform.PLATFORM_QQ), getData(BMPlatform.PLATFORM_QQ));
				}else{
					BMCore.getInstance().share(act, BMPlatform.PLATFORM_QQ, getListener(BMPlatform.PLATFORM_QQ), shareData);
				}
				break;
			case PLATFORM_QZONE:
				// 分享到QQ空间
				if(getData(BMPlatform.PLATFORM_QZONE)!=null){
					BMCore.getInstance().share(act, BMPlatform.PLATFORM_QZONE, getListener(BMPlatform.PLATFORM_QZONE), getData(BMPlatform.PLATFORM_QZONE));
				}else{
					BMCore.getInstance().share(act, BMPlatform.PLATFORM_QZONE, getListener(BMPlatform.PLATFORM_QZONE), shareData);
				}
				break;
			case PLATFORM_TENCENTWEIBO:
				// 分享到腾讯微博
				if(getData(BMPlatform.PLATFORM_TENCENTWEIBO)!=null){
					BMCore.getInstance().share(act, BMPlatform.PLATFORM_TENCENTWEIBO, getListener(BMPlatform.PLATFORM_TENCENTWEIBO), getData(BMPlatform.PLATFORM_TENCENTWEIBO));
				}else{
					BMCore.getInstance().share(act, BMPlatform.PLATFORM_TENCENTWEIBO, getListener(BMPlatform.PLATFORM_TENCENTWEIBO), shareData);
				}
				break;
			case PLATFORM_SINAWEIBO:
				// 分享到新浪微博
				if(getData(BMPlatform.PLATFORM_SINAWEIBO)!=null){
					BMCore.getInstance().share(act, BMPlatform.PLATFORM_SINAWEIBO, getListener(BMPlatform.PLATFORM_SINAWEIBO), getData(BMPlatform.PLATFORM_SINAWEIBO));
				}else{
					BMCore.getInstance().share(act, BMPlatform.PLATFORM_SINAWEIBO, getListener(BMPlatform.PLATFORM_SINAWEIBO), shareData);
				}
				break;
			case PLATFORM_RENN:
				// 分享到人人网
				if(getData(BMPlatform.PLATFORM_RENN)!=null){
					BMCore.getInstance().share(act, BMPlatform.PLATFORM_RENN, getListener(BMPlatform.PLATFORM_RENN), getData(BMPlatform.PLATFORM_RENN));
				}else{
					BMCore.getInstance().share(act, BMPlatform.PLATFORM_RENN, getListener(BMPlatform.PLATFORM_RENN), shareData);
				}
				break;
			case PLATFORM_EMAIL:
				// 分享到邮箱
				if(getData(BMPlatform.PLATFORM_EMAIL)!=null){
					BMCore.getInstance().share(act, BMPlatform.PLATFORM_EMAIL, getListener(BMPlatform.PLATFORM_EMAIL), getData(BMPlatform.PLATFORM_EMAIL));
				}else{
					BMCore.getInstance().share(act, BMPlatform.PLATFORM_EMAIL, getListener(BMPlatform.PLATFORM_EMAIL), shareData);
				}
				break;
			case PLATFORM_MESSAGE:
				// 分享到短信
				if(getData(BMPlatform.PLATFORM_MESSAGE)!=null){
					BMCore.getInstance().share(act, BMPlatform.PLATFORM_MESSAGE, getListener(BMPlatform.PLATFORM_MESSAGE), getData(BMPlatform.PLATFORM_MESSAGE));
				}else{
					BMCore.getInstance().share(act, BMPlatform.PLATFORM_MESSAGE, getListener(BMPlatform.PLATFORM_MESSAGE), shareData);
				}
				break;
			case PLATFORM_COPYLINK:
				// 复制链接
				if(getData(BMPlatform.PLATFORM_COPYLINK)!=null){
					BMCore.getInstance().share(act, BMPlatform.PLATFORM_COPYLINK, getListener(BMPlatform.PLATFORM_COPYLINK), getData(BMPlatform.PLATFORM_COPYLINK));
				}else{
					BMCore.getInstance().share(act, BMPlatform.PLATFORM_COPYLINK, getListener(BMPlatform.PLATFORM_COPYLINK), shareData);
				}
				break;
			case PLATFORM_MORE_SHARE:
				// 更多分享
				if(getData(BMPlatform.PLATFORM_MORE_SHARE)!=null){
					BMCore.getInstance().share(act, BMPlatform.PLATFORM_MORE_SHARE, getListener(BMPlatform.PLATFORM_MORE_SHARE), getData(BMPlatform.PLATFORM_MORE_SHARE));
				}else{
					BMCore.getInstance().share(act, BMPlatform.PLATFORM_MORE_SHARE, getListener(BMPlatform.PLATFORM_MORE_SHARE), shareData);
				}
				break;
	
			default:
				break;
		}
	}
}
